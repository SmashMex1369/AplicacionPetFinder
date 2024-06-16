package com.example.proyectopetfinder

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

class ChatDuenioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatDuenioBinding
    private lateinit var dataBase: DatabaseReference
    private var remitente = ""
    private var destino = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDuenioBinding.inflate(layoutInflater)
        dataBase = Firebase.database.reference
        setContentView(binding.root)

        cargarDatosFirebase()
        //cargarDatosChat()

        window.statusBarColor = Color.parseColor(R.color.rojo.toString())
        window.navigationBarColor = Color.parseColor(R.color.rojo.toString())

        binding.btnEnviar.setOnClickListener {

        }

        binding.btnImagen.setOnClickListener {

        }
    }

    fun cargarDatosChat(){
        remitente = intent.getStringExtra("usuario")!!
        destino = intent.getStringExtra("destino")!!
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
        dataBase.child("mensajes").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val mensajesList = mutableListOf<Chat>()
                for (data in snapshot.children) {
                    val mensaje = data.getValue(Chat::class.java)
                    if (mensaje != null) {
                        // Filtrar mensajes por origen y destino
                        if ((mensaje.origen == remitente && mensaje.destino == destino) ||
                            (mensaje.origen == destino && mensaje.destino == remitente)) {
                            mensajesList.add(mensaje)
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

    fun actualizarChat(mensajes: List<Chat>) {
        binding.recycleNotas.visibility = View.VISIBLE
        //Con eso ya jalan los
        binding.recycleNotas.adapter = ChatAdapter(mensajes)
    }
}