package com.example.proyectopetfinder.actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.databinding.ActivityBusquedaDePublicacionesBinding


class BusquedaDePublicacionesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBusquedaDePublicacionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaDePublicacionesBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)

        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.rojo)

        val spinnerMascota: Spinner = findViewById(binding.spinner1.id)
        val listaMascota = arrayOf("","Perro", "Gato")
        val adaptadorMascota = ArrayAdapter<String>(this, R.layout.spinner, listaMascota)
        spinnerMascota.adapter = adaptadorMascota

        val spinnerRaza: Spinner = findViewById(binding.spinner2.id)
        val listaRazasPerro =
            arrayOf("","Labrador", "Golden Retriever", "Bulldog", "Pastor Alemán", "Chihuahua") // Lista de razas de perro
        val listaRazasGato = arrayOf("","Siamés", "Maine Coon", "Persa")

        spinnerMascota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMascota = listaMascota[position]
                val adaptadorRaza: ArrayAdapter<String> = when (selectedMascota) {
                    "Perro" -> ArrayAdapter(
                        this@BusquedaDePublicacionesActivity,
                        R.layout.spinner,
                        listaRazasPerro
                    )

                    "Gato" -> ArrayAdapter(
                        this@BusquedaDePublicacionesActivity,
                        R.layout.spinner,
                        listaRazasGato
                    )

                    else -> ArrayAdapter(
                        this@BusquedaDePublicacionesActivity,
                        R.layout.spinner,
                        arrayOf()
                    )
                }
                spinnerRaza.adapter = adaptadorRaza
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val spinnerRegion: Spinner = findViewById(binding.spinner3.id)
        val listaRegion = arrayOf("","ArcoSur", "Tecnológico","Economía", "Centro")
        val adaptadorRegion = ArrayAdapter<String>(this, R.layout.spinner, listaRegion)
        spinnerRegion.adapter = adaptadorRegion

        binding.btnBuscar.setOnClickListener {
            binding.linearBotones.visibility=View.VISIBLE
            binding.recyclerBusqueda.visibility=View.VISIBLE
        }

        binding.tvPerdidos.setOnClickListener {

        }

    }

    fun obtenerFecha():String{
        val fecha: String
        val dia= binding.datePicker.dayOfMonth
        val mes= (binding.datePicker.month+1)
        val anio= binding.datePicker.year
        fecha= "${dia}/${mes}/${anio}"
        return fecha
    }
    fun configurarRecyclerPerdidos(){
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.itim)
        binding.recyclerBusqueda.layoutManager = LinearLayoutManager(this)
        binding.recyclerBusqueda.setHasFixedSize(true)
        binding.tvPerdidos.setTypeface(typeface, Typeface.BOLD)
        binding.viewPerdidos.setBackgroundResource(R.color.black)
        binding.tvEncontrados.setTypeface(typeface, Typeface.NORMAL)
        binding.viewEncontrados.setBackgroundColor(Color.TRANSPARENT)
    }
    fun recuperarBusquedaPerdidos(){
        var tipoBusqueda = binding.spinner1.selectedItem
        var razaBusqueda = binding.spinner2.selectedItem
        var ubicacionBusqueda = binding.spinner3.selectedItem

    }
}