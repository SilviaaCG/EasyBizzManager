package com.silvia.easybizzmanager3.ui.factura

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentFacturaBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import kotlinx.coroutines.launch
import java.util.Locale

class FacturaFragment : Fragment() {

    private var _binding: FragmentFacturaBinding? = null
    private val binding get() = _binding!!

    private val facturaViewModel: FacturaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_facturas)
        ajustes().insertarTitulo( "Facturas",requireActivity() as AppCompatActivity)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)
        volverButton()
        initRecyclerView()
        initBotones()
        initBuscador()

        return root
    }

    private fun initBuscador() {
        lifecycleScope.launch {
            binding.buscador.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override  fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty()) {
                        volverListaFacturas()
                        return true
                    } else {
                        listaBuscada(newText) // Llama a la función para mostrar los resultados de la búsqueda
                        return true
                    }
                }
            })
        }
    }

    private fun listaBuscada(newText: String) {
        val listaFiltrada = mutableListOf<Factura>()
        facturaViewModel.listaFacturas.observe(viewLifecycleOwner) { listaFacturas ->
            for (fac in listaFacturas) {
                if (fac.numeroFactura.trim().lowercase()?.contains(newText.trim().lowercase(Locale.getDefault())) == true || fac.fechaEmision.trim().lowercase()?.contains(newText.trim().lowercase(
                        Locale.getDefault())) == true || fac.comprador.nombre.trim().lowercase()?.contains(newText.trim().lowercase(
                        Locale.getDefault())) == true || fac.comprador.apellidos.trim().lowercase()?.contains(newText.trim().lowercase(
                        Locale.getDefault())) == true)  {
                    listaFiltrada.add(fac)
                }
            }

            facturaViewModel.facturaAdapter.observe(viewLifecycleOwner) {
                it.setListaFacturas(listaFiltrada as ArrayList<Factura>)
            }

        }
    }

    private fun volverListaFacturas() {
        facturaViewModel.listaFacturas.observe(viewLifecycleOwner){ listaFacturas ->
            facturaViewModel.facturaAdapter.observe(viewLifecycleOwner) {
                it.setListaFacturas(listaFacturas)
            }
        }
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_nav_facturas_to_nav_home)

        }
    }
    private fun initBotones() {
        binding.agregarFacturaButton.setOnClickListener {
            facturaViewModel.insertarFacturaActual(Factura())
            facturaViewModel.insertarAccionFactura(1)
            findNavController().navigate(R.id.action_nav_facturas_to_editarFacturaFragment)
        }
    }

    private fun initRecyclerView() {
        facturaViewModel.initListaFacturas()
        val manager = LinearLayoutManager(activity)
        facturaViewModel.initFacturaAdapter { factura ->  onInfoFacturaClick(factura) }
        facturaViewModel.facturaAdapter.observe(viewLifecycleOwner){
            binding.facturasRecyclerView.adapter = it
        }
        binding.facturasRecyclerView.layoutManager = manager
    }
    private fun onInfoFacturaClick(factura: Factura){
        facturaViewModel.insertarFacturaActual(factura)
        facturaViewModel.insertarAccionFactura(2)
        findNavController().navigate(R.id.action_nav_facturas_to_contenedorFacturaFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}