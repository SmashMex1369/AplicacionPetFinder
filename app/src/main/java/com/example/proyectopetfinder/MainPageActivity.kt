package com.example.proyectopetfinder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.databinding.ActivityMainPageBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainPageBinding
    private lateinit var database :DatabaseReference
    private var nombre = ""
    private var id = 0
    private var fabsVisibles = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nombre = intent.getStringExtra("Nombre")!!
        id = intent.getIntExtra("Id",0)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        database = Firebase.database.reference
        val view = binding.root
        ocultarFABs()
        configurarRecycler()
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)



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

    private fun configurarRecycler(){
        binding.publicacionesRecycler.layoutManager = LinearLayoutManager(this)
        binding.publicacionesRecycler.setHasFixedSize(true)
    }

    private fun recuperarPublicacionesPerdidos(){
        database.child("PublicacionesExtraviados").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}