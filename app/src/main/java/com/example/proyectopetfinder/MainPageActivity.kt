package com.example.proyectopetfinder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.databinding.ActivityMainPageBinding


class MainPageActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainPageBinding
    private var nombre = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nombre = intent.getStringExtra("Nombre")!!
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)



        binding.btnChat.setOnClickListener {
            val intent = Intent(this,ChatsActivity::class.java)
            intent.putExtra("Nombre",nombre)
            startActivity(intent)
        }
    }
}