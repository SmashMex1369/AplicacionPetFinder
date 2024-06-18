package com.example.proyectopetfinder

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.databinding.ActivityPublicacionExtravioBinding
import com.google.firebase.database.DatabaseReference
import java.io.ByteArrayOutputStream

class PublicacionExtravioActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPublicacionExtravioBinding
    private lateinit var database: DatabaseReference
    var foto:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPublicacionExtravioBinding.inflate(layoutInflater)
        val view= binding.root
        enableEdgeToEdge()
        setContentView(view)

        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.rojo)

        val spinnerMascota: Spinner = findViewById(binding.spinnerTipoExtravio.id)
        val listaMascota = arrayOf("Perro", "Gato")
        val adaptadorMascota = ArrayAdapter<String>(this, R.layout.spinner, listaMascota)
        spinnerMascota.adapter = adaptadorMascota

        val spinnerRaza: Spinner = findViewById(binding.spinnerRazaExtravio.id)
        val listaRazasPerro =
            arrayOf("Labrador", "Golden Retriever", "Bulldog", "Pastor Alemán", "Chihuahua") // Lista de razas de perro
        val listaRazasGato = arrayOf("Siamés", "Maine Coon", "Persa")

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
                        this@PublicacionExtravioActivity,
                        R.layout.spinner,
                        listaRazasPerro
                    )

                    "Gato" -> ArrayAdapter(
                        this@PublicacionExtravioActivity,
                        R.layout.spinner,
                        listaRazasGato
                    )

                    else -> ArrayAdapter(
                        this@PublicacionExtravioActivity,
                        R.layout.spinner,
                        arrayOf()
                    )
                }
                spinnerRaza.adapter = adaptadorRaza
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val numberPicker: NumberPicker = findViewById(R.id.numberPicker)
        numberPicker.minValue = 1
        numberPicker.maxValue = 20
        numberPicker.wrapSelectorWheel = false

        val spinnerSexo: Spinner = findViewById(binding.spinnerSexoExtravio.id)
        val listaSexo= arrayOf("Hembra", "Macho")
        val adaptadorSexo = ArrayAdapter<String>(this, R.layout.spinner, listaSexo)
        spinnerSexo.adapter = adaptadorSexo

        val spinnerUbicacion: Spinner = findViewById(binding.spinnerUbicacionExtravio.id)
        val listaUbicacion= arrayOf("ArcoSur", "Tecnológico","Economía", "Centro")
        val adaptadorUbicacion = ArrayAdapter<String>(this, R.layout.spinner, listaUbicacion)
        spinnerUbicacion.adapter = adaptadorUbicacion

        binding.btnSubirImgExtravio.setOnClickListener{
            insertarImagen(view)
        }

        binding.btnPublicar.setOnClickListener {
            //datos
            val id=binding.etIdExtraviado.text.toString()
            val tipo= binding.spinnerTipoExtravio.selectedItem.toString()
            val nombre= binding.etNombreExtravio.text.toString()
            val raza= binding.spinnerRazaExtravio.selectedItem.toString()
            val edad= binding.numberPicker.value
            val sexo= binding.spinnerSexoExtravio.selectedItem.toString()
            val ubicacion=binding.spinnerUbicacionExtravio.selectedItem.toString()
            val descripcion= binding.etDescripcion.text.toString()

            if (validarCampos(id,nombre, descripcion)){
                Toast.makeText(this,"Campos validos",Toast.LENGTH_LONG).show()
                subirDatosAFirebase(id, tipo, nombre, raza, edad, sexo, ubicacion, descripcion,foto)
            }

        }

    }

    fun validarCampos(id:String,nombre:String, descripcion: String):Boolean{
        var bandera=true

        if(id.isEmpty()){
            binding.etIdExtraviado.error=getString(R.string.et_id_extravio)
            bandera=false
        }
        if(nombre.isEmpty()){
            binding.etNombreExtravio.error=getString(R.string.et_nombre_extravio)
            bandera=false
        }
        if(descripcion.isEmpty()){
            binding.etDescripcion.error= getString(R.string.et_descripcion_extravio)
            bandera=false
        }
        if(binding.viewFotos.drawable==null){
            Toast.makeText(this,"Suba foto de referencia", Toast.LENGTH_LONG).show()
            bandera= false
        }
        return bandera
    }

    fun subirDatosAFirebase( key:String, tipo:String, nombre:String, raza:String, edad:Int, sexo:String, ubicacion: String, descripcion: String, foto:String) {
        database.child("PublicacionesExtraviado").child(key).child("Tipo").setValue(tipo)
        database.child("PublicacionesExtraviado").child(key).child("Nombre").setValue(nombre)
        database.child("PublicacionesExtraviado").child(key).child("Raza").setValue(raza)
        database.child("PublicacionesExtraviado").child(key).child("Edad").setValue(edad)
        database.child("PublicacionesExtraviado").child(key).child("Sexo").setValue(sexo)
        database.child("PublicacionesExtraviado").child(key).child("UbicacionUltimaVezVisto").setValue(ubicacion)
        database.child("PublicacionesExtraviado").child(key).child("Descripcion").setValue(descripcion)

        database.child("PublicacionesEncontrado").child(key).child("Foto").setValue(foto)

        Toast.makeText(this,"Publicación exitosa",Toast.LENGTH_LONG).show()
    }

    fun insertarImagen(view:View){
        var myfileintent= Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.setType("image/*")
        PublicacionExtravioActivity.launch(myfileintent)
    }

    private val PublicacionExtravioActivity= registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result: ActivityResult ->
        if (result.resultCode== RESULT_OK){
            val uri= result.data!!.data
            try {
                val inputStream= contentResolver.openInputStream(uri!!)
                val myBitmap= BitmapFactory.decodeStream(inputStream)
                val streamm= ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, streamm)
                val bytes= streamm.toByteArray()
                foto= Base64.encodeToString(bytes,Base64.DEFAULT)
                binding.viewFotos.setImageBitmap(myBitmap)
                inputStream!!.close()
                Toast.makeText(this,"Imagen seleccionada",Toast.LENGTH_LONG).show()
            }catch (ex:Exception){
                Toast.makeText(this,ex.message.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }


}