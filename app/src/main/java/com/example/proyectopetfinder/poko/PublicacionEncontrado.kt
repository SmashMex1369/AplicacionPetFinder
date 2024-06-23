package com.example.proyectopetfinder.poko

data class PublicacionEncontrado(
    var tipo:String = "Tipo",
    var placa:Boolean?=false,
    var fecha:String="00/00/0000",
    var sexo:String="Sexo",
    var raza:String="Raza",
    var ubicacion: String="Ubicacion",
    var descripcion:String="Descripcion",
    var foto:String="",
    var idEncontrado: Long?=0,
    var idUsuario:Long?=0
){
    constructor():this("Tipo",false,"00/00/0000","Sexo","Raza","Ubicacion","Descripcion","",0,0)
}
