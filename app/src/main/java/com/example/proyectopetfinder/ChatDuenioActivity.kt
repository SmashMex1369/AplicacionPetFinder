package com.example.proyectopetfinder

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.databinding.ActivityChatDuenioBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.proyectopetfinder.poko.Chat
import com.example.proyectopetfinder.adaptadores.ChatAdapter
import com.google.firebase.database.getValue

class ChatDuenioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDuenioBinding
    private lateinit var dataBase: DatabaseReference
    private var remitente = ""
    private var destino = ""
    val mensajesList = mutableListOf<Chat>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDuenioBinding.inflate(layoutInflater)
        dataBase = Firebase.database.reference
        setContentView(binding.root)

        cargarDatosChat()
        configurarRecyclerChat()
        cargarDatosFirebase()

        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
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

        }
    }

    override fun onResume() {
        super.onResume()
        actualizarChat(mensajesList)
    }

    fun cargarDatosChat(){
        remitente = intent.getStringExtra("usuario")!!
        destino = intent.getStringExtra("destino")!!
        binding.tvDueODe.text = binding.tvDueODe.text.toString().plus(destino)
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
        dataBase.child("Mensajes").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mensajesList.clear() // Limpiar la lista antes de agregar nuevos mensajes
                for (data in snapshot.children) {
                    val mensaje = data.getValue(Chat::class.java)
                    if (mensaje != null) {
                        // Filtrar mensajes por origen y destino
                        if ((mensaje.Origen == remitente && mensaje.Destino == destino) ||
                            (mensaje.Origen == destino && mensaje.Destino == remitente)) {
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
        binding.etMensaje.setText("")
    }

    fun actualizarChat(mensajes: List<Chat>) {
        binding.recycleNotas.visibility = View.VISIBLE
        chatAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }

    private fun configurarRecyclerChat(){
        chatAdapter = ChatAdapter(mensajesList)
        binding.recycleNotas.layoutManager = LinearLayoutManager(this@ChatDuenioActivity)
        binding.recycleNotas.setHasFixedSize(true)
        binding.recycleNotas.adapter = chatAdapter
    }
}