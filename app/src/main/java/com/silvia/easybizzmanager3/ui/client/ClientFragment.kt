package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentClientBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.factura.FacturaViewModel
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
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_clientes)
        ajustes().insertarTitulo( "Clientes",requireActivity() as AppCompatActivity)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)
        _binding = FragmentClientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        volverButton()
        initRecyclerView()
        initBuscadorClientes()



        binding.agregarClienteButton.setOnClickListener{
            agregarCliente()
        }

        return root
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_nav_clientes_to_nav_home)
        }
    }

    private fun initBuscadorClientes() {
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
                if (cliente.nombre.trim().lowercase()?.contains(texto.lowercase(Locale.getDefault())) == true || cliente.apellidos.trim().lowercase()?.contains(texto.lowercase(Locale.getDefault())) == true) {
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
        clientViewModel.initAdapter { client -> infoClienteClick(client) }
        clientViewModel.clientAdapter.observe(viewLifecycleOwner){
            binding.clientsRecyclerView.adapter = it

        }

        binding.clientsRecyclerView.layoutManager = manager
    }


    private fun infoClienteClick(client: Client){
        clientViewModel.setClienteActual(client)
        clientViewModel.insertarAccion(2)
        findNavController().navigate(R.id.action_nav_clientes_to_infoClientFragment)
    }


    private fun agregarCliente(){
        clientViewModel.insertarAccion(1)
        clientViewModel.setClienteActual(Client())
        findNavController().navigate(R.id.action_nav_clientes_to_editarClientFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}