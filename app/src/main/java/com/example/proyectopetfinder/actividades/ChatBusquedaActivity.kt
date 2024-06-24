package com.example.proyectopetfinder.actividades

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.adaptadores.ChatAdapter
import com.example.proyectopetfinder.databinding.ActivityChatBusquedaBinding
import com.example.proyectopetfinder.poko.Chat
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.IOException

class ChatBusquedaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBusquedaBinding
    private lateinit var dataBase: DatabaseReference
    private lateinit var database2: DatabaseReference
    private var remitente = ""
    private var destino = ""
    private var fotoMascota = ""
    private var imagen : String = ""
    val mensajesList = mutableListOf<Chat>()
    private lateinit var chatAdapter: ChatAdapter
    private val PICK_IMAGE_REQUEST = 1
    private var extraviado= PublicacionExtravio()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBusquedaBinding.inflate(layoutInflater)
        dataBase = Firebase.database.reference
        cargarDatosChat()
        database2 = com.google.firebase.Firebase.database.getReference("PublicacionesExtraviado").child("PublicacionExt${extraviado.idExtraviado}")
        setContentView(binding.root)
        cargarImagen()
        configurarRecyclerChat()
        cargarDatosFirebase()

        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.btnEnviar.setOnClickListener {
            if(validarCampo()){
                val contMensaje = binding.etMensaje.text.toString()
                val chat = Chat(remitente,destino,contMensaje)
                val noChats = mensajesList.size
                enviarMensaje(noChats,chat)
            }
        }

        binding.btnImagen.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST)
        }

        binding.btnImagenSeleccionada.setOnClickListener{
            limpiarImagen()
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarChat(mensajesList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imagen = bitmapToString(bitmap)
                binding.btnImagenSeleccionada.setImageBitmap(bitmap)
                binding.llImagen.visibility = View.VISIBLE
                binding.btnImagen.visibility = View.INVISIBLE

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun cargarDatosChat(){
        remitente = intent.getStringExtra("usuario")!!
        destino = intent.getStringExtra("destino")!!
        extraviado.idExtraviado = intent.getLongExtra("idPublicacion",0)!!
        binding.tvDueODe.text = binding.tvDueODe.text.toString().plus(destino)
    }

    fun cargarImagen(){
        database2.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                fotoMascota=snapshot.child("Foto").value.toString()
                val id = snapshot.child("IdExtraviado").value.toString()
                binding.ivImagen.setImageBitmap(stringToBitmap(fotoMascota))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatBusquedaActivity,"Error al acceder a la base datos", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun validarCampo():Boolean{
        var validos = false
        if(binding.etMensaje.length() > 0){
            validos = true
        }else{
            binding.etMensaje.setError("Campo vacio.")
        }
        return validos
    }

    fun cargarDatosFirebase(){
        dataBase.child("Mensajes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mensajesList.clear() // Limpiar la lista antes de agregar nuevos mensajes
                for (data in snapshot.children) {
                    val mensaje = data.getValue(Chat::class.java)
                    if (mensaje != null) {
                        // Filtrar mensajes por origen y destino
                        if (mensaje.Destino == destino) {
                            if(mensaje.Origen == remitente){
                                mensaje.Origen = "Yo"
                            }
                            if (!mensajesList.contains(mensaje)) { // Verificar si el mensaje ya existe en la lista
                                mensajesList.add(mensaje)
                            }
                        }
                    }
                }
                // Aquí puedes utilizar la lista mensajesList como necesites, por ejemplo, actualizar la UI
                actualizarChat(mensajesList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun enviarMensaje(key: Int, chat: Chat){
        var idMensaje = "Mensaje"+chat.Destino+chat.Origen+key
        dataBase.child("Mensajes").child(idMensaje).child("Contenido").setValue(chat.Contenido)
        dataBase.child("Mensajes").child(idMensaje).child("Destino").setValue(chat.Destino)
        dataBase.child("Mensajes").child(idMensaje).child("Origen").setValue(chat.Origen)
        if(imagen != ""){
            dataBase.child("Mensajes").child(idMensaje).child("Imagen").setValue(imagen)
            limpiarImagen()
        }
        binding.etMensaje.setText("")
    }

    fun limpiarImagen(){
        imagen = ""
        binding.llImagen.visibility = View.INVISIBLE
        binding.btnImagen.visibility = View.VISIBLE
    }

    fun actualizarChat(mensajes: List<Chat>) {
        binding.recycleNotas.visibility = View.VISIBLE
        chatAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    private fun configurarRecyclerChat(){
        chatAdapter = ChatAdapter(mensajesList)
        binding.recycleNotas.layoutManager = LinearLayoutManager(this@ChatBusquedaActivity)
        binding.recycleNotas.setHasFixedSize(true)
        binding.recycleNotas.adapter = chatAdapter
    }

    fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // Comprimir el bitmap al formato deseado (en este caso JPEG con calidad 100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }
}