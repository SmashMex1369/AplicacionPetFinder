package com.example.proyectopetfinder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.databinding.ActivityVerPublicacionBinding

class VerPublicacionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVerPublicacionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerPublicacionBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)
    }
}