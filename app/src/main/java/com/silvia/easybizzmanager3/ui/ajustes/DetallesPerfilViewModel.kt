package com.silvia.easybizzmanager3.ui.ajustes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silvia.easybizzmanager3.models.DetallesPerfil

class DetallesPerfilViewModel : ViewModel() {
    private val _detallesActuales =  MutableLiveData<DetallesPerfil>().apply {
        value = DetallesPerfil()
    }
    val detallesActuales: LiveData<DetallesPerfil> = _detallesActuales
    fun insertarDetallesActuales(detalles: DetallesPerfil){
        _detallesActuales.value = detalles
    }

}