package com.example.proyectopetfinder.adaptadores

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.interfaces.ListenerRecyclerExtraviado
import com.example.proyectopetfinder.poko.PublicacionExtravio

class PublicacionExtravioAdapter(private val publicacionesExtravio:List<PublicacionExtravio>,private var listenerPublicacion:ListenerRecyclerExtraviado):
    RecyclerView.Adapter<PublicacionExtravioAdapter.PublicacionExtravioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): PublicacionExtravioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_publicacion_extravio,parent,false)
        return PublicacionExtravioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PublicacionExtravioViewHolder, position: Int) {
        val item = publicacionesExtravio[position]
        holder.bind(item)
        holder.cardPublicacion.setOnClickListener{
            listenerPublicacion.clicPublicacion(item)
        }
    }

    override fun getItemCount(): Int {
        return publicacionesExtravio.size
    }

    class PublicacionExtravioViewHolder(itemView: View):ViewHolder(itemView){
        val tvNombre : TextView = itemView.findViewById(R.id.tv_nombre_extravio_recycler)
        val tvFecha:TextView=itemView.findViewById(R.id.tv_fecha_extravio_recycler)
        val tvTipo:TextView=itemView.findViewById(R.id.tv_tipo_extravio_recycler)
        val tvSexo:TextView=itemView.findViewById(R.id.tv_sexo_extravio_recycler)
        val imgvFoto:ImageView=itemView.findViewById(R.id.imgv_publicacion_extravio)
        val cardPublicacion:CardView=itemView.findViewById(R.id.card_extravio)

        var edad : Long? = 0
        var descripcion:String=""
        var ubicacion:String=""
        var idExtraviado:Long?=0
        var idUsuario:Long? = 0

        fun bind(publicacion:PublicacionExtravio){
            tvNombre.text = publicacion.nombre
            tvFecha.text = publicacion.fecha
            tvTipo.text=publicacion.tipo
            tvSexo.text=publicacion.sexo

            edad=publicacion.edad
            descripcion=publicacion.descripcion
            ubicacion=publicacion.ubicacion
            idExtraviado=publicacion.idExtraviado
            idUsuario=publicacion.idUsuario

            if(publicacion.foto.isNotEmpty() ){
                val stringDecodificado = Base64.decode(publicacion.foto,Base64.DEFAULT)
                val byteDecodificado = BitmapFactory.decodeByteArray(stringDecodificado,0,stringDecodificado.size)
                imgvFoto.setImageBitmap(byteDecodificado)
            }
        }


    }


}