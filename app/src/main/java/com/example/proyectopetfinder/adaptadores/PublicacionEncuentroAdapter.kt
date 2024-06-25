package com.example.proyectopetfinder.adaptadores

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.interfaces.ListenerRecyclerEncontrado
import com.example.proyectopetfinder.interfaces.ListenerRecyclerExtraviado
import com.example.proyectopetfinder.poko.PublicacionEncontrado

class PublicacionEncuentroAdapter(private val publicacionesEncuentro:List<PublicacionEncontrado>, private var listenerPublicacion:ListenerRecyclerEncontrado):
    RecyclerView.Adapter<PublicacionEncuentroAdapter.PublicacionEncuentroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): PublicacionEncuentroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_publicacion_encuentro,parent,false)
        return PublicacionEncuentroViewHolder(view) 
    }

    override fun onBindViewHolder(holder: PublicacionEncuentroViewHolder, position: Int) {
        val item = publicacionesEncuentro[position]
        holder.bind(item)
        holder.cardPublicacion.setOnClickListener{
            listenerPublicacion.clicPublicacion(item)
        }
    }

    override fun getItemCount(): Int {
        return publicacionesEncuentro.size
    }

    class PublicacionEncuentroViewHolder(itemView: View):ViewHolder(itemView){
        val tvFecha:TextView=itemView.findViewById(R.id.tv_fecha_encuentro_recycler)
        val tvTipo:TextView=itemView.findViewById(R.id.tv_tipo_encuentro_recycler)
        val tvPlaca:TextView=itemView.findViewById(R.id.tv_placa_encuentro_recycler)
        val tvSexo:TextView=itemView.findViewById(R.id.tv_sexo_encuentro_recycler)
        val imgvFoto:ImageView=itemView.findViewById(R.id.imgv_publicacion_encuentro)
        val cardPublicacion:CardView=itemView.findViewById(R.id.card_encuentro)

        var descripcion:String=""
        var ubicacion:String=""
        var idExtraviado:Long?=0
        var idUsuario:Long? = 0

        fun bind(publicacion:PublicacionEncontrado){
            tvFecha.text = publicacion.fecha
            tvTipo.text=publicacion.tipo
            tvSexo.text=publicacion.sexo

            descripcion=publicacion.descripcion
            ubicacion=publicacion.ubicacion
            idExtraviado=publicacion.idEncontrado
            idUsuario=publicacion.idUsuario

            if(publicacion.placa==true){
                tvPlaca.text="Tiene placa"
            }else{
                tvPlaca.text="No tiene placa"
            }

            if(publicacion.foto.isNotEmpty() ){
                val stringDecodificado = Base64.decode(publicacion.foto,Base64.DEFAULT)
                val byteDecodificado = BitmapFactory.decodeByteArray(stringDecodificado,0,stringDecodificado.size)
                imgvFoto.setImageBitmap(byteDecodificado)
            }
        }


    }


}