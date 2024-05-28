package com.silvia.easybizzmanager3.ui.factura

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.ClientManagment
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import kotlinx.coroutines.launch
import java.io.File

class FacturaViewModel : ViewModel() {
    val facturaManagment:FacturaManagment = FacturaManagment()

    private val _listaFacturas = MutableLiveData<ArrayList<Factura>>()
    val listaFacturas: LiveData<ArrayList<Factura>> = _listaFacturas
    fun insertarListaFacturas(listaFacturasNueva:ArrayList<Factura>){
        _listaFacturas.value = listaFacturasNueva
        if (_facturaAdapter.value!=null){
            _facturaAdapter.value!!.setListaFacturas(_listaFacturas.value!!)
        }

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
    fun agregarFacturaNueva(factura: Factura){
        facturaManagment.agregarFactura(factura)
    }
    fun actualizarFactura(id:Int,facturaActualizada: Factura){
        facturaManagment.actualizarFactura(id,facturaActualizada)
    }
    fun eliminarFactura(id: Int){
        facturaManagment.eliminarFactura(id)
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

    private val _accionFactura = MutableLiveData<Int>().apply {
        value = 0
    }
    val accionFactura: LiveData<Int> = _accionFactura

    /**
     * 0: Ninguna
     * 1: Llamada desde FacturaFragmen desde el botón de agregar
     * 2: Llamada desde FacturaFragment desde un item de la lista
     */
    fun insertarAccionFactura(seleccion:Int){
        _accionFactura.value = seleccion
    }

    fun generarNumeroFactura():Int{
        var numeroMasGrande = Int.MIN_VALUE
        if (listaFacturas.value != null){
            if (listaFacturas.value!!.isNotEmpty()){
                for (factura in listaFacturas.value!!) {
                    if (factura.numeroFactura.toInt() > numeroMasGrande) {
                        numeroMasGrande = factura.numeroFactura.toInt()
                    }
                }
            }else{
                numeroMasGrande = 0
            }
        }
        return numeroMasGrande + 1

    }
    private val _listaClientes = MutableLiveData<ArrayList<Client>>().apply {
        viewModelScope.launch {
            val listaClientes = ClientManagment().obtenerClientesFlow()
            value = ArrayList(listaClientes)
                 }

    }
    val listaClientes: LiveData<ArrayList<Client>> = _listaClientes


    private val _fileActual = MutableLiveData<File>()
    val fileActual: LiveData<File> = _fileActual
    fun insertarFileActual(file: File){
        _fileActual.value = file
    }




}