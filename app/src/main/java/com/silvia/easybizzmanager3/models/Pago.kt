package com.silvia.easybizzmanager3.models

data class Pago(
    var modoPago: String,
    var cantidad: Double,
    var fecha: String) {
    constructor() : this("", 0.0, "")
}

