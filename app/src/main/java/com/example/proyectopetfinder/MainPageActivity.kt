package com.example.proyectopetfinder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.adaptadores.PublicacionExtravioAdapter
import com.example.proyectopetfinder.databinding.ActivityMainPageBinding
import com.example.proyectopetfinder.interfaces.ListenerRecyclerExtraviado
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainPageActivity : AppCompatActivity(),ListenerRecyclerExtraviado {
    private lateinit var binding : ActivityMainPageBinding
    private lateinit var databasePerdidos :DatabaseReference
    private var nombre:String? = ""
    private var id:Long = 0
    private var fabsVisibles = false
    private lateinit var perdidosAdapter: PublicacionExtravioAdapter
    private var perdidosList:MutableList<PublicacionExtravio> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nombre = intent.getStringExtra("Nombre")
        id = intent.getLongExtra("Id",0)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        databasePerdidos = Firebase.database.getReference("PublicacionesExtraviado")
        val view = binding.root
        ocultarFABs()
        configurarRecyclerPerdidos()
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)

        binding.tvPerdidos.setOnClickListener{
            configurarRecyclerPerdidos()
        }

        binding.tvEncontrados.setOnClickListener{
            configurarRecyclerEncontrados()
        }

        binding.fabPublicaciones.setOnClickListener {
            if(!fabsVisibles){
                mostrarFABs()
            }else{
                ocultarFABs()
            }
        }

        binding.fabEncontrado.setOnClickListener {
            val intent = Intent(this,PublicacionEncontradoActivity::class.java)
            intent.putExtra("Id",id)
            startActivity(intent)
        }
        binding.fabPerdido.setOnClickListener {
            val intent = Intent(this,PublicacionExtravioActivity::class.java)
            intent.putExtra("Id",id)
            startActivity(intent)
        }

        binding.imgBuscar.setOnClickListener{
            val intent = Intent(this, BusquedaDePublicacionesActivity::class.java)
            startActivity(intent)
        }

        binding.btnChat.setOnClickListener {
            val intent = Intent(this,ChatsActivity::class.java)
            intent.putExtra("Nombre",nombre)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        ocultarFABs()
    }

    override fun onResume() {
        super.onResume()
        ocultarFABs()
    }

    override fun onRestart() {
        super.onRestart()
        ocultarFABs()
    }
    private fun ocultarFABs(){
        binding.fabPublicaciones.shrink()
        binding.fabEncontrado.visibility=View.GONE
        binding.tvFabEncontrado.visibility=View.GONE
        binding.fabPerdido.visibility=View.GONE
        binding.tvFabPerdido.visibility=View.GONE
        fabsVisibles = false
    }

    private fun mostrarFABs(){
        binding.fabPublicaciones.extend()
        binding.fabEncontrado.show()
        binding.tvFabEncontrado.visibility=View.VISIBLE
        binding.fabPerdido.show()
        binding.tvFabPerdido.visibility=View.VISIBLE
        fabsVisibles = true
    }

    private fun configurarRecyclerPerdidos(){
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.itim)
        binding.publicacionesRecycler.layoutManager = LinearLayoutManager(this)
        binding.publicacionesRecycler.setHasFixedSize(true)
        binding.tvPerdidos.setTypeface(typeface, Typeface.BOLD)
        binding.viewPerdidos.setBackgroundResource(R.color.black)
        binding.tvEncontrados.setTypeface(typeface, Typeface.NORMAL)
        binding.viewEncontrados.setBackgroundColor(Color.TRANSPARENT)
        recuperarPublicacionesPerdidos()
        perdidosAdapter = PublicacionExtravioAdapter(perdidosList,this)
        binding.publicacionesRecycler.adapter=perdidosAdapter
    }

    private fun configurarRecyclerEncontrados(){
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.itim)
        binding.publicacionesRecycler.layoutManager = LinearLayoutManager(this)
        binding.publicacionesRecycler.setHasFixedSize(true)
        binding.tvPerdidos.setTypeface(typeface, Typeface.NORMAL)
        binding.viewPerdidos.setBackgroundColor(Color.TRANSPARENT)
        binding.tvEncontrados.setTypeface(typeface,Typeface.BOLD)
        binding.viewEncontrados.setBackgroundResource(R.color.black)

    }

    private fun recuperarPublicacionesPerdidos(){
        databasePerdidos.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var perdido  = PublicacionExtravio()
                perdidosList.clear()
                for(perdidos in snapshot.children){
                    if(perdidos.hasChild("Tipo")){
                        perdido.nombre = perdidos.child("Nombre").value.toString()
                        perdido.tipo = perdidos.child("Tipo").value.toString()
                        perdido.sexo = perdidos.child("Sexo").value.toString()
                        perdido.edad = perdidos.child("Edad").value as? Long
                        perdido.fecha = perdidos.child("Fecha").value.toString()
                        perdido.descripcion = perdidos.child("Descripcion").value.toString()
                        perdido.raza = perdidos.child("Raza").value.toString()
                        perdido.ubicacion = perdidos.child("UbicacionUltimaVezVisto").value.toString()
                        perdido.foto = perdidos.child("Foto").value.toString()
                        perdido.idExtraviado = perdidos.child("IdExtraviado").value as? Long
                        perdido.idUsuario= (perdidos.child("IdUsuario").value as? Long)
                        perdidosList.add(perdido)
                        Log.i("Completado","Se agrego la publicacion con id ${perdido.idExtraviado}")
                    }

                }
                perdidosAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainPageActivity,"Error al acceder a la base de datos",Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun clicPublicacion(extraviado: PublicacionExtravio) {
        val intent = Intent(this,VerPublicacionActivity::class.java)
        intent.putExtra("NombreExtraviado",extraviado.nombre)
        startActivity(intent)

    }
}