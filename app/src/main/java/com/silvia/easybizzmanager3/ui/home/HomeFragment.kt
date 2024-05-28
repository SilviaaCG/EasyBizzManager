package com.silvia.easybizzmanager3.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.silvia.easybizzmanager3.AuthActivity
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentHomeBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.client.ClientViewModel
import com.silvia.easybizzmanager3.ui.factura.FacturaViewModel
import com.silvia.easybizzmanager3.ui.productos_servicios.ProductosYServiciosViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
    lateinit var facturaActual:Factura

    private val homeViewModel: HomeViewModel by activityViewModels()
    private val facturaViewModel: FacturaViewModel by activityViewModels()
    private val clienteViewModel: ClientViewModel by activityViewModels()
    private val itemsViewModel: ProductosYServiciosViewModel by activityViewModels()
    private val agregarClienteButton by lazy { binding.agregarClienteButton}
    private val agregarProductoServicioButton by lazy { binding.agregarProductoServicioButton}
    private val agregarFacturaButton by lazy { binding.agregarFacturaButton}


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {



        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ajustes().seleccionarFragment(requireActivity() as AppCompatActivity, R.id.nav_home)
        ajustes().insertarTitulo( "Inicio",requireActivity() as AppCompatActivity)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)

        volverButton()



        initRecyclerView()
        initButtons()
        initSalvaPantallas()



        return root
    }

    private fun initSalvaPantallas() {
        homeViewModel.listaClientesFavs.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.sinClientesLayout.visibility = View.VISIBLE
                binding.clientesFavsRecyclerView.visibility = View.GONE
            } else {
                binding.sinClientesLayout.visibility = View.GONE
                binding.clientesFavsRecyclerView.visibility = View.VISIBLE
            }
        }
        homeViewModel.listaFacturasPendientes.observe(viewLifecycleOwner) {
        if (it.isEmpty()){
            binding.sinFacturasPendientesLayout.visibility = View.VISIBLE
            binding.facturasPendientesRecyclerView.visibility = View.GONE
        }else{
            binding.sinFacturasPendientesLayout.visibility = View.GONE
            binding.facturasPendientesRecyclerView.visibility = View.VISIBLE
        }}
    }

    private fun initButtons() {
        agregarClienteButton.setOnClickListener{
            clienteViewModel.insertarAccion(1)
            findNavController().navigate(R.id.action_nav_home_to_editarClientFragment)
        }
        agregarProductoServicioButton.setOnClickListener{
            itemsViewModel.insertarAccionItem(1)
            findNavController().navigate(R.id.action_nav_home_to_editarItemsFragment)
        }
        agregarFacturaButton.setOnClickListener{
            facturaViewModel.insertarAccionFactura(1)
            findNavController().navigate(R.id.action_nav_home_to_editarFacturaFragment)
        }

        binding.navClientesButton.setOnClickListener {
            findNavController().navigate(R.id.nav_clientes)
        }
        binding.navFacturasButton.setOnClickListener {
            findNavController().navigate(R.id.nav_facturas)
        }
        binding.navItemsButton.setOnClickListener {
            findNavController().navigate(R.id.contenedorServiciosProductosFragment)
        }
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Está seguro de que quiere cerrar sesión?")
                .setPositiveButton("Sí") { dialog, _ ->
                    FirebaseAuth.getInstance().signOut()
                    showAuth()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun initRecyclerView() {

        /**RecyclerView CLIENTES **/
        homeViewModel.initListaClientesFavs()
        homeViewModel.initClientesFavsAdapter { cliente -> onItemClientesClickListener(cliente) }
        homeViewModel.clientesFavsAdapter.observe(viewLifecycleOwner){
            binding.clientesFavsRecyclerView.adapter = it

        }
        binding.clientesFavsRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)


        /**RecyclerView FACTURAS **/
        homeViewModel.initListaFacturasPendientes()
        homeViewModel.initFacturasPendientesAdapter { factura -> onItemFacturaClickListener(factura) }
        homeViewModel.facturasPendientesAdapter.observe(viewLifecycleOwner){
            binding.facturasPendientesRecyclerView.adapter = it

        }
        binding.facturasPendientesRecyclerView.layoutManager = LinearLayoutManager(activity)
        facturaViewModel.initListaFacturas()
    }
    fun onItemClientesClickListener(cliente:Client){
        clienteViewModel.setClienteActual(cliente)
        clienteViewModel.insertarAccion(2)
        findNavController().navigate(R.id.action_nav_home_to_infoClientFragment)

    }
    fun onItemFacturaClickListener(factura: Factura){
        facturaActual = factura
        facturaViewModel.insertarFacturaActual(factura)
        facturaViewModel.insertarAccionFactura(2)
        findNavController().navigate(R.id.action_nav_home_to_contenedorFacturaFragment)

    }

    private fun showAuth() {
        val authIntent = Intent(context, AuthActivity::class.java)
        startActivity(authIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}