package com.silvia.easybizzmanager3.models

import java.io.Serializable
import java.sql.Timestamp

data class Factura (
    val numeroFactura: String,
    val fechaEmision: String,
    val fechaValidez:String?,
    val detallesPerfil:DetallesPerfil,
    var comprador: Client,
    val importeTotal: Double,
    val impuestos: Int?,
    val descuento: Int?,
    var pagos:ArrayList<Pago>?,
    val informacionAdicional: String?,
    var serviciosProductos: ArrayList<Item>?,
    var estado:EstadoFactura

){
    enum class EstadoFactura { SIN_ESTADO, PENDIENTE, PAGO_PARCIAL, RETRASO, PAGADO }


    constructor() : this(
        "",
        "",
        null,
        DetallesPerfil(),
        Client(),
        0.0,
        0,
        null,
        ArrayList(),
        "",
        ArrayList(),
        EstadoFactura.SIN_ESTADO
    )
}