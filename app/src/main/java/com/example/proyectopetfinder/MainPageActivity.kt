package com.example.proyectopetfinder

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this,R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this,R.color.rojo)
    }
}