package com.example.proyectopetfinder.actividades

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityChatsBinding
import com.example.proyectopetfinder.poko.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatsActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatsBinding
    private lateinit var dataBase: DatabaseReference
    val usuarios = mutableListOf<String>()
    val noMensajes = mutableListOf<Int>()
    var noMensajesGuardados = mutableListOf<Int>()

    private var usuario = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        dataBase = Firebase.database.reference
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.rojo)

        usuario = intent.getStringExtra("Nombre").toString()
        cargarDatosFirebase()
    }

    private fun configurarListView(chats: MutableList<String>) {
        val chatsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chats)

        for (i in chats.indices) {
            val chat = chats[i]
            val posicion = usuarios.indexOf(chat)
            /*if (posicion != -1 && noMensajes[posicion] > noMensajesGuardados[posicion]) {
                chats[i] = chat + " ¡¡NUEVO MENSAJE!!"
            } else if (chat.endsWith(" ¡¡NUEVO MENSAJE!!")) {
                chats[i] = chat.substring(0, chat.length - " ¡¡NUEVO MENSAJE!!".length)
            }*/
        }

        binding.lvChats.adapter = chatsAdapter
        binding.lvChats.setOnItemClickListener { _, _, i, _ ->
            var usuario2 = chats[i]
            if (usuario2.endsWith(" ¡¡NUEVO MENSAJE!!")) {
                usuario2 = usuario2.substring(0, usuario2.length - " ¡¡NUEVO MENSAJE!!".length)
            }
            abrirConversacion(usuario2)
        }
    }

    private fun cargarDatosFirebase() {
        dataBase.child("Mensajes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usuarios.clear()
                noMensajes.clear()

                for (data in snapshot.children) {
                    val mensaje = data.getValue(Chat::class.java)
                    if (mensaje != null && mensaje.Destino == usuario) {
                        val noUsuario = usuarios.indexOf(mensaje.Origen)
                        if (noUsuario != -1) {
                            noMensajes[noUsuario] = noMensajes[noUsuario] + 1
                        } else {
                            usuarios.add(mensaje.Origen)
                            noMensajes.add(1)
                        }
                    }
                }
                noMensajesGuardados = cargarNoMensajes(usuarios)
                configurarListView(usuarios)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo del error
            }
        })
    }

    private fun abrirConversacion(usuario2: String) {
        val intent = Intent(this, ChatDuenioActivity::class.java)
        intent.putExtra("usuario", usuario)
        intent.putExtra("destino", usuario2)
        startActivity(intent)

        val index = usuarios.indexOf(usuario2)
        if (index != -1) {
            noMensajesGuardados[index] = noMensajes[index]
            guardarNoMensajes(usuarios, noMensajesGuardados)
        }
    }

    override fun onResume() {
        super.onResume()
        noMensajesGuardados = cargarNoMensajes(usuarios)
        configurarListView(usuarios)
    }

    private fun cargarNoMensajes(usuarios: List<String>): MutableList<Int> {
        val lista = mutableListOf<Int>()
        val preferenciasUno = getSharedPreferences("noMensajes", Context.MODE_PRIVATE)
        for (usuario in usuarios) {
            lista.add(preferenciasUno.getInt(usuario, 0))
        }
        return lista
    }

    private fun guardarNoMensajes(lista: List<String>, lista2: List<Int>) {
        val preferencias = getSharedPreferences("noMensajes", Context.MODE_PRIVATE)
        with(preferencias.edit()) {
            for (usuario in lista) {
                putInt(usuario, lista2[lista.indexOf(usuario)])
            }
            apply()
        }
    }
}
