package com.silvia.easybizzmanager3.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.ClientManagment
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.factura.FacturaAdapter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val _email = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val email: LiveData<String> = _email
    fun setEmail(email:String){
        _email.value = email
    }
    /**Lista y adapter de CLIENTES**/
    private val clientManagment:ClientManagment = ClientManagment()
    private val _listaClientesFavs = MutableLiveData<ArrayList<Client>>()
    val listaClientesFavs:LiveData<ArrayList<Client>> = _listaClientesFavs

    fun initListaClientesFavs(){
        viewModelScope.launch {
            val listaClientes = clientManagment.obtenerClientesFlow()
            val arrayListClientes  = ArrayList<Client>()
            for (cliente in listaClientes){
                if(cliente.favorito) {
                    arrayListClientes.add(cliente)
                }

            }
            insertarListaClientesFavs(arrayListClientes)
        }
    }
    fun insertarListaClientesFavs(lista:ArrayList<Client>){
        _listaClientesFavs.value = lista
        _clientesFavsAdapter.value!!.setLista(_listaClientesFavs.value!!)
    }
    private val _clientesFavsAdapter = MutableLiveData<ClientesFavsAdapter>()
    val clientesFavsAdapter: LiveData<ClientesFavsAdapter> = _clientesFavsAdapter
    fun initClientesFavsAdapter(onItemClickListener: (Client) -> Unit){
        _clientesFavsAdapter.value = ClientesFavsAdapter(onItemClickListener)
        if (_listaClientesFavs.value != null){
            _clientesFavsAdapter.value!!.setLista(_listaClientesFavs.value!!)
        }
    }


    /**Lista y adapter de FACTURAS**/
    private val facturaManagment:FacturaManagment = FacturaManagment()
    private val _listaFacturasPendientes = MutableLiveData<ArrayList<Factura>>()
    val listaFacturasPendientes:LiveData<ArrayList<Factura>> = _listaFacturasPendientes
    fun initListaFacturasPendientes(){
        viewModelScope.launch {
            val listaFacturas = facturaManagment.obtenerFacturas()
            val arrayListFacturas  = ArrayList<Factura>()
            for (factura in listaFacturas){
                if (!facturaPagada(factura)){
                arrayListFacturas.add(factura)
                }
            }
            insertarListaFacturasPendientes(arrayListFacturas)
        }
    }


    private fun facturaPagada(facturaActual: Factura): Boolean {
        var pagos = 0.0
        if (facturaActual.pagos != null){
            for (pago in facturaActual.pagos!!){
                pagos += pago.cantidad
            }
        }
        if (pagos >= facturaActual.importeTotal){
            return true
        }else{
            return false
        }
    }

    fun insertarListaFacturasPendientes(lista:ArrayList<Factura>){
        _listaFacturasPendientes.value = lista
        _facturasPendientesAdapter.value!!.setLista(_listaFacturasPendientes.value!!)
    }
    private val _facturasPendientesAdapter = MutableLiveData<FacturasAdapter>()
    val facturasPendientesAdapter: LiveData<FacturasAdapter> = _facturasPendientesAdapter
    fun initFacturasPendientesAdapter(onItemClickListener: (Factura) -> Unit){
        _facturasPendientesAdapter.value = FacturasAdapter(false,onItemClickListener)
        if (_listaFacturasPendientes.value != null){
            _facturasPendientesAdapter.value!!.setLista(_listaFacturasPendientes.value!!)
        }
    }

    //Factura actual
    private val _facturaActual = MutableLiveData<Factura>().apply {
        value = Factura()
    }
    val  facturaActual: LiveData<Factura> = _facturaActual
    fun insertarFacturaActual(factura: Factura){
        _facturaActual.value = factura
    }
    //Cliente actual
    private val _clienteActual = MutableLiveData<Client>().apply {
        value = Client()
    }
    val clienteActual:LiveData<Client> = _clienteActual
    fun insertarClienteActual(cliente: Client){

    }

    private val _accion = MutableLiveData<Int>().apply {
        value = 3
    }
    val accion:LiveData<Int> = _accion


}