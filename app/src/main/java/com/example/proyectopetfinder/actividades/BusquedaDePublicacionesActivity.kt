package com.example.proyectopetfinder.actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.adaptadores.PublicacionEncuentroAdapter
import com.example.proyectopetfinder.adaptadores.PublicacionExtravioAdapter
import com.example.proyectopetfinder.databinding.ActivityBusquedaDePublicacionesBinding
import com.example.proyectopetfinder.interfaces.ListenerRecyclerEncontrado
import com.example.proyectopetfinder.poko.PublicacionEncontrado
import com.example.proyectopetfinder.poko.PublicacionExtravio
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class BusquedaDePublicacionesActivity : AppCompatActivity(),ListenerRecyclerEncontrado {
    private lateinit var binding: ActivityBusquedaDePublicacionesBinding
    private lateinit var databasePerdidos : DatabaseReference
    private lateinit var databaseEncontrados:DatabaseReference
    private lateinit var adaptador: PublicacionEncuentroAdapter
    private lateinit var perdidosAdapter: PublicacionExtravioAdapter
    private var listaPerdidos:MutableList<PublicacionExtravio> = mutableListOf()
    private lateinit var encontradosAdapter: PublicacionEncuentroAdapter
   var listaEncontrado= arrayListOf<PublicacionEncontrado>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaDePublicacionesBinding.inflate(layoutInflater)
        databasePerdidos= Firebase.database.getReference("PublicacionesExtraviado")
        databaseEncontrados=Firebase.database.getReference("PublicacionesEncontrado")
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
            configurarRecyclerEncontrados()
        }

        binding.tvPerdidos.setOnClickListener {

        }



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

    fun configurarRecyclerEncontrados(){
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.itim)
        binding.recyclerBusqueda.layoutManager = LinearLayoutManager(this)
        binding.recyclerBusqueda.setHasFixedSize(true)
        binding.tvPerdidos.setTypeface(typeface, Typeface.BOLD)
        binding.viewPerdidos.setBackgroundResource(R.color.black)
        binding.tvEncontrados.setTypeface(typeface, Typeface.NORMAL)
        binding.viewEncontrados.setBackgroundColor(Color.TRANSPARENT)
        recuperarBusquedaEncontrados()
        adaptador= PublicacionEncuentroAdapter(listaEncontrado, this)
        binding.recyclerBusqueda.adapter=adaptador

    }

    override fun clicPublicacion(encontrado: PublicacionEncontrado) {
        var intent = Intent(this,VerPublicacionEncontradoActivity::class.java)
        startActivity(intent)
    }
    private fun recuperarBusquedaEncontrados(){

        databaseEncontrados.addValueEventListener(object: ValueEventListener {
             var tipoSelecionado= binding.spinner1.selectedItem.toString()
             var razaSeleccionado=binding.spinner2.selectedItem.toString()
             var ubicacionSeleccionada=binding.spinner3.selectedItem.toString()
            override fun onDataChange(snapshot: DataSnapshot) {

                listaEncontrado.clear()
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
                        if((tipoSelecionado==encontrado.tipo||tipoSelecionado=="")&&
                            (razaSeleccionado==encontrado.raza||razaSeleccionado=="")&&
                            (ubicacionSeleccionada==encontrado.ubicacion||ubicacionSeleccionada=="")){
                            listaEncontrado.add(encontrado)
                            i++
                        }



                    }
                    encontradosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BusquedaDePublicacionesActivity,"Error al acceder a la base de datos", Toast.LENGTH_LONG).show()
            }

        })
    }


}