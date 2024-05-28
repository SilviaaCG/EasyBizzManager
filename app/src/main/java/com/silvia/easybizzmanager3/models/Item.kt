package com.silvia.easybizzmanager3.models

import java.util.UUID

data class Item(
    val id:String,
    var nombre: String,
    var imagen: String?,
    var categoria: String?,
    var descripcion: String?,
    var precioUnitario: Double,
    var cantidad: Int,
    var precioTotal:Double,
    var isProducto: Boolean
) {

    fun generarId():String{
        return UUID.randomUUID().toString()
    }
    constructor() : this("","", null, "", "", 0.0, 0,0.0,false)
}
