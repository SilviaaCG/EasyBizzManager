package com.silvia.easybizzmanager3.models

data class Servicio(
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val duracion: Int,
    val categoria: String?,
    val fechaInicio: String?,
    val fechaFin: String?
)
