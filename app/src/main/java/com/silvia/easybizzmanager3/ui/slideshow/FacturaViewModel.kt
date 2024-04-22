package com.silvia.easybizzmanager3.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.models.Factura
import kotlinx.coroutines.launch

class FacturaViewModel : ViewModel() {
    val facturaManagment:FacturaManagment = FacturaManagment()
    init {
        initListaFacturas()
    }

    private val _listaFacturas = MutableLiveData<ArrayList<Factura>>()
    val listaFacturas: LiveData<ArrayList<Factura>> = _listaFacturas
    fun insertarListaFacturas(listaFacturasNueva:ArrayList<Factura>){
        _listaFacturas.value = listaFacturasNueva
    }
    fun initListaFacturas(){
        viewModelScope.launch {
            val listaFacturas = facturaManagment.obtenerFacturas()
            val arrayListFacturas  = ArrayList<Factura>()
            for (factura in listaFacturas){
                arrayListFacturas.add(factura)
            }
            insertarListaFacturas(arrayListFacturas)
        }
    }

    private val _facturaAdapter = MutableLiveData<FacturaAdapter>()
    val facturaAdapter: LiveData<FacturaAdapter> = _facturaAdapter
    fun initFacturaAdapter(onInfoClickListener: (Factura) -> Unit){
        _facturaAdapter.value = FacturaAdapter(onInfoClickListener = onInfoClickListener)
        if (_listaFacturas.value != null){
            _facturaAdapter.value!!.setListaFacturas(_listaFacturas.value!!)
        }
    }

    private val _facturaActual = MutableLiveData<Factura>().apply {
        value = Factura()
    }
    val  facturaActual: LiveData<Factura> = _facturaActual
    fun insertarFacturaActual(factura: Factura){
        _facturaActual.value = factura
    }

}