package com.silvia.easybizzmanager3.ui.client


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentInfoClientBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.dialogos.EliminarClienteDialog
import com.silvia.easybizzmanager3.ui.factura.FacturaViewModel
import com.squareup.picasso.Picasso


class InfoClientFragment : Fragment() {

    private var _binding: FragmentInfoClientBinding? = null
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val facturaViewModel: FacturaViewModel by activityViewModels()
    private val binding get() = _binding!!
    private val imagenImageView by lazy { binding.clientImageView }
    private val nombreTextView by lazy { binding.nombreTextView }
    private val apellidosTextView by lazy { binding.apellidosTextView }
    private val numeroContactoTextView by lazy { binding.numeroContactoTextView }
    private val correoTextView by lazy { binding.correoTextView }
    private val direccionTextView by lazy { binding.direcionTextView }
    private val facturasRecyclerView by lazy { binding.clientFacturasRecyclerView }
    private val editarButton by lazy { binding.editarButton }
    private val eliminarButton by lazy { binding.eliminarButton }
    private val favButton by lazy { binding.favoritoButton }
    private lateinit var clienteActual: Client


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_clientes)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoClientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initVariables()
        volverButton()
        initComponentes()
        initRecyclerView()
        initButtons()
        return root
    }

    private fun initButtons() {
        editarButton.setOnClickListener {
            clientViewModel.actualizarCliente(clienteActual.id,clienteActual)
            clientViewModel.setClienteActual(clienteActual)
            clientViewModel.insertarAccion(2)
            findNavController().navigate(R.id.action_infoClientFragment_to_editarClientFragment)
        }
        eliminarButton.setOnClickListener {
            val dialogoEliminarCliente = EliminarClienteDialog()
            dialogoEliminarCliente.setEliminarClienteDialogListener(object :EliminarClienteDialog.EliminarClienteDialogListener{
                override fun onDialogPositiveClick(dialog: DialogFragment) {
                    clientViewModel.eliminarCliente(clienteActual.id)
                    findNavController().navigate(R.id.action_infoClientFragment_to_nav_clientes)
                    Snackbar.make(requireView(), "Se ha eliminado el cliente con nombre ${clienteActual.nombre} + ${clienteActual.apellidos} correctamente", Snackbar.LENGTH_SHORT).show()
                }
                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    dialogoEliminarCliente.dismiss()
                    Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
                }
            })
            dialogoEliminarCliente.show(requireActivity().supportFragmentManager,"EliminarClienteDialog")
        }

        favButton.setOnClickListener{
            if (clienteActual.favorito){
                clienteActual.favorito = false
                Snackbar.make(requireView(), "Cliente eliminado de favoritos.", Snackbar.LENGTH_SHORT).show()
            }else{
                clienteActual.favorito = true
                Snackbar.make(requireView(), "Cliente agregado a favoritos.", Snackbar.LENGTH_SHORT).show()
            }
            clientViewModel.actualizarCliente(clienteActual.id,clienteActual)
            actualizarFavButton()
        }




    }

    private fun actualizarFavButton() {
        if (clienteActual.favorito){
            favButton.setIcon(R.drawable.estrella_blanca_rellena_24)
        }else{
            favButton.setIcon(R.drawable.estrella_blanca_vacia_24)
        }
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_infoClientFragment_to_nav_clientes)
        }
    }

    private fun initRecyclerView() {
        clientViewModel.initListaFacturasCliente(clienteActual)
        val manager = LinearLayoutManager(activity)
        clientViewModel.initFacturasClienteAdapter { factura -> onInfoFacturaClick(factura) }
        clientViewModel.facturasClienteAdapter.observe(viewLifecycleOwner){
            facturasRecyclerView.adapter = it
        }
        facturasRecyclerView.layoutManager = manager
    }

    private fun onInfoFacturaClick(factura: Factura) {
        facturaViewModel.insertarAccionFactura(2)
        facturaViewModel.insertarFacturaActual(factura)
        findNavController().navigate(R.id.action_infoClientFragment_to_contenedorFacturaFragment)
    }


    private fun initComponentes() {
        actualizarFavButton()
        if (!clienteActual.image.isNullOrEmpty()){
            Picasso.get().load(clienteActual.image
            ).transform(CircleTransform()).into(imagenImageView)
        }
        nombreTextView.text = clienteActual.nombre
        apellidosTextView.text = clienteActual.apellidos
        if (clienteActual.number!=null){
            numeroContactoTextView.text = clienteActual.number!!.prefix + clienteActual.number!!.number
        }
        if (clienteActual.correo!=null){
            correoTextView.text = clienteActual.correo
        }
        if (clienteActual.direccion!=null){
            direccionTextView.text = clienteActual.direccion
        }

    }

    private fun initVariables() {
        if (clientViewModel.clienteActual.value != null){
            clienteActual = clientViewModel.clienteActual.value!!
        }else{
            Snackbar.make(requireView(), "No se ha podido cargar el cliente", Snackbar.LENGTH_LONG).show()
        }


    }

}