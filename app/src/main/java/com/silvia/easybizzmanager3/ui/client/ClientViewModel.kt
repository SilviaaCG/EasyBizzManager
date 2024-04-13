package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.silvia.easybizzmanager3.managment.ClientManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import kotlinx.coroutines.flow.Flow

class ClientViewModel : ViewModel() {
    private val _clientList = MutableLiveData<ArrayList<Client>>().apply {
        value = ArrayList<Client>().apply {
            add(Client(
                "1","https://img.freepik.com/free-psd/3d-illustration-person-with-sunglasses_23-2149436188.jpg?size=338&ext=jpg&ga=GA1.1.1700460183.1712361600&semt=ais",
                "Paco",
                "Garcia",
                NumberContact("34", 2334232),
                "Calle 4 Ondara",
                "Paco@gmail.com",
                false
            ))
            add(Client(
                "2","https://img.freepik.com/psd-gratis/representacion-3d-avatar_23-2150833572.jpg",
                "Paquita",
                "Salas",
                NumberContact("33", 343242),
                "Calle 34 Madrid",
                "PakiSalas@gmail.com",
                true
            ))
            add(
                Client(
                    "3","https://www.gstatic.com/webp/gallery/1.jpg", // Cliente 1
                    "Juan",
                    "Pérez",
                    NumberContact("1", 123456789),
                    "Calle Principal 123",
                    "juan@example.com",
                    false
                )
            )
            add(
                Client(
                    "4","https://www.gstatic.com/webp/gallery/2.jpg", // Cliente 2
                    "María",
                    "Gómez",
                    NumberContact("34", 987654321),
                    "Avenida Secundaria 456",
                    "maria@example.com",
                    true
                )
            )
            add(
                Client(
                    "5","https://www.gstatic.com/webp/gallery/3.jpg", // Cliente 3
                    "Pedro",
                    "Rodríguez",
                    NumberContact("34", 567890123),
                    "Plaza Central 789",
                    "pedro@example.com",
                    false
                )
            )
            add(
                Client(
                    "6","https://www.gstatic.com/webp/gallery/4.jpg", // Cliente 4
                    "Luisa",
                    "Martínez",
                    NumberContact("34", 321098765),
                    "Calle Secundaria 321",
                    "luisa@example.com",
                    true
                )
            )
            add(
                Client(
                    "7","https://www.gstatic.com/webp/gallery/5.jpg", // Cliente 5
                    "Antonio",
                    "Sánchez",
                    NumberContact("34", 789012345),
                    "Avenida Principal 654",
                    "antonio@example.com",
                    false
                )
            )}

    }
    val clientList: LiveData<ArrayList<Client>> = _clientList

    fun setClientList(clientList: ArrayList<Client>){
        _clientList.value = clientList
    }
    // Operaciones CRUD para la lista
    val clientManagment = ClientManagment()
    fun agregarClienteNuevo(cliente:Client){
        clientManagment.agregarCliente(cliente)
    }
    fun actualizarCliente(id:String,clienteActualizado: Client){
        clientManagment.actualizarCliente(id,clienteActualizado)
    }
    fun eliminarCliente(id: String){
        clientManagment.eliminarCliente(id)
    }
    fun buscarClientePorNombre(nombre: String, apellidos: String): String {
        _clientList.value!!.forEach {cliente ->
            if (cliente.nombre.equals(nombre) && cliente.apellidos.equals(apellidos)) {
                return cliente.id
            }
        }
        return "NO ID"
    }
    suspend fun recuperaClientes(){
        val clientesFlow: Flow<List<Client>> = clientManagment.obtenerClientesFlow()
        val listaClientes = ArrayList<Client>()
        clientesFlow.collect { lista ->
            listaClientes.addAll(lista)
        }
        _clientList.value = listaClientes
    }

    private val _clientAdapter = MutableLiveData<ClientAdapter>()
    val clientAdapter: LiveData<ClientAdapter> = _clientAdapter
    // botones de cada cliente en sus items
    fun initAdapter(onItemSelected: (Client) -> Unit, onClickCall:(Int) -> Unit ){
        _clientAdapter.value = ClientAdapter(
            clientList.value!!,
            onClickListener = { client-> onItemSelected(client) },
            onClickCall = { int -> onClickCall(int) }
        )
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