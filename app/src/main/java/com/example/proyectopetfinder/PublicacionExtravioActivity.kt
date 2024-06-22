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
import com.example.proyectopetfinder.poko.Usuario
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.io.ByteArrayOutputStream
import com.example.proyectopetfinder.utilidades.Internet.tieneInternet
import com.example.proyectopetfinder.utilidades.Internet.perdioConexion

class PublicacionExtravioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPublicacionExtravioBinding
    private lateinit var database: DatabaseReference
    var foto: String = ""
    private var idUsuario:Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublicacionExtravioBinding.inflate(layoutInflater)
        val view = binding.root
        idUsuario=intent.getLongExtra("Id",0)
        enableEdgeToEdge()
        setContentView(view)
        database = Firebase.database.getReference("PublicacionesExtraviado")
        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.rojo)

        val spinnerMascota: Spinner = findViewById(binding.spinnerTipoExtravio.id)
        val listaMascota = arrayOf("Perro", "Gato")
        val adaptadorMascota = ArrayAdapter<String>(this, R.layout.spinner, listaMascota)
        spinnerMascota.adapter = adaptadorMascota

        val spinnerRaza: Spinner = findViewById(binding.spinnerRazaExtravio.id)
        val listaRazasPerro =
            arrayOf(
                "Labrador",
                "Golden Retriever",
                "Bulldog",
                "Pastor Alemán",
                "Chihuahua"
            ) // Lista de razas de perro
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
        val listaSexo = arrayOf("Hembra", "Macho")
        val adaptadorSexo = ArrayAdapter<String>(this, R.layout.spinner, listaSexo)
        spinnerSexo.adapter = adaptadorSexo

        val spinnerUbicacion: Spinner = findViewById(binding.spinnerUbicacionExtravio.id)
        val listaUbicacion = arrayOf("ArcoSur", "Tecnológico", "Economía", "Centro")
        val adaptadorUbicacion = ArrayAdapter<String>(this, R.layout.spinner, listaUbicacion)
        spinnerUbicacion.adapter = adaptadorUbicacion

        binding.btnSubirImgExtravio.setOnClickListener {
            insertarImagen(view)
        }

        binding.btnPublicar.setOnClickListener {
            //datos
            val tipo = binding.spinnerTipoExtravio.selectedItem.toString()
            val nombre = binding.etNombreExtravio.text.toString()
            val raza = binding.spinnerRazaExtravio.selectedItem.toString()
            val edad = binding.numberPicker.value.toLong()
            val sexo = binding.spinnerSexoExtravio.selectedItem.toString()
            val ubicacion = binding.spinnerUbicacionExtravio.selectedItem.toString()
            val fecha= obtenerFecha()
            val descripcion = binding.etDescripcion.text.toString()
            if (validarCampos(nombre, descripcion)) {
                if (tieneInternet(this)) {
                    deshabilitarCampos()
                    subirDatosAFirebase(tipo,nombre,raza,edad,sexo,ubicacion,fecha, descripcion,foto, idUsuario)
                } else {
                    Toast.makeText(this, ContextCompat.getString(this, R.string.sin_conexion),Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun validarCampos(nombre: String, descripcion: String): Boolean {
        var bandera = true
        if (nombre.isEmpty()) {
            binding.etNombreExtravio.error = getString(R.string.et_nombre_mascota_extravio)
            bandera = false
        }
        if (descripcion.isEmpty()) {
            binding.etDescripcion.error = getString(R.string.et_descripcion_extravio)
            bandera = false
        }
        if (binding.viewFotos.drawable == null) {
            Toast.makeText(this, "Suba foto de referencia", Toast.LENGTH_LONG).show()
            bandera = false
        }
        return bandera
    }

    fun subirDatosAFirebase(
        tipo: String,
        nombre: String,
        raza: String,
        edad: Long,
        sexo: String,
        ubicacion: String,
        fecha: String,
        descripcion: String,
        foto: String,
        idUsuario: Long
    ) {
        if (tieneInternet(this)) {
            var idExtraviado: Long= 0

            database.child("IdExtraviado").get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    idExtraviado = dataSnapshot.getValue().toString().toLong()

                }
                if (tieneInternet(this)) {
                    database.child("PublicacionExt" + idExtraviado).child("Tipo").setValue(tipo)
                    database.child("PublicacionExt" + idExtraviado).child("Nombre").setValue(nombre)
                    database.child("PublicacionExt" + idExtraviado).child("Raza").setValue(raza)
                    database.child("PublicacionExt" + idExtraviado).child("Edad").setValue(edad.toInt())
                    database.child("PublicacionExt" + idExtraviado).child("Sexo").setValue(sexo)
                    database.child("PublicacionExt" + idExtraviado).child("UbicacionUltimaVezVisto").setValue(ubicacion)
                    database.child("PublicacionExt" + idExtraviado).child("Fecha de extraviado").setValue(fecha)
                    database.child("PublicacionExt" + idExtraviado).child("Descripcion").setValue(descripcion)
                    database.child("PublicacionExt" + idExtraviado).child("Foto").setValue(foto)
                    database.child("PublicacionExt" + idExtraviado).child("IdUsuario").setValue(idUsuario.toInt())
                    database.child("PublicacionExt" + idExtraviado).child("IdExtraviado").setValue(idExtraviado.toInt())
                    database.child("IdExtraviado").setValue(idExtraviado.toInt() + 1)
                    Toast.makeText(this, "Publicación exitosa", Toast.LENGTH_LONG).show()
                    limpiarCampos()
                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    perdioConexion(this)
                    habilitarCampos()
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error al crear la cuenta, intente nuavemente más tarde",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun limpiarCampos() {
        binding.etNombreExtravio.setText("")
        binding.etDescripcion.setText("")
        binding.viewFotos.setImageDrawable(null)
    }

    fun deshabilitarCampos() {
        binding.btnSubirImgExtravio.isEnabled = false
        binding.btnPublicar.isEnabled = false
        binding.etNombreExtravio.isEnabled = false
        binding.etDescripcion.isEnabled = false
    }

    fun habilitarCampos() {
        binding.btnSubirImgExtravio.isEnabled = true
        binding.btnPublicar.isEnabled = true
        binding.etNombreExtravio.isEnabled = true
        binding.etDescripcion.isEnabled = true
    }

    fun obtenerFecha():String{
        val fecha: String
        val dia= binding.datePicker.dayOfMonth
        val mes= (binding.datePicker.month+1)
        val anio= binding.datePicker.year
        fecha= "${dia}/${mes}/ ${anio}"
        return fecha
    }

    fun insertarImagen(view: View) {
        val myfileintent = Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.type = "image/*"
        PublicacionExtravioActivity.launch(myfileintent)
    }

    private val PublicacionExtravioActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val myBitmap = BitmapFactory.decodeStream(inputStream)

                val resizedBitmap = redimensionarYComprimirImagen(myBitmap, 800, 800)
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
                val bytes = stream.toByteArray()
                foto = Base64.encodeToString(bytes, Base64.DEFAULT)

                binding.viewFotos.setImageBitmap(resizedBitmap)
                inputStream!!.close()
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_LONG).show()
            } catch (ex: Exception) {
                Toast.makeText(this, ex.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun redimensionarYComprimirImagen(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val aspectRatio = width.toFloat() / height.toFloat()

        if (width > height) {
            if (width > maxWidth) {
                width = maxWidth
                height = (width / aspectRatio).toInt()
            }
        } else {
            if (height > maxHeight) {
                height = maxHeight
                width = (height * aspectRatio).toInt()
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

}