package com.silvia.easybizzmanager3.models

data class DetallesPerfil (
    var nombre: String?,
    var apellidos: String?,
    var nombreEmpresa: String?,
    var numeroEmpresa: String?,
    var numeroContacto: NumberContact?,
    var correo: String,
    var correoContacto: String?,
    var direccion: String?

){
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        "",
        null,
        null
    )
}


