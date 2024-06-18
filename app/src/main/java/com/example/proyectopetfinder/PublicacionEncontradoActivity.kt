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
import com.example.proyectopetfinder.utilidades.Internet.tieneInternet
import com.example.proyectopetfinder.utilidades.Internet.perdioConexion

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
        database= Firebase.database.getReference("PublicacionesEncontrado")

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
            //datos de la base
            val tipoMascota= binding.spinnerTipoEncontrado.selectedItem.toString()
            val tienePlaca= binding.spinnerTienePlaca.selectedItem.toString()
            val sexo= binding.spinnerSexoEncontrado.selectedItem.toString()
            val raza= binding.spinnerRazaEncontrado.selectedItem.toString()
            val ubicacion= binding.spinnerUbicacionEncontrado.selectedItem.toString()
            val descripcion= binding.etDescripcionEncontrado.text.toString()

            if (validarCampos(descripcion)){
                Toast.makeText(this,"Campos validos",Toast.LENGTH_LONG).show()
                subirDatosAFirebase(tipoMascota, tienePlaca, sexo, raza, ubicacion, descripcion,foto)
            }
        }



    }

    fun validarCampos(descripcion: String):Boolean{

        var bandera=true
        if(descripcion.isEmpty()){
            binding.etDescripcionEncontrado.error= getString(R.string.et_descripcion_encontrado)
            bandera=false
        }
        if(binding.viewFotosEncontrado.drawable==null){
            Toast.makeText(this,"Suba foto de referencia",Toast.LENGTH_LONG).show()
            bandera= false
        }
        return bandera
    }

    fun subirDatosAFirebase(tipo:String, tienePlaca:String, sexo:String, raza:String, ubicacion:String, descripcion: String, foto:String) {
        if (tieneInternet(this)){
            var idEncontrado=0

            database.child("IdExtraviado").get().addOnSuccessListener {dataSnapshot ->
                if(dataSnapshot.exists()){
                    idEncontrado = dataSnapshot.getValue().toString().toInt()
                }
                if (tieneInternet(this)){
                    database.child("Publicacion"+idEncontrado).child("Tipo").setValue(tipo)
                    database.child("Publicacion"+idEncontrado).child("Placa").setValue(tienePlaca)
                    database.child("Publicacion"+idEncontrado).child("Sexo").setValue(sexo)
                    database.child("Publicacion"+idEncontrado).child("Raza").setValue(raza)
                    database.child("Publicacion"+idEncontrado).child("Ubicacion").setValue(ubicacion)
                    database.child("Publicacion"+idEncontrado).child("Descripcion").setValue(descripcion)
                    database.child("Publicacion"+idEncontrado).child("Foto").setValue(foto)
                    database.child("IdEncontrado").setValue(idEncontrado+1)
                    Toast.makeText(this,"Publicación exitosa",Toast.LENGTH_LONG).show()
                    limpiarCampos()
                    /*val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()*/
                }else{
                    perdioConexion(this)
                    habilitarCampos()
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(this,"Error al crear la cuenta, intente nuavemente más tarde",Toast.LENGTH_LONG).show()

            }
        }
    }

    fun limpiarCampos(){
        binding.etDescripcionEncontrado.setText("")
    }
    fun desabilitarCampos(){
        binding.btnSubirImg.isEnabled=false
        binding.btnPublicarEncontrado.isEnabled=false
        binding.etDescripcionEncontrado.isEnabled=false
    }
    fun habilitarCampos(){
        binding.btnSubirImg.isEnabled=false
        binding.btnPublicarEncontrado.isEnabled=false
        binding.etDescripcionEncontrado.isEnabled=false
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