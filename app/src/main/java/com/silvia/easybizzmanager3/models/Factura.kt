package com.silvia.easybizzmanager3.models

import java.sql.Timestamp

data class Factura(
    val numeroFactura: String,
    val fechaEmision: String,
    val vendedor: String,
    val comprador: Client,
    val direccionVendedor: String?,
    val direccionComprador: String?,
    val importeTotal: Double,
    val impuestos: Double,
    val metodoPago: String?,
    val informacionAdicional: String?,
    val productos: List<Producto>?,
    val servicios:List<Servicio>?,
    val descuento: Double?
){
    constructor() : this(
        "",
        "",
        "",
        Client(),
        "",
        "",
        0.0,
        0.0,
        "",
        null,
        null,
        null,
        null
    )
}