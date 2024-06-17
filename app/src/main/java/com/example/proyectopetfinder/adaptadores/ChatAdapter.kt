package com.example.proyectopetfinder.adaptadores

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.poko.Chat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

class ChatAdapter(val chats : List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolderChat>(){

    class ViewHolderChat(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvUsuario : TextView = itemView.findViewById(R.id.tv_usuario)
        val tvContenido : TextView = itemView.findViewById(R.id.tv_contenido)
        val ivPerfil : ImageView = itemView.findViewById(R.id.iv_perfil)
        val ivImagen : ImageView = itemView.findViewById(R.id.iv_imagen)
        val llChat : LinearLayout = itemView.findViewById(R.id.ll_chat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderChat {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_chat, parent, false)
        return ViewHolderChat(itemView)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolderChat, position: Int) {
        val chat = chats.get(position)
        holder.tvUsuario.text = chat.Origen
        holder.tvContenido.text = chat.Contenido
        if(chat.Origen == "Yo"){
            holder.llChat.setBackgroundColor(R.color.rojoOscuro)
        }
        if((chat.Imagen!= null)||(chat.Imagen!="")){
            holder.ivImagen.setImageBitmap(stringToBitmap(chat.Imagen.toString()))
        }
    }

    fun stringToBitmap(encodedString: String): Bitmap? {
        return try {
            val decodedString: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

}