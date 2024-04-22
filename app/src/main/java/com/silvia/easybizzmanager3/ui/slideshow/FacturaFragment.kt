package com.silvia.easybizzmanager3.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentFacturaBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.client.ClientViewModel

class FacturaFragment : Fragment() {

    private var _binding:FragmentFacturaBinding? = null
    private val binding get() = _binding!!

    private val facturaViewModel: FacturaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        initBotones()


        return root
    }

    private fun initBotones() {
        binding.agregarFacturaButton.setOnClickListener {
            facturaViewModel.insertarFacturaActual(Factura())
            findNavController().navigate(R.id.action_nav_facturas_to_infoFacturaFragment)
        }
    }

    private fun initRecyclerView() {
        val manager = LinearLayoutManager(activity)
        val decoration = DividerItemDecoration(activity,manager.orientation)
        facturaViewModel.initFacturaAdapter { factura ->  onInfoFacturaClick(factura) }
        facturaViewModel.facturaAdapter.observe(viewLifecycleOwner){
            binding.facturasRecyclerView.adapter = it
        }
        binding.facturasRecyclerView.layoutManager = manager
        binding.facturasRecyclerView.addItemDecoration(decoration)
    }
    private fun onInfoFacturaClick(factura: Factura){
    //    facturaViewModel.setClienteActual(client)
    //    factura.setEditable(false)
    //    findNavController().navigate(R.id.action_nav_clientes_to_infoClientFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}