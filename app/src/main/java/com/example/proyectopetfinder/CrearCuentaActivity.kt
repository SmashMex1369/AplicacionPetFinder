package com.example.proyectopetfinder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.databinding.ActivityCrearCuentaBinding
import com.example.proyectopetfinder.utilidades.Internet.tieneInternet
import com.example.proyectopetfinder.utilidades.Internet.perdioConexion
import com.google.android.material.snackbar.Snackbar
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
        habilitarCampos()
        database = Firebase.database.getReference("Usuarios")
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.btnRegistrar.setOnClickListener {
            
            if (validarCampos()){
                if (tieneInternet(this)){
                    deshabilitarCampos()
                    val nombre=binding.etUsuario.text.toString()
                    val correo=binding.etCorreo.text.toString()
                    val contrasena=binding.etContraseA.text.toString()
                    val telefono=binding.etCelular.text.toString()
                    agrearUsuarioFirebase(nombre,correo,contrasena,telefono)
                }else{
                    Toast.makeText(this, ContextCompat.getString(this,R.string.sin_conexion),Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this,"Rellene los campos faltantes",Toast.LENGTH_LONG).show()
            }

        }

    }

    fun agrearUsuarioFirebase(nombre:String, correo:String, contrasena:String, telefono:String){
        if (tieneInternet(this)){
            //importante
            var idUsuario = 0
            database.child("IdUsuario").get().addOnSuccessListener {dataSnapshot ->
                Toast.makeText(this,"Registranse, espere un momento ...",Toast.LENGTH_LONG).show()
                if(dataSnapshot.exists()){
                    idUsuario = dataSnapshot.getValue().toString().toInt()
                }
                //
                if (tieneInternet(this)){
                    database.child("Usuario"+idUsuario).child("Nombre").setValue(nombre)
                    database.child("Usuario"+idUsuario).child("Correo").setValue(correo)
                    database.child("Usuario"+idUsuario).child("Contraseña").setValue(contrasena)
                    database.child("Usuario"+idUsuario).child("Telefono").setValue(telefono)
                    database.child("IdUsuario").setValue(idUsuario+1) //importante
                    limpiarCampos()
                    Toast.makeText(this,"Cuenta registrada correctamente",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    perdioConexion(this)
                    habilitarCampos()
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(this,"Error al crear la cuenta, intente nuavemente más tarde",Toast.LENGTH_LONG).show()

            }
        }else{
            perdioConexion(this)
            habilitarCampos()
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

    fun deshabilitarCampos(){
        binding.btnRegistrar.isEnabled=false
        binding.etUsuario.isEnabled=false
        binding.etCorreo.isEnabled=false
        binding.etContraseA.isEnabled=false
        binding.etCelular.isEnabled=false
    }
    fun habilitarCampos(){
        binding.btnRegistrar.isEnabled=true
        binding.etUsuario.isEnabled=true
        binding.etCorreo.isEnabled=true
        binding.etContraseA.isEnabled=true
        binding.etCelular.isEnabled=true
    }
    fun limpiarCampos(){
        binding.etUsuario.setText("")
        binding.etCorreo.setText("")
        binding.etContraseA.setText("")
        binding.etCelular.setText("")
    }
}