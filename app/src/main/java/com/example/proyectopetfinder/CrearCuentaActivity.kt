package com.example.proyectopetfinder

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.databinding.ActivityCrearCuentaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CrearCuentaActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCrearCuentaBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearCuentaBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        database = Firebase.database.getReference("Usuarios")
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.btnRegistrar.setOnClickListener {
            
            if (validarCampos()){
                val nombre=binding.etUsuario.text.toString()
                val correo=binding.etCorreo.text.toString()
                val contrasena=binding.etContraseA.text.toString()
                val telefono=binding.etCelular.text.toString()
                agrearUsuarioFirebase(nombre,correo,contrasena,telefono)
            }

        }

    }

    fun agrearUsuarioFirebase(nombre:String, correo:String, contrasena:String, telefono:String){
        var idUsuario = 0
        database.child("IdUsuario").get().addOnSuccessListener {dataSnapshot ->
            if(dataSnapshot.exists()){
                idUsuario = dataSnapshot.getValue() as Int
            }
        }

    }


    fun validarCampos():Boolean{
        var valido = true
        if(binding.etUsuario.text.isEmpty()){
            binding.etUsuario.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }
        if(binding.etCorreo.text.isEmpty()){
            binding.etCorreo.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }
        if(binding.etContraseA.text?.isEmpty() == true){
            binding.etContraseA.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }
        if(binding.etCelular.text.isEmpty()){
            binding.etCelular.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }

        return valido
    }
}