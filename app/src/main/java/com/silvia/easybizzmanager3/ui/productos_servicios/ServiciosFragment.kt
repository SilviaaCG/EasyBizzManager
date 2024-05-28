package com.silvia.easybizzmanager3.ui.productos_servicios

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentServiciosBinding
import com.silvia.easybizzmanager3.models.Item
import kotlinx.coroutines.launch
import java.util.Locale


class ServiciosFragment : Fragment() {
    private var _binding: FragmentServiciosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosYServiciosViewModel by activityViewModels()
        private val recyclerView by lazy { binding.recyclerViewServicio }
    private val buscador by lazy { binding.buscadorServicio }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentServiciosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView(false)

        initBuscadorItems()



        return root
    }

    private fun initBuscadorItems() {
        lifecycleScope.launch {
            buscador.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override  fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        volverListaServicios()
                        return true
                    } else {
                        listaBuscada(newText) // Llama a la función para mostrar los resultados de la búsqueda
                        return true
                    }
                }
            })
        }
    }

    private fun listaBuscada(texto: String) {
        val listaFiltrada = mutableListOf<Item>()
        viewModel.listaServicios.observe(viewLifecycleOwner) { listaItems ->
            for (item in listaItems) {

                if ((item.nombre.trim().lowercase()?.contains(texto.lowercase(Locale.getDefault())) == true ||
                            (if(! item.descripcion.isNullOrEmpty()) item.descripcion else " ")!!.trim().lowercase()?.contains(texto.lowercase(
                                Locale.getDefault())) == true || item.precioUnitario.toString().contains(texto.lowercase(
                        Locale.getDefault())))
                ) {
                    listaFiltrada.add(item)
                }

            }

            viewModel.itemAdapterServicios.observe(viewLifecycleOwner) {
                it.itemsList = listaFiltrada as ArrayList<Item>
                it.notifyDataSetChanged()
            }

        }
    }

    private fun volverListaServicios() {
        viewModel.listaServicios.observe(viewLifecycleOwner){ listaServicios ->
            viewModel.itemAdapterServicios.observe(viewLifecycleOwner) {
                it.itemsList = listaServicios
                it.notifyDataSetChanged()
            }
        }
    }


    private fun initRecyclerView(isProducto: Boolean) {
        viewModel.initListaItems(isProducto)
        val manager = LinearLayoutManager(activity)
        viewModel.initItemAdapterServicios{item ->  onItemClick(item)}
        viewModel.itemAdapterServicios.observe(viewLifecycleOwner){
            recyclerView.adapter = it
        }
        recyclerView.layoutManager = manager
    }

    private fun onItemClick(item: Item) {
        viewModel.insertarItemActual(item)
        viewModel.insertarAccionItem(2)
        findNavController().navigate(R.id.action_contenedorServiciosProductosFragment_to_infoProductosYServiciosFragment)
    }
}