package com.example.proyectopetfinder.actividades

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityChatsBinding
import com.example.proyectopetfinder.poko.Chat
import com.example.proyectopetfinder.poko.UltimoChat
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
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        usuario = intent.getStringExtra("Nombre").toString()
        cargarDatosFirebase()

    }


    fun configurarListView(chats : MutableList<String>){
        val chatsAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,
            chats)

        for(chat in chats){
            var posicion = usuarios.indexOf(chat)
            if(noMensajes.get(posicion) > noMensajesGuardados.get(posicion) ){
                chats.set(posicion,chat + "!!NUEVO MENSAJE¡¡")
            }
        }
        binding.lvChats.adapter = chatsAdapter
        binding.lvChats.setOnItemClickListener { adapterView, view, i, l ->
            var usuario2 = chats.get(i)
            if(usuario2.endsWith("!!NUEVO MENSAJE¡¡")){
                usuario2 = usuario2.substring(0,usuario2.length-"!!NUEVO MENSAJE¡¡".length)
            }
            abrirConversacion(usuario2)
        }
    }

    fun cargarDatosFirebase(){
        dataBase.child("Mensajes").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (data in snapshot.children) {
                    val mensaje = data.getValue(Chat::class.java)
                    if (mensaje != null) {
                        // Filtrar mensajes por origen y destino
                        if (mensaje.Destino == usuario) {
                            if(usuarios.contains(mensaje.Origen)){
                                val noUsuario = usuarios.indexOf(mensaje.Origen)
                                noMensajes.set(noUsuario,noMensajes.get(noUsuario)+1)
                            }else{
                                usuarios.add(mensaje.Origen)
                                noMensajes.add(1)
                            }
                        }
                    }


                }
                // Aquí puedes utilizar la lista mensajesList como necesites, por ejemplo, actualizar la UI
                noMensajesGuardados = cargarNoMensajes(usuarios)
                configurarListView(usuarios)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun abrirConversacion(usuario2 : String){
        guardarNoMensajes(usuarios,noMensajes)
        val intent = Intent(this,ChatDuenioActivity::class.java)
        intent.putExtra("usuario",usuario)
        intent.putExtra("destino",usuario2)
        startActivity(intent)
    }

    fun cargarNoMensajes(usuarios: List<String>): MutableList<Int> {
        val lista = mutableListOf<Int>()
        val preferenciasUno = getSharedPreferences("noMensajes", Context.MODE_PRIVATE)
        for (usuario in usuarios){
            lista.add(preferenciasUno.getInt(usuario.toString(),0) )
        }
        return lista
    }

    fun guardarNoMensajes(lista : List<String>, lista2 : List<Int>){
        val archPreferenciasDefault = getPreferences(Context.MODE_PRIVATE)
        val preferenciasUno = getSharedPreferences("noMensajes", Context.MODE_PRIVATE) //Para llamar a otro prefernces que no sea el default
        with(preferenciasUno.edit()){//Te crea automaticamente una variable en la cual trabajas en los
            //parentesis. Sirve para no necesitar crear una variable y llamar sus datos con '.'
            for (usuario in lista){
                putInt(usuario,lista2.get( lista.indexOf(usuario) ))
            }
            //.commit() sirve para el apply pero detiene el programa
            apply()
        }
    }
}