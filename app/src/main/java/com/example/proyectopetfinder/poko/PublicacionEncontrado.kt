package com.example.proyectopetfinder.poko

data class PublicacionEncontrado(
    var tipo:String = "Tipo",
    var placa:Boolean=false,
    var fecha:String="00/00/0000",
    var sexo:String="Sexo",
    var raza:String="Raza",
    var ubicacion: String="Ubicacion",
    var foto:String="",
    var idEncontrado: Long=0,
    var idUusario:Long=0
)
