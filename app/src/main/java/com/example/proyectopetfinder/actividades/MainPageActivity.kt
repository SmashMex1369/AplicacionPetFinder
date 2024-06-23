package com.example.proyectopetfinder.actividades

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.adaptadores.PublicacionEncuentroAdapter
import com.example.proyectopetfinder.adaptadores.PublicacionExtravioAdapter
import com.example.proyectopetfinder.databinding.ActivityMainPageBinding
import com.example.proyectopetfinder.interfaces.ListenerRecyclerEncontrado
import com.example.proyectopetfinder.interfaces.ListenerRecyclerExtraviado
import com.example.proyectopetfinder.poko.PublicacionEncontrado
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainPageActivity : AppCompatActivity(),ListenerRecyclerExtraviado,ListenerRecyclerEncontrado {
    private lateinit var binding : ActivityMainPageBinding
    private lateinit var databasePerdidos :DatabaseReference
    private lateinit var databaseEncontrados :DatabaseReference
    private var nombre:String? = ""
    private var id:Long = 0
    private var fabsVisibles = false
    private lateinit var perdidosAdapter: PublicacionExtravioAdapter
    private var perdidosList:MutableList<PublicacionExtravio> = mutableListOf()
    private lateinit var encontradosAdapter: PublicacionEncuentroAdapter
    private var encontradosList:MutableList<PublicacionEncontrado> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nombre = intent.getStringExtra("Nombre")
        id = intent.getLongExtra("Id",0)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        databasePerdidos = Firebase.database.getReference("PublicacionesExtraviado")
        databaseEncontrados = Firebase.database.getReference("PublicacionesEncontrado")
        val view = binding.root
        ocultarFABs()
        configurarRecyclerPerdidos()
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
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
        recuperarPublicacionesEncontrados()
        encontradosAdapter=PublicacionEncuentroAdapter(encontradosList,this)
        binding.publicacionesRecycler.adapter=encontradosAdapter
    }

    private fun recuperarPublicacionesPerdidos(){

        databasePerdidos.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                perdidosList.clear()
                var i = 0
                for(perdidos in snapshot.children){
                    if(perdidos.hasChild("Tipo")){
                        val perdido= PublicacionExtravio()
                        perdido.nombre = perdidos.child("Nombre").value.toString()
                        perdido.tipo = perdidos.child("Tipo").value.toString()
                        perdido.sexo = perdidos.child("Sexo").value.toString()
                        perdido.edad = perdidos.child("Edad").value as? Long
                        perdido.fecha = perdidos.child("Fecha de extraviado").value.toString()
                        perdido.descripcion = perdidos.child("Descripcion").value.toString()
                        perdido.raza = perdidos.child("Raza").value.toString()
                        perdido.ubicacion = perdidos.child("UbicacionUltimaVezVisto").value.toString()
                        perdido.foto = perdidos.child("Foto").value.toString()
                        perdido.idExtraviado = perdidos.child("IdExtraviado").value as? Long
                        perdido.idUsuario= perdidos.child("IdUsuario").value as? Long
                        perdidosList.add(i,perdido)
                        i++

                    }
                    perdidosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainPageActivity,"Error al acceder a la base de datos",Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun recuperarPublicacionesEncontrados(){

        databaseEncontrados.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                encontradosList.clear()
                var i = 0
                for(encontrados in snapshot.children){
                    if(encontrados.hasChild("Tipo")){
                        val encontrado= PublicacionEncontrado()
                        encontrado.tipo = encontrados.child("Tipo").value.toString()
                        encontrado.placa = encontrados.child("Placa").value as? Boolean
                        encontrado.sexo = encontrados.child("Sexo").value.toString()
                        encontrado.fecha = encontrados.child("Fecha de encontrado").value.toString()
                        encontrado.descripcion = encontrados.child("Descripcion").value.toString()
                        encontrado.raza = encontrados.child("Raza").value.toString()
                        encontrado.ubicacion = encontrados.child("Ubicacion").value.toString()
                        encontrado.foto = encontrados.child("Foto").value.toString()
                        encontrado.idEncontrado = encontrados.child("IdEncontrado").value as? Long
                        encontrado.idUsuario= encontrados.child("IdUsuario").value as? Long
                        encontradosList.add(i,encontrado)
                        i++
                    }
                    encontradosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainPageActivity,"Error al acceder a la base de datos",Toast.LENGTH_LONG).show()
            }

        })
    }


    override fun clicPublicacion(extraviado: PublicacionExtravio) {
        val intent=Intent(this, VerPublicacionExtravioActivity::class.java)
        intent.putExtra("NombreExtraviado",extraviado.nombre)
        intent.putExtra("TipoExtraviado",extraviado.tipo)
        intent.putExtra("SexoExtraviado",extraviado.sexo)
        intent.putExtra("EdadExtraviado",extraviado.edad)
        intent.putExtra("FechaExtraviado",extraviado.fecha)
        intent.putExtra("DescripcionExtraviado",extraviado.descripcion)
        intent.putExtra("RazaExtraviado",extraviado.raza)
        intent.putExtra("UbicacionExtraviado",extraviado.ubicacion)
        intent.putExtra("IdExtraviado",extraviado.idExtraviado)
        intent.putExtra("IdUsuarioBase",extraviado.idUsuario)
        intent.putExtra("Nombre",nombre)
        intent.putExtra("IdUsuarioActual",id)
        startActivity(intent)

    }

    override fun clicPublicacion(encontrado: PublicacionEncontrado) {
        val intent = Intent(this,VerPublicacionEncontradoActivity::class.java)
        intent.putExtra("TipoEncontrado",encontrado.tipo)
        intent.putExtra("PlacaEncontrado",encontrado.placa)
        intent.putExtra("FechaEncontrado",encontrado.fecha)
        intent.putExtra("SexoEncontrado",encontrado.sexo)
        intent.putExtra("RazaEncontrado",encontrado.raza)
        intent.putExtra("UbicacionEncontrado",encontrado.ubicacion)
        intent.putExtra("DescripcionEncontrado",encontrado.descripcion)
        intent.putExtra("IdEncontrado",encontrado.idEncontrado)
        intent.putExtra("IdUsuarioBase",encontrado.idUsuario)
        intent.putExtra("Nombre",nombre)
        intent.putExtra("IdUsuarioActual",id)
        startActivity(intent)
    }
}