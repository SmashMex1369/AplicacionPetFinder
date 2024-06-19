package com.example.proyectopetfinder.utilidades

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.proyectopetfinder.R

object Internet {
    fun tieneInternet(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivityManager != null){
            @Suppress("DEPRECATION")
            val redActiva = connectivityManager.activeNetworkInfo
            return redActiva !=null && redActiva.isConnected
        }
        return false
    }

    fun perdioConexion(context: Context){
        Toast.makeText(context, ContextCompat.getString(context, R.string.conexion_perdida), Toast.LENGTH_LONG).show()

    }
}