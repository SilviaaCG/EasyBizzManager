package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.ClientManagment
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.models.NumberContact
import com.silvia.easybizzmanager3.ui.home.FacturasAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {
    // Operaciones CRUD para la lista
    val clientManagment = ClientManagment()

    private val _clientList = MutableLiveData<ArrayList<Client>>().apply {
        viewModelScope.launch {
            actualizarClientList()
        }

    }

    val clientList: LiveData<ArrayList<Client>> = _clientList

    suspend fun actualizarClientList() {
        val listaClientes = ArrayList<Client>()
        val lista = clientManagment.obtenerClientesFlow()
        for (cliente in lista){
            listaClientes.add(cliente)
        }
        _clientList.value = listaClientes
    }
    fun agregarClienteNuevo(cliente:Client){
        clientManagment.agregarCliente(cliente)
    }
    fun actualizarCliente(id:String,clienteActualizado: Client){
        clientManagment.actualizarCliente(id,clienteActualizado)
    }
    fun eliminarCliente(id: String){
        clientManagment.eliminarCliente(id)
    }

    private val _clientAdapter = MutableLiveData<ClientAdapter>()
    val clientAdapter: LiveData<ClientAdapter> = _clientAdapter
    // botones de cada cliente en sus items
    fun initAdapter(onItemSelected: (Client) -> Unit ){
        viewModelScope.launch {
            actualizarClientList()
            _clientAdapter.value = ClientAdapter(
                clientList.value!!
            ) { client -> onItemSelected(client) }
        }

    }
    private val _clienteActual =  MutableLiveData<Client>().apply {
        value = Client("0"," ","Nombre", "Apellidos", NumberContact("34",654564),"Dirección","correo@gmail.com", false)
    }

    val clienteActual:LiveData<Client> = _clienteActual
    fun setClienteActual(cliente: Client?){
        _clienteActual.value = cliente
    }

    private val _accion =  MutableLiveData<Int>()
    val accion: LiveData<Int> = _accion
    fun insertarAccion(accion: Int){
        _accion.value = accion
    }

    /**NUEVOOOOOOOOOOOOOOOO**/

    /**Lista y adapter de FACTURAS**/
    private val facturaManagment: FacturaManagment = FacturaManagment()
    private val _listaFacturasCliente = MutableLiveData<ArrayList<Factura>>()
    val listaFacturasCliente:LiveData<ArrayList<Factura>> = _listaFacturasCliente
    fun initListaFacturasCliente(cliente: Client){
        viewModelScope.launch {
            val listaFacturas = facturaManagment.obtenerFacturas()
            val arrayListFacturas  = ArrayList<Factura>()
            for (factura in listaFacturas){
                if (factura.comprador.nombre.trim().equals(cliente.nombre.trim(),true)
                    && factura.comprador.apellidos.trim().equals(cliente.apellidos.trim(),true)){
                    arrayListFacturas.add(factura) }

            }
            insertarListaFacturasCliente(arrayListFacturas)
        }
    }
    fun insertarListaFacturasCliente(lista:ArrayList<Factura>){
        _listaFacturasCliente.value = lista
        _facturasClienteAdapter.value!!.setLista(_listaFacturasCliente.value!!)
    }
    private val _facturasClienteAdapter = MutableLiveData<FacturasAdapter>()
    val facturasClienteAdapter: LiveData<FacturasAdapter> = _facturasClienteAdapter
    fun initFacturasClienteAdapter(onItemClickListener: (Factura) -> Unit){
        _facturasClienteAdapter.value = FacturasAdapter(true,onItemClickListener)
        if (_listaFacturasCliente.value != null){
            _facturasClienteAdapter.value!!.setLista(_listaFacturasCliente.value!!)
        }
    }




}