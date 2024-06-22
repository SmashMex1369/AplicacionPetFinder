package com.example.proyectopetfinder.poko

data class PublicacionExtravio(
    var nombre:String = "Nombre",
    var tipo:String="Tipo",
    var sexo:String="Sexo",
    var edad:Long?=0,
    var fecha:String="00/00/0000",
    var descripcion:String="Descripcion",
    var raza:String="Raza",
    var ubicacion: String="Ubicacion",
    var foto:String="",
    var idExtraviado:Long?=0,
    var idUsuario:Long?=0
){
    constructor():this("Nombre","Tipo", "Sexo", 0,"00/00/0000","Descripcion","Raza","Unicacion","",0,0)
}
