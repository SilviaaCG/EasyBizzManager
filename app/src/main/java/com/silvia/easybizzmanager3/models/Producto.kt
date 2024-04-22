package com.silvia.easybizzmanager3.models

data class Producto(
    val nombre: String,
    val descripcion: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val categoria: String?,
    val codigoBarras: String?,
    val proveedor: String?
)
