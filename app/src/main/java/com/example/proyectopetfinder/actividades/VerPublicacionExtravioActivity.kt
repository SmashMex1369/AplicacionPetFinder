package com.example.proyectopetfinder.actividades

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityVerPublicacionExtravioBinding

class VerPublicacionExtravioActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVerPublicacionExtravioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerPublicacionExtravioBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)
    }
}