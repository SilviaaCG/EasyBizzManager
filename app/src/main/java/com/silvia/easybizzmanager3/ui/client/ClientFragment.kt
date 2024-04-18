package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentClientBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact

import kotlinx.coroutines.launch
import java.util.Locale


class ClientFragment : Fragment() {

    private var _binding: FragmentClientBinding? = null
    private val clientViewModel: ClientViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentClientBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        lifecycleScope.launch {

            binding.buscadorClientes.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override  fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        volverListaClientes()
                        return true
                    } else {
                        listaBuscada(newText) // Llama a la función para mostrar los resultados de la búsqueda
                        return true
                    }
                }
            })
        }

        binding.floatingActionButton.setOnClickListener{
            agregarCliente()
        }

        return root
    }

    private fun volverListaClientes() {
        clientViewModel.clientList.observe(viewLifecycleOwner){ listaClientes ->
            clientViewModel.clientAdapter.observe(viewLifecycleOwner) {
                it.listaClientes = listaClientes
                it.notifyDataSetChanged()
            }
        }

        }



    private fun listaBuscada(texto: String) {
        val listaFiltrada = mutableListOf<Client>()
        clientViewModel.clientList.observe(viewLifecycleOwner) { listaClientes ->
            for (cliente in listaClientes) {
                if (cliente.nombre.lowercase()?.contains(texto.lowercase(Locale.getDefault())) == true) {
                    listaFiltrada.add(cliente)
                }
            }

            clientViewModel.clientAdapter.observe(viewLifecycleOwner) {
                it.listaClientes = listaFiltrada
                it.notifyDataSetChanged()
            }

        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(activity)
        val decoration = DividerItemDecoration(activity,manager.orientation)
        clientViewModel.setClienteActual(Client("0"," ","Nombre","Apellidos",
            NumberContact("+34",62354334),"Dirección","correo@gmail.com",false))
        clientViewModel.initAdapter({client -> infoClienteClick(client)},{position -> onCallClick(position)})
        clientViewModel.clientAdapter.observe(viewLifecycleOwner){
            binding.clientsRecyclerView.adapter = it
        }
        binding.clientsRecyclerView.layoutManager = manager
        binding.clientsRecyclerView.addItemDecoration(decoration)
    }
    // Botones de los items del RecyclerView

    private fun onCallClick(position: Int){

    }
    private fun infoClienteClick(client: Client){
        clientViewModel.setClienteActual(client)
        clientViewModel.setEditable(false)
        findNavController().navigate(R.id.action_nav_clientes_to_infoClientFragment)
    }

    // Boton agregar cliente
    private fun agregarCliente(){
        clientViewModel.setEditable(true)
        clientViewModel.setClienteActual(Client())
        findNavController().navigate(R.id.action_nav_clientes_to_infoClientFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}