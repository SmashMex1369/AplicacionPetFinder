package com.example.proyectopetfinder.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityVerPublicacionEncontradoBinding
import com.example.proyectopetfinder.poko.PublicacionEncontrado
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class VerPublicacionEncontradoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVerPublicacionEncontradoBinding
    private lateinit var database: DatabaseReference
    private var encontrado= PublicacionEncontrado()
    private lateinit var nombre :String
    private var idUsuarioActual :Long = 0
    private lateinit var foto:String
    private lateinit var database2: DatabaseReference
    private lateinit var duenio :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerPublicacionEncontradoBinding.inflate(layoutInflater)
        nombre=intent.getStringExtra("Nombre")!!
        idUsuarioActual=intent.getLongExtra("IdUsuarioActual",0)
        agregarIntents()
        database = Firebase.database.getReference("PublicacionesEncontrado").child("Publicacion${encontrado.idEncontrado}")
        database2 = Firebase.database.getReference("Usuarios").child("Usuario${encontrado.idUsuario}")
        insertarDatos()
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.imabBack.setOnClickListener {
            finish()
        }

        binding.btnContactarDuenio.setOnClickListener {
            clicChatDuenio()
        }
    }

    private fun agregarIntents(){
        encontrado.tipo=intent.getStringExtra("TipoEncontrado")!!
        encontrado.placa=intent.getBooleanExtra("PlacaEncontrado",false)
        encontrado.fecha=intent.getStringExtra("FechaEncontrado")!!
        encontrado.sexo=intent.getStringExtra("SexoEncontrado")!!
        encontrado.raza=intent.getStringExtra("RazaEncontrado")!!
        encontrado.ubicacion=intent.getStringExtra("UbicacionEncontrado")!!
        encontrado.descripcion=intent.getStringExtra("DescripcionEncontrado")!!
        encontrado.idEncontrado=intent.getLongExtra("IdEncontrado",0)
        encontrado.idUsuario=intent.getLongExtra("IdUsuarioBase",0)
    }

    private fun clicChatDuenio(){
        val intent = Intent(this, ChatDuenioActivity::class.java)
        intent.putExtra("usuario",nombre)
        intent.putExtra("destino",duenio)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    fun insertarDatos(){
        binding.tvTituloEncontrado.text=encontrado.tipo
        binding.tvFechaEncontrado.text=getString(R.string.tv_fecha_encuentro)+encontrado.fecha
        binding.tvRazaEncontrado.text=getString(R.string.tv_raza_publicacion)+encontrado.raza
        binding.tvSexoEncontrado.text=getString(R.string.tv_sexo_publicacion)+encontrado.sexo
        if(encontrado.placa==true){
            binding.tvPlacaEncontrado.text=getString(R.string.tv_placa_publicacion)+"Tiene placa"
        }else{
            binding.tvPlacaEncontrado.text=getString(R.string.tv_placa_publicacion)+"No tiene placa"
        }
        binding.tvLugarEncuentro.text=getString(R.string.tv_lugar_encontrado)+encontrado.ubicacion
        binding.tvDescripcionEncontrado.text=encontrado.descripcion
        database.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foto=snapshot.child("Foto").value.toString()
                val stringDecodificado = Base64.decode(foto, Base64.DEFAULT)
                val byteDecodificado = BitmapFactory.decodeByteArray(stringDecodificado,0,stringDecodificado.size)
                binding.imgvFotoEncontrados.setImageBitmap(byteDecodificado)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPublicacionEncontradoActivity,"Error al acceder a la base datos", Toast.LENGTH_LONG).show()
            }

        })

        database2.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                duenio=snapshot.child("Nombre").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPublicacionEncontradoActivity,"Error al acceder a la base datos", Toast.LENGTH_LONG).show()
            }

        })
    }
}