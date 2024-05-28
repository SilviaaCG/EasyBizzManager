package com.silvia.easybizzmanager3.ui.productos_servicios

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.managment.ItemsManagment
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.generarId
import com.silvia.easybizzmanager3.ui.factura.FacturaAdapter
import kotlinx.coroutines.launch

class ProductosYServiciosViewModel : ViewModel() {
    val itemsManagment: ItemsManagment = ItemsManagment()

    private val _listaItems = MutableLiveData<ArrayList<Item>>().apply {
        viewModelScope.launch {
            actualizarItemsList()
        }
    }


    private suspend fun actualizarItemsList() {
        val listaItems = ArrayList<Item>()
        val listaProductos = ArrayList<Item>()
        val listaServicios = ArrayList<Item>()
        val lista = itemsManagment.obtenerItems()

        for (item in lista){
            listaItems.add(item)
            if (item.isProducto){
                listaProductos.add(item)
            }else{
                listaServicios.add(item)
            }
        }
        _listaItems.value = listaItems

        insertarListaProductos(listaProductos)
        insertarListaServicios(listaServicios)

    }

    val listaItems: LiveData<ArrayList<Item>> = _listaItems

    private val _listaProductos = MutableLiveData<ArrayList<Item>>()
    val listaProductos: LiveData<ArrayList<Item>> = _listaProductos

    private fun insertarListaProductos(lista:ArrayList<Item>) {
        _listaProductos.value = lista
        if (_itemAdapterProductos.value!=null){
            _itemAdapterProductos.value!!.setListaItems(_listaProductos.value!!)
        }
    }

    private val _listaServicios = MutableLiveData<ArrayList<Item>>()
    val listaServicios: LiveData<ArrayList<Item>> = _listaServicios

    private fun insertarListaServicios(lista:ArrayList<Item>) {
        _listaServicios.value = lista
        if (_itemAdapterServicios.value != null){
            _itemAdapterServicios.value!!.setListaItems(_listaServicios.value!!)
        }
    }


    fun insertarListaItems(listaFacturasNueva:ArrayList<Item>){
            _listaItems.value = listaFacturasNueva
            if (_itemAdapter.value!=null){
                _itemAdapter.value!!.setListaItems(_listaItems.value!!)
            }




    }
    fun initListaItems(isProducto: Boolean){
        viewModelScope.launch {
            val listaItems = itemsManagment.obtenerItems()
            val listaProductos  = ArrayList<Item>()
            val listaServicios  = ArrayList<Item>()
            val arrayListItems  = ArrayList<Item>()

            for (item in listaItems){
                arrayListItems.add(item)
                if (item.isProducto){
                    listaProductos.add(item)
                }else{
                    listaServicios.add(item)
                }
            }

            _listaItems.value = arrayListItems
            _listaServicios.value = listaServicios
            _listaProductos.value = listaProductos


            insertarListaProductos(listaProductos)
            insertarListaServicios(listaServicios)
            insertarListaItems(arrayListItems)



        }
    }
    fun agregarItemNuevo(item: Item){
        itemsManagment.agregarItem(item)
    }
    fun actualizarItem(id:String,itemActualizado: Item){
        itemsManagment.actualizarItem(id,itemActualizado)
    }
    fun eliminarItem(id: String){
        itemsManagment.eliminarItem(id)
    }

    private val _itemAdapter = MutableLiveData<ItemsAdapter>()
    val itemAdapter: LiveData<ItemsAdapter> = _itemAdapter
    @SuppressLint("SuspiciousIndentation")
    fun initItemAdapter(onItemClickListener: (Item) -> Unit){
        _itemAdapter.value = ItemsAdapter(onItemClickListener)
            if (_listaItems.value!= null){
                _itemAdapter.value!!.setListaItems(_listaItems.value!!)
            }

    }


    private val _itemAdapterServicios = MutableLiveData<ItemsAdapter>()
    val itemAdapterServicios: LiveData<ItemsAdapter> = _itemAdapterServicios
    fun initItemAdapterServicios(onItemClickListener: (Item) -> Unit){
        _itemAdapterServicios.value = ItemsAdapter(onItemClickListener)
        if (_listaServicios.value!= null){
                _itemAdapterServicios.value!!.setListaItems(_listaServicios.value!!)
            }
    }

    private val _itemAdapterProductos = MutableLiveData<ItemsAdapter>()
    val itemAdapterProductos: LiveData<ItemsAdapter> = _itemAdapterProductos
    fun initItemAdapterProductos(onItemClickListener: (Item) -> Unit){
        _itemAdapterProductos.value = ItemsAdapter(onItemClickListener)
        if (_listaProductos.value!= null){
                _itemAdapterProductos.value!!.setListaItems(_listaProductos.value!!)
            }
    }


    private val _itemActual = MutableLiveData<Item>().apply {
        value = Item()
    }
    val  itemActual: LiveData<Item> = _itemActual
    fun insertarItemActual(item: Item){
        _itemActual.value = item
    }

    private val _accionItem = MutableLiveData<Int>().apply {
        value = 0
    }
    val accionItem: LiveData<Int> = _accionItem

    /**
     * 0: Ninguna
     * 1: Llamada desde button crear
     * 2: Llamada desde productos
     */
    fun insertarAccionItem(seleccion: Int) {
        _accionItem.value = seleccion
    }

}