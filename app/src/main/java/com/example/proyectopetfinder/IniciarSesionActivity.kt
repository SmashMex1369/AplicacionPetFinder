package com.example.proyectopetfinder

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.databinding.ActivityIniciarSesionBinding
import com.example.proyectopetfinder.poko.Chat
import com.example.proyectopetfinder.poko.Usuario
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class IniciarSesionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIniciarSesionBinding
    private lateinit var dataBase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        dataBase = Firebase.database.reference
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.btnIniciarSesion.setOnClickListener {

            if(validarCampos()){
                buscarUsuario()
            }


        }
    }

    fun iniciarSesion(usuarioNombre : String){
        val intent = Intent(this,MainPageActivity::class.java)
        intent.putExtra("usuario",usuarioNombre)
        startActivity(intent)
    }

    fun buscarUsuario(){
        dataBase.child("Usuarios").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Limpiar la lista antes de agregar nuevos mensajes
                for (data in snapshot.children) {
                    val usuario = data.getValue(Usuario::class.java)
                    if (usuario != null) {
                        // Filtrar mensajes por origen y destino
                        if ((usuario.Direccion == binding.etCorreo.text.toString()) &&
                                (usuario.Constraneia == binding.etContraseA.text.toString()) ){
                            iniciarSesion(usuario.Nombre)
                        }
                    }
                }
                Toast.makeText(this@IniciarSesionActivity,"Usuario y/o Contraseña no validos",Toast.LENGTH_LONG)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun validarCampos():Boolean{
        var boolean : Boolean = true
        if(binding.etCorreo.length()==0){
            binding.etCorreo.setError("Campo vacio")
            boolean = false
        }

        if(binding.etContraseA.length()==0){
            binding.etContraseA.setError("Campo vacio")
            boolean = false
        }

        return boolean
    }


}