package com.example.proyectopetfinder.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopetfinder.R
import com.example.proyectopetfinder.poko.Chat

class ChatAdapter(val chats : List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolderChat>(){

    class ViewHolderChat(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvUsuario : TextView = itemView.findViewById(R.id.tv_usuario)
        val tvContenido : TextView = itemView.findViewById(R.id.tv_contenido)
        val ivPerfil : ImageView = itemView.findViewById(R.id.iv_perfil)
        val ivImagen : ImageView = itemView.findViewById(R.id.iv_imagen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderChat {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_chat, parent, false)
        return ChatAdapter.ViewHolderChat(itemView)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ViewHolderChat, position: Int) {
        TODO("Not yet implemented")
    }

}