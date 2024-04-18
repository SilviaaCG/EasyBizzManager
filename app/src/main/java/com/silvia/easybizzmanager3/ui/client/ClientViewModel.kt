package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.ClientManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ClientViewModel : ViewModel() {
    // Operaciones CRUD para la lista
    val clientManagment = ClientManagment()

    private val _clientList = MutableLiveData<ArrayList<Client>>()

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
    fun initAdapter(onItemSelected: (Client) -> Unit, onClickCall:(Int) -> Unit ){
        viewModelScope.launch {
            actualizarClientList()
            _clientAdapter.value = ClientAdapter(
                clientList.value!!,
                onClickListener = { client -> onItemSelected(client) },
                onClickCall = { int -> onClickCall(int) }
            )
        }



    }
    private val _clienteActual =  MutableLiveData<Client>().apply {
        value = Client("0"," ","Nombre", "Apellidos", NumberContact("34",654564),"Dirección","correo@gmail.com", false)
    }

    val clienteActual:LiveData<Client> = _clienteActual
    fun setClienteActual(cliente: Client?){
        _clienteActual.value = cliente
    }

    private val _isEditable =  MutableLiveData<Boolean>().apply {
        value = false
    }
    val isEditable: MutableLiveData<Boolean> = _isEditable
    fun setEditable(isEditable: Boolean){
        _isEditable.value = isEditable
    }




}