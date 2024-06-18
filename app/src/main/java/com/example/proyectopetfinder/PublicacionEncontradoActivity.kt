package com.example.proyectopetfinder

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopetfinder.databinding.ActivityPublicacionEncontradoBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import java.io.ByteArrayOutputStream

class PublicacionEncontradoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPublicacionEncontradoBinding
    private lateinit var database: DatabaseReference
    private lateinit var spinnerMascota: Spinner
    var foto:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPublicacionEncontradoBinding.inflate(layoutInflater)
        val view= binding.root
        enableEdgeToEdge()
        setContentView(view)
        database= Firebase.database.reference

        window.statusBarColor = ContextCompat.getColor(this, R.color.rojo)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.rojo)

        spinnerMascota= findViewById(binding.spinnerTipoEncontrado.id)
        val listaMascota = arrayOf("Perro", "Gato")
        val adaptadorMascota = ArrayAdapter<String>(this, R.layout.spinner, listaMascota)
        spinnerMascota.adapter = adaptadorMascota

        val spinnerRaza: Spinner = findViewById(binding.spinnerRazaEncontrado.id)
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
                        this@PublicacionEncontradoActivity,
                        R.layout.spinner,
                        listaRazasPerro
                    )

                    "Gato" -> ArrayAdapter(
                        this@PublicacionEncontradoActivity,
                        R.layout.spinner,
                        listaRazasGato
                    )

                    else -> ArrayAdapter(
                        this@PublicacionEncontradoActivity,
                        R.layout.spinner,
                        arrayOf()
                    )
                }
                spinnerRaza.adapter = adaptadorRaza
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val spinnerPlaca: Spinner = findViewById(binding.spinnerTienePlaca.id)
        val tienePlaca= arrayOf("Si", "No")
        val adaptadorPlaca = ArrayAdapter<String>(this, R.layout.spinner, tienePlaca)
        spinnerPlaca.adapter = adaptadorPlaca

        val spinnerSexo: Spinner = findViewById(binding.spinnerSexoEncontrado.id)
        val listaSexo= arrayOf("Hembra", "Macho")
        val adaptadorSexo = ArrayAdapter<String>(this, R.layout.spinner, listaSexo)
        spinnerSexo.adapter = adaptadorSexo

        val spinnerUbicacion: Spinner = findViewById(binding.spinnerUbicacionEncontrado.id)
        val listaUbicacion= arrayOf("ArcoSur", "Tecnológico","Economía", "Centro")
        val adaptadorUbicacion = ArrayAdapter<String>(this, R.layout.spinner, listaUbicacion)
        spinnerUbicacion.adapter = adaptadorUbicacion

        binding.btnSubirImg.setOnClickListener{
            insertarImagen(view)
        }


        binding.btnPublicarEncontrado.setOnClickListener {
            val id=binding.etIdEncontrado.text.toString()
            //val descripcionValido= binding.etDescripcionEncontrado.text.toString()
            //datos de la base
            val key=binding.etIdEncontrado.text.toString()
            val tipoMascota= binding.spinnerTipoEncontrado.selectedItem.toString()
            val tienePlaca= binding.spinnerTienePlaca.selectedItem.toString()
            val sexo= binding.spinnerSexoEncontrado.selectedItem.toString()
            val raza= binding.spinnerRazaEncontrado.selectedItem.toString()
            val ubicacion= binding.spinnerUbicacionEncontrado.selectedItem.toString()
            val descripcion= binding.etDescripcionEncontrado.text.toString()

            if (validarCampos(id, descripcion)){
                Toast.makeText(this,"Campos validos",Toast.LENGTH_LONG).show()
                subirDatosAFirebase(key, tipoMascota, tienePlaca, sexo, raza, ubicacion, descripcion,foto)
            }
        }



    }

    fun validarCampos(id:String, descripcion: String):Boolean{

        var bandera=true
        /*if (id.isEmpty() && descripcion.isEmpty() && validaFotos){
            binding.etDescripcionEncontrado.error= getString(R.string.et_descripcion)
            binding.etIdEncontrado.error=getString(R.string.et_id)
            Toast.makeText(this,"Llene los campos vacios",Toast.LENGTH_LONG).show()
            bandera=false
        }*/if(id.isEmpty()){
            binding.etIdEncontrado.error=getString(R.string.et_id)
            bandera=false
        }
        if(descripcion.isEmpty()){
            binding.etDescripcionEncontrado.error= getString(R.string.et_descripcion)
            bandera=false
        }
        if(binding.viewFotosEncontrado.drawable==null){
            Toast.makeText(this,"Suba foto de referencia",Toast.LENGTH_LONG).show()
            bandera= false
        }
        return bandera
    }

    fun subirDatosAFirebase( key:String, tipo:String, tienePlaca:String, sexo:String, raza:String, ubicacion:String, descripcion: String, foto:String) {
        database.child("PublicacionesEncontrado").child(key).child("Tipo").setValue(tipo)
        database.child("PublicacionesEncontrado").child(key).child("Placa").setValue(tienePlaca)
        database.child("PublicacionesEncontrado").child(key).child("Sexo").setValue(sexo)
        database.child("PublicacionesEncontrado").child(key).child("Raza").setValue(raza)
        database.child("PublicacionesEncontrado").child(key).child("Ubicacion").setValue(ubicacion)
        database.child("PublicacionesEncontrado").child(key).child("Descripcion").setValue(descripcion)


        database.child("PublicacionesEncontrado").child(key).child("Foto").setValue(foto)

        Toast.makeText(this,"Publicación exitosa",Toast.LENGTH_LONG).show()
    }
    fun insertarImagen(view:View){
        var myfileintent= Intent(Intent.ACTION_GET_CONTENT)
        myfileintent.setType("image/*")
        PublicacionEncontradoActivity.launch(myfileintent)
    }

    private val PublicacionEncontradoActivity= registerForActivityResult<Intent, ActivityResult>(
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
                binding.viewFotosEncontrado.setImageBitmap(myBitmap)
                inputStream!!.close()
                Toast.makeText(this,"Imagen seleccionada",Toast.LENGTH_LONG).show()
            }catch (ex:Exception){
                Toast.makeText(this,ex.message.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }

}