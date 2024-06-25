package com.example.proyectopetfinder.actividades

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
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
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.adaptadores.BusquedaEncuentroAdapter
import com.example.proyectopetfinder.adaptadores.BusquedaExtravioAdapter
import com.example.proyectopetfinder.adaptadores.PublicacionEncuentroAdapter
import com.example.proyectopetfinder.adaptadores.PublicacionExtravioAdapter
import com.example.proyectopetfinder.databinding.ActivityBusquedaDePublicacionesBinding
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


class BusquedaDePublicacionesActivity : AppCompatActivity(),ListenerRecyclerEncontrado,ListenerRecyclerExtraviado {
    private lateinit var binding: ActivityBusquedaDePublicacionesBinding
    private lateinit var databasePerdidos : DatabaseReference
    private lateinit var databaseEncontrados:DatabaseReference
    private var nombre:String? = ""
    private var id:Long = 0
    private lateinit var perdidosAdapter: PublicacionExtravioAdapter
    private var listaPerdidos:MutableList<PublicacionExtravio> = mutableListOf()
    private lateinit var encontradosAdapter: BusquedaEncuentroAdapter
   var listaEncontrado= arrayListOf<PublicacionEncontrado>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaDePublicacionesBinding.inflate(layoutInflater)
        nombre = intent.getStringExtra("Nombre")
        id = intent.getLongExtra("Id",0)
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
                        arrayOf("")
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
            configurarRecyclerPerdidos()
        }
        binding.tvPerdidos.setOnClickListener {
            configurarRecyclerPerdidos()
        }

        binding.tvEncontrados.setOnClickListener {
            configurarRecyclerEncontrados()
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
        recuperarBusquedaPerdidos()
        perdidosAdapter = PublicacionExtravioAdapter(listaPerdidos,this)
        binding.recyclerBusqueda.adapter=perdidosAdapter
    }
    fun recuperarBusquedaPerdidos(){
        val tipoSelecionado= binding.spinner1.selectedItem.toString()
        val razaSeleccionado=binding.spinner2.selectedItem.toString()
        val ubicacionSeleccionada=binding.spinner3.selectedItem.toString()
        databasePerdidos.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listaPerdidos.clear()
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
                        if((tipoSelecionado==perdido.tipo||tipoSelecionado=="")&&
                            (razaSeleccionado==perdido.raza||razaSeleccionado=="")&&
                            (ubicacionSeleccionada==perdido.ubicacion||ubicacionSeleccionada=="")){
                            listaPerdidos.add(perdido)
                            i++
                        }

                    }
                    perdidosAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BusquedaDePublicacionesActivity,"Error al acceder a la base de datos",Toast.LENGTH_LONG).show()
            }

        })

    }

    fun configurarRecyclerEncontrados(){
        val typeface: Typeface? = ResourcesCompat.getFont(this, R.font.itim)
        binding.recyclerBusqueda.layoutManager = LinearLayoutManager(this)
        binding.recyclerBusqueda.setHasFixedSize(true)
        binding.tvPerdidos.setTypeface(typeface, Typeface.NORMAL)
        binding.viewPerdidos.setBackgroundColor(Color.TRANSPARENT)
        binding.tvEncontrados.setTypeface(typeface, Typeface.BOLD)
        binding.viewEncontrados.setBackgroundResource(R.color.black)
        recuperarBusquedaEncontrados()
        encontradosAdapter= BusquedaEncuentroAdapter(listaEncontrado, this)
        binding.recyclerBusqueda.adapter=encontradosAdapter

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