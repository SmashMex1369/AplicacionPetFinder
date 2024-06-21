package com.example.proyectopetfinder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.databinding.ActivityIniciarSesionBinding
import com.example.proyectopetfinder.poko.Chat
import com.example.proyectopetfinder.poko.Usuario
import com.example.proyectopetfinder.utilidades.Internet
import com.example.proyectopetfinder.utilidades.Internet.perdioConexion
import com.example.proyectopetfinder.utilidades.Internet.tieneInternet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class IniciarSesionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityIniciarSesionBinding
    private lateinit var database: DatabaseReference
    var toast : Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        database = Firebase.database.getReference("Usuarios")
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        cargarCredenciales()
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.btnIniciarSesion.setOnClickListener {

            if(validarCampos()){
                if (tieneInternet(this)){
                    toast = Toast.makeText(this,"Iniciando sesión, por favor espere ...",Toast.LENGTH_SHORT)
                    toast?.show()
                    deshabilitarCampos()
                    val correo = binding.etCorreo.text.toString()
                    val contrasena = binding.etContraseA.text.toString()
                    iniciarSesion(correo,contrasena,this)
                }else{
                    Toast.makeText(this, ContextCompat.getString(this,R.string.sin_conexion),Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this,"Rellene los campos faltantes",Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun iniciarSesion(correo:String, contrasena:String, context:Context){
        if (tieneInternet(context)){
            database.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var encontrado = false
                    var termino = true
                    var correoBase : String
                    var contrasenaBase =""
                    var nombreBase = ""
                    var idBase = 0
                    for(usuario in snapshot.children){
                        if (tieneInternet(context)){
                            correoBase = usuario.child("Correo").value.toString()
                            if(correo==correoBase){
                                encontrado = true
                                contrasenaBase=usuario.child("Contraseña").value.toString()
                                nombreBase=usuario.child("Nombre").value.toString()
                                idBase=usuario.child("Id").value.toString().toInt()
                                break
                            }
                        }else{
                            perdioConexion(context)
                            habilitarCampos()
                            termino = false
                            break
                        }
                    }
                    if(termino){
                        if(encontrado){
                            if(contrasena==contrasenaBase){
                                if(tieneInternet(context)){
                                    if (binding.checkRecordarContraseA.isChecked){
                                        guardarCredenciales(correo,contrasena,true)
                                    }else{
                                        guardarCredenciales("","",false)
                                    }
                                    limpiarCampos()
                                    habilitarCampos()
                                    toast?.cancel()
                                    Toast.makeText(context,"Bienvenido a la aplicación $nombreBase",Toast.LENGTH_LONG).show()
                                    val intent = Intent(context, MainPageActivity::class.java)
                                    intent.putExtra("Nombre",nombreBase)
                                    intent.putExtra("Id",idBase)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    perdioConexion(context)
                                    habilitarCampos()
                                }
                            }else{
                                Toast.makeText(context,"Contraseña incorrectos. Intente nuevamente",Toast.LENGTH_LONG).show()
                                habilitarCampos()
                            }
                        }else{
                            Toast.makeText(context,"Cuenta no registrada, considere crear una cuenta",Toast.LENGTH_LONG).show()
                            habilitarCampos()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context,"Error al acceder a la base datos",Toast.LENGTH_LONG).show()
                }
            })
        }else{
            perdioConexion(context)
            habilitarCampos()
        }
    }

    private fun validarCampos():Boolean{
        var valido = true
        if(binding.etCorreo.text.isEmpty()){
            binding.etCorreo.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }

        if(binding.etContraseA.text?.isEmpty() == true){
            binding.etContraseA.error = ContextCompat.getString(this,R.string.et_error)
            valido = false
        }

        return valido
    }

    private fun deshabilitarCampos(){
        binding.etCorreo.isEnabled=false
        binding.etContraseA.isEnabled=false
        binding.checkRecordarContraseA.isEnabled= false
        binding.btnIniciarSesion.isEnabled= false
    }

    fun habilitarCampos(){
        binding.etCorreo.isEnabled=true
        binding.etContraseA.isEnabled=true
        binding.checkRecordarContraseA.isEnabled= true
        binding.btnIniciarSesion.isEnabled= true
    }

    fun guardarCredenciales(correo:String, contrasena: String, guardado:Boolean){
        val archivoPreferenciasDefault = getPreferences(Context.MODE_PRIVATE)
        val archivoEdit = archivoPreferenciasDefault.edit()
        archivoEdit.putString("Correo",correo)
        archivoEdit.putString("Contraseña",contrasena)
        archivoEdit.putBoolean("Guardar",guardado)
        archivoEdit.apply()
    }

    fun cargarCredenciales(){
        val archivoPreferenciasDefault = getPreferences(Context.MODE_PRIVATE)
        binding.etCorreo.setText(archivoPreferenciasDefault.getString("Correo",""))
        binding.etContraseA.setText(archivoPreferenciasDefault.getString("Contraseña",""))
        if(archivoPreferenciasDefault.getBoolean("Guardar",false)){
            binding.checkRecordarContraseA.isChecked = true

        }else{
            binding.checkRecordarContraseA.isChecked = false
        }
    }

    fun limpiarCampos(){
        binding.etCorreo.setText("")
        binding.etContraseA.setText("")
    }
}