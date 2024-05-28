package com.silvia.easybizzmanager3.models

import java.util.UUID

data class Client(
    val id:String,
    var image:String?,
    val nombre:String,
    val apellidos:String,
    val number:NumberContact?,
    val direccion:String?,
    val correo:String?,
    var favorito:Boolean){
    constructor() : this(
        "0",
        null,
        "",
        "",
        null,
        null,
        null,
        false
    )
}
fun generarId():String{
    return UUID.randomUUID().toString()
}



