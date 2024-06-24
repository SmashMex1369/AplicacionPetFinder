package com.example.proyectopetfinder.actividades

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityVerPublicacionExtravioBinding
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class VerPublicacionExtravioActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVerPublicacionExtravioBinding
    private lateinit var database: DatabaseReference
    private var extraviado=PublicacionExtravio()
    private lateinit var nombre :String
    private var idUsuarioActual :Long = 0
    private lateinit var foto:String
    private lateinit var database2: DatabaseReference
    private lateinit var duenio :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerPublicacionExtravioBinding.inflate(layoutInflater)
        nombre= intent.getStringExtra("Nombre")!!
        idUsuarioActual= intent.getLongExtra("IdUsuarioActual",0)
        agregarIntents()
        database = Firebase.database.getReference("PublicacionesExtraviado").child("PublicacionExt${extraviado.idExtraviado}")
        database2 = Firebase.database.getReference("Usuarios").child("Usuario${extraviado.idUsuario}")
        insertarDatos()
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.imabBack.setOnClickListener {
            finish()
        }

        binding.btnChatGrupal.setOnClickListener {
            clicChatGrupal()
        }

        binding.btnContactarDuenio.setOnClickListener {
            clicChatDuenio()
        }
    }

    private fun clicChatGrupal(){
        val intent = Intent(this,ChatBusquedaActivity::class.java)
        intent.putExtra("usuario",nombre)
        intent.putExtra("destino",extraviado.nombre)
        intent.putExtra("idPublicacion",extraviado.idExtraviado)
        startActivity(intent)
    }

    private fun clicChatDuenio(){
        val intent = Intent(this, ChatDuenioActivity::class.java)
        intent.putExtra("usuario",nombre)
        intent.putExtra("destino",duenio)
        startActivity(intent)
    }

    private fun agregarIntents(){
        extraviado.nombre=intent.getStringExtra("NombreExtraviado")!!
        extraviado.tipo=intent.getStringExtra("TipoExtraviado")!!
        extraviado.sexo=intent.getStringExtra("SexoExtraviado")!!
        extraviado.edad=intent.getLongExtra("EdadExtraviado",0)
        extraviado.fecha=intent.getStringExtra("FechaExtraviado")!!
        extraviado.descripcion=intent.getStringExtra("DescripcionExtraviado")!!
        extraviado.raza=intent.getStringExtra("RazaExtraviado")!!
        extraviado.ubicacion=intent.getStringExtra("UbicacionExtraviado")!!
        extraviado.idExtraviado=intent.getLongExtra("IdExtraviado",0)
        extraviado.idUsuario=intent.getLongExtra("IdUsuarioBase",0)
    }

    @SuppressLint("SetTextI18n")
    fun insertarDatos(){
        binding.tvTituloExtravio.text=extraviado.nombre
        binding.tvFechaExtravio.text=getString(R.string.tv_fecha_extravio)+extraviado.fecha
        if(extraviado.edad?.toInt() ==1){
            binding.tvEdadExtravio.text=getString(R.string.tv_edad_publicacion)+extraviado.edad+" año"
        }else{
            binding.tvEdadExtravio.text=getString(R.string.tv_edad_publicacion)+extraviado.edad+" años"
        }

        binding.tvSexoExtravio.text=getString(R.string.tv_sexo_publicacion)+extraviado.sexo
        binding.tvRazaExtravio.text=getString(R.string.tv_raza_publicacion)+extraviado.raza
        binding.tvUltimaVezExtravio.text=getString(R.string.tv_ultima_vez)+extraviado.ubicacion
        binding.tvDescripcionExtravio.text=extraviado.descripcion
        database.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foto=snapshot.child("Foto").value.toString()
                val stringDecodificado = Base64.decode(foto,Base64.DEFAULT)
                val byteDecodificado = BitmapFactory.decodeByteArray(stringDecodificado,0,stringDecodificado.size)
                binding.imgvFotoExtravidos.setImageBitmap(byteDecodificado)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPublicacionExtravioActivity,"Error al acceder a la base datos", Toast.LENGTH_LONG).show()
            }

        })

        database2.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                duenio=snapshot.child("Nombre").value.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPublicacionExtravioActivity,"Error al acceder a la base datos", Toast.LENGTH_LONG).show()
            }

        })

    }
}