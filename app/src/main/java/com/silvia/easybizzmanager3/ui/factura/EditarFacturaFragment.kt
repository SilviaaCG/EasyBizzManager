package com.silvia.easybizzmanager3.ui.factura

import com.silvia.easybizzmanager3.ui.dialogos.AgregarClienteDialog
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.client.ClientViewModel
import com.silvia.easybizzmanager3.ui.productos_servicios.ProductosYServiciosViewModel
import com.silvia.easybizzmanager3.ui.productos_servicios.ServiciosProductosAdapter
import com.silvia.easybizzmanager3.databinding.FragmentEditarFacturaBinding
import com.silvia.easybizzmanager3.managment.AuthManager
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.DetallesPerfil
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.NumberContact
import com.silvia.easybizzmanager3.ui.dialogos.AgregarItemDialog
import com.silvia.easybizzmanager3.ui.dialogos.DatePickerFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.round


class EditarFacturaFragment : Fragment() {
    private var _binding: FragmentEditarFacturaBinding? = null
    private val binding get() = _binding!!
    private val facturaViewModel: FacturaViewModel by activityViewModels()
    private val clienteViewModel: ClientViewModel by activityViewModels()
    private val serviciosProductosViewModel: ProductosYServiciosViewModel by activityViewModels()
    private var accion:Int = 0
    private lateinit var facturaActual: Factura
    private lateinit var listaServiciosProductos: ArrayList<Item>
    private val recyclerViewItems by lazy { binding.productosServiciosRecyclerView }
    private val buscadorClientes by lazy { binding.itemClientNombreTextView }
    private val buscadorItems by lazy { binding.selectorItemsTextView }

    private var precio = 0.0
    private var impuestos = 0
    private var descuentos = 0
    private lateinit var detallesPerfil: DetallesPerfil
    private var clienteSeleccionado: Client? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ajustes().esconderAppBar(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditarFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initVariables()
        volverButton()
        initComponentes()
        initButtons()
        return root
    }

    @SuppressLint("ResourceType")
    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (accion==2){
                facturaViewModel.insertarFacturaActual(facturaActual)
                facturaViewModel.insertarAccionFactura(2)
                findNavController().navigate(R.id.action_editarFacturaFragment_to_contenedorFacturaFragment)
            }else{
                findNavController().navigate(R.id.action_editarFacturaFragment_to_nav_facturas)
                Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun precios(){
        var precioImpuestos = ((precio * impuestos)/100).roundToTwoDecimals()
        var precioConImpuestos = (precio + precioImpuestos).roundToTwoDecimals()
        var precioDescuento = ((descuentos * precioConImpuestos)/100).roundToTwoDecimals()
        var precioTotal = (precioConImpuestos - precioDescuento).roundToTwoDecimals()
        binding.subtotalTextView.text = precio.toString()
        binding.precioTotalTextView.text =   precioTotal.toString()
        binding.descuentoTextView.text = precioDescuento.toString()
        binding.precioImpuestosTextView.text = precioImpuestos.toString()
    }




    private fun initButtons() {
        binding.agregarProductoServicioButton.setOnClickListener {
            serviciosProductosViewModel.insertarAccionItem(1)
            val dialogoAgregarItem = AgregarItemDialog(null) { item ->
                listaServiciosProductos.add(item)
                (recyclerViewItems.adapter as ServiciosProductosAdapter).setListaServiciosProductos(
                    listaServiciosProductos
                )
                precioItems()
            }
            dialogoAgregarItem.show(requireActivity().supportFragmentManager, "s")
        }

        binding.agregarClienteButton.setOnClickListener {
            val dialogoAgregarCliente = AgregarClienteDialog { cliente ->
                clienteExiste(cliente) { existe, clienteEncontrado ->
                    if (existe) {
                        Snackbar.make(requireView(), "El cliente ya existe.", Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        clienteViewModel.agregarClienteNuevo(cliente)
                        lifecycleScope.launch {
                            clienteViewModel.actualizarClientList()
                        }
                        clienteSeleccionado = cliente
                        if (!cliente.image.isNullOrEmpty()){
                            Picasso.get().load(cliente.image).transform(CircleTransform()).into(binding.clientImageView)
                        }
                        buscadorClientes.setText(cliente.nombre + " " + cliente.apellidos)

                    }
                }
            }
            dialogoAgregarCliente.show(requireActivity().supportFragmentManager, "s")
        }
        binding.fechaVencimientoEditText.setOnClickListener {
            val datePicker =
                DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
            datePicker.show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.guardarButton.setOnClickListener {
            var factura = Factura()
            if (compruebaInputs()) {

                detallesPerfil.nombreEmpresa = binding.nombreEmpresaEditText.text.toString()
                detallesPerfil.numeroEmpresa = binding.numeroEmpresaEditText.text.toString()
                detallesPerfil.numeroContacto =
                    if (binding.numeroContactoEditText.text!!.isNotEmpty())
                        NumberContact(
                            binding.fixSpinner.selectedItem.toString(),
                            binding.numeroContactoEditText.text.toString().toInt()
                        ) else null
                detallesPerfil.correoContacto = binding.correoEditText.text.toString()
                detallesPerfil.direccion = binding.ubicacionEditText.text.toString()

                when (accion) {
                    1 -> {
                        factura = Factura(
                            facturaViewModel.generarNumeroFactura().toString(),
                            obtenerFechaFormateada().toString(),
                            binding.fechaVencimientoEditText.text.toString(),
                            detallesPerfil,
                            clienteSeleccionado!!,
                            //binding.selectorClienteSpinner.selectedItem as Client,
                            binding.precioTotalTextView.text.toString().toDouble(),
                            if (binding.impuestosEditText.text!!.isNotEmpty()) binding.impuestosEditText.text.toString()
                                .toInt() else 0,
                            if (binding.descuentoEditText.text!!.isNotEmpty()) binding.descuentoEditText.text.toString()
                                .toInt() else 0,
                            ArrayList(),
                            binding.infoAdicionalFacturaEditText.text.toString(),
                            listaServiciosProductos,
                            obtenerEstado(binding.fechaVencimientoEditText.text.toString())
                        )
                        facturaViewModel.agregarFacturaNueva(factura)
                        facturaViewModel.insertarFacturaActual(factura)
                        facturaViewModel.insertarAccionFactura(2)
                        Snackbar.make(requireView(), "Factura creada correctamente", Snackbar.LENGTH_SHORT).show()

                    }

                    2 -> {
                        factura = Factura(
                            facturaActual.numeroFactura,
                            facturaActual.fechaEmision,
                            binding.fechaVencimientoEditText.text.toString(),
                            detallesPerfil,
                            clienteSeleccionado!!,
                            binding.precioTotalTextView.text.toString().toDouble(),
                            if (binding.impuestosEditText.text!!.isNotEmpty()) binding.impuestosEditText.text.toString()
                                .toInt() else 0,
                            if (binding.descuentoEditText.text!!.isNotEmpty()) binding.descuentoEditText.text.toString()
                                .toInt() else 0,
                            facturaActual.pagos,
                            binding.infoAdicionalFacturaEditText.text.toString(),
                            listaServiciosProductos,
                            obtenerEstado(binding.fechaVencimientoEditText.text.toString())
                        )
                        facturaViewModel.actualizarFactura(factura.numeroFactura.toInt(), factura)
                        facturaViewModel.insertarFacturaActual(factura)
                        facturaViewModel.insertarAccionFactura(2)
                        Snackbar.make(requireView(), "Factura modificada correctamente", Snackbar.LENGTH_SHORT).show()
                    }


                }
                findNavController().navigate(R.id.action_editarFacturaFragment_to_contenedorFacturaFragment)
            }
        }
    }

    private fun compruebaInputs(): Boolean {
        var isValid = true

        //Comprobar cliente comprador
        if (clienteSeleccionado == null){
            buscadorClientes.error = "Selecciona un cliente."

            isValid =  false
        }
        // Obtener el texto del EditText

        val nombreEmpresa = binding.nombreEmpresaEditText.text.toString()

        // Comprobar si el campo está vacío
        if (nombreEmpresa.isNullOrEmpty()) {
            // Establecer mensaje de error
            binding.nombreEmpresaEditText.error = "El nombre de la empresa no puede estar vacío"
            isValid = false
        } else {
            // Limpiar mensaje de error si no está vacío
            binding.nombreEmpresaEditText.error = null

        }

        return isValid

    }

    fun Double.roundToTwoDecimals(): Double {
        return round(this * 100) / 100
    }

    fun obtenerEstado(fechaVencimiento: String): Factura.EstadoFactura {
        val fechaActual = LocalDate.now()
        var estado = Factura.EstadoFactura.SIN_ESTADO
        var draw: Int = 0
        if (binding.fechaVencimientoEditText.text!!.isNotEmpty()) {
            if (facturaActual.pagos == null) {
                when {
                    fechaActual.isBefore(
                        LocalDate.parse(
                            fechaVencimiento,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")
                        )
                    ) -> {
                       estado =  Factura.EstadoFactura.PENDIENTE
                    }

                    fechaActual.isAfter(
                        LocalDate.parse(
                            fechaVencimiento,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")
                        )
                    ) -> {
                        // Retrasado
                        estado = Factura.EstadoFactura.RETRASO
                    }

                    else -> {
                        estado = Factura.EstadoFactura.SIN_ESTADO
                    }
                }

            } else if (facturaActual.pagos!!.isNotEmpty()) {
                estado = Factura.EstadoFactura.PAGO_PARCIAL
            }
        }

        return estado
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        insertStringOnEditText("$day-${month+1}-$year",binding.fechaVencimientoEditText)
    }

    private fun obtenerFechaFormateada(): Any {
        val fechaActual = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return fechaActual.format(formatter)
    }


    private fun clienteExiste(clienteNuevo: Client,callback: (Boolean, Client?) -> Unit) {
        val clients = clienteViewModel.clientList.value ?: emptyList()
        var existe = false
        var clienteEncontrado: Client? = null
        for (client in clients) {
            if (client.nombre.equals(clienteNuevo.nombre.trim(),ignoreCase = true)
                && client.apellidos.equals(clienteNuevo.apellidos.trim(),ignoreCase = true)) {
                existe = true
                clienteEncontrado = client
                break
            }
        }
        callback(existe, clienteEncontrado)
        }


    private fun initVariables() {
        accion = facturaViewModel.accionFactura.value!!
        facturaActual = facturaViewModel.facturaActual.value!!


    }

    private fun initComponentes() {

        /**initSpinnerProductos()**/
        when(accion){
            // desde button agregar
            1 -> {
                listaServiciosProductos = ArrayList<Item>()
                AuthManager().recuperarDetalles { detallesRecuperado  ->
                    if (detallesRecuperado != null) {
                        detallesPerfil = detallesRecuperado
                        rellenarDetalles()
                    }
                }
                clienteSeleccionado = null


            }
            // desde button editar de una factura
            2 -> {
                rellenarCampos(facturaActual)
                detallesPerfil = facturaActual.detallesPerfil

            }
        }
        initSpinnerClientes()
        initSpinnerProductos()
        initRecyclerViewItems()
        initListenerPrecio()
        precios()
    }

    private fun rellenarDetalles() {
        if (detallesPerfil!= null){
        if (!detallesPerfil.nombreEmpresa.isNullOrEmpty()){
            insertStringOnEditText(detallesPerfil.nombreEmpresa!!,binding.nombreEmpresaEditText)
        }
        if (!detallesPerfil.numeroEmpresa.isNullOrEmpty()){
            insertStringOnEditText(detallesPerfil.numeroEmpresa!!,binding.numeroEmpresaEditText)
        }
        if (detallesPerfil.numeroContacto != null){
            //spinnerseleccion
            insertStringOnEditText(detallesPerfil.numeroContacto!!.number.toString(),binding.numeroContactoEditText)
        }
        if (!detallesPerfil.correoContacto.isNullOrEmpty()){
            insertStringOnEditText(detallesPerfil.correoContacto!!,binding.correoEditText)
        }
        if (!detallesPerfil.direccion.isNullOrEmpty()){
            insertStringOnEditText(detallesPerfil.direccion!!,binding.ubicacionEditText)
        }}
    }

    private fun initListenerPrecio() {
        binding.impuestosEditText.addTextChangedListener {
            if (it!!.isNotEmpty() && it.toString().toInt() > 0){
                if (it.toString().toInt() > 100){
                    binding.impuestosEditText.error  = "No puede ser un porcentaje mayor de 100"

                }else{
                    impuestos = it.toString().toInt()
                    precios()
                }
            }else{
                impuestos = 0
            }

        }
        binding.descuentoEditText.addTextChangedListener {
            if (it!!.isNotEmpty() && it.toString().toInt() > 0){
                if (it.toString().toInt() > 100) {
                    binding.impuestosEditText.error = "No puede ser un porcentaje mayor de 100"
                }else{
                    descuentos = it.toString().toInt()
                    precios()
                }
            }else{
                descuentos = 0
            }
        }
    }

    private fun initSpinnerProductos() {
        serviciosProductosViewModel.listaItems.observe(viewLifecycleOwner){items->
            val adapter = BuscadorItemsAdapter(requireContext(),items){ item ->
                serviciosProductosViewModel.insertarAccionItem(2)
                val dialogoAgregarItem = AgregarItemDialog(item) { item ->
                    listaServiciosProductos.add(item)
                    (recyclerViewItems.adapter as ServiciosProductosAdapter).setListaServiciosProductos(
                        listaServiciosProductos
                    )
                    precioItems()
                }
                dialogoAgregarItem.show(requireActivity().supportFragmentManager, "s")
            }
            buscadorItems.setAdapter(adapter)
        }
    }

    private fun initSpinnerClientes() {
        clienteViewModel.clientList.observe(viewLifecycleOwner){clientes ->
            val adapter = BuscadorClienteAdapter(requireContext(), clientes){ cliente ->
                onClienteSelected(cliente)
                buscadorClientes.setText("${cliente.nombre} ${cliente.apellidos}")
            }
            buscadorClientes.setAdapter(adapter)
        }
    }

    private fun onClienteSelected(cliente: Client) {
        if (!cliente.image.isNullOrEmpty()){
            Picasso.get().load(cliente.image).transform(CircleTransform()).into(binding.clientImageView)
        }
        clienteSeleccionado = cliente


    }

    private fun initRecyclerViewItems() {
        if (listaServiciosProductos != null){
            recyclerViewItems.adapter = ServiciosProductosAdapter(true) { position -> onClickItem(position)}
            (recyclerViewItems.adapter as ServiciosProductosAdapter).setListaServiciosProductos(listaServiciosProductos)
            recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        }
        precioItems()
    }

    private fun precioItems() {
        var sumaPrecio = 0.0
        for (item in listaServiciosProductos){
            sumaPrecio += item.precioTotal
        }
        precio = sumaPrecio
        binding.subtotalTextView.text = precio.toString()
        precios()
    }

    private fun onClickItem(position:Int){
        listaServiciosProductos.removeAt(position)
        (recyclerViewItems.adapter as ServiciosProductosAdapter).setListaServiciosProductos(listaServiciosProductos)
        precioItems()
    }

    private fun rellenarCampos(facturaActual: Factura) {
        if (!facturaActual.comprador.image.isNullOrEmpty()){
            Picasso.get().load(facturaActual.comprador.image).transform(CircleTransform()).into(binding.clientImageView)
        }
        clienteSeleccionado = facturaActual.comprador
        buscadorClientes.setText(facturaActual.comprador.nombre + " " + facturaActual.comprador.apellidos)
        if (facturaActual.detallesPerfil.nombreEmpresa != null){
            insertStringOnEditText(facturaActual.detallesPerfil.nombreEmpresa!!,binding.nombreEmpresaEditText)
        }
        if (facturaActual.detallesPerfil.numeroEmpresa != null){
            insertStringOnEditText(facturaActual.detallesPerfil.numeroEmpresa!!,binding.numeroEmpresaEditText)
        }
        if (facturaActual.detallesPerfil.numeroContacto != null){
            //spinnerseleccion
            insertStringOnEditText(facturaActual.detallesPerfil.numeroContacto!!.number.toString(),binding.numeroContactoEditText)
        }
        if (facturaActual.detallesPerfil.correoContacto != null){
            insertStringOnEditText(facturaActual.detallesPerfil.correoContacto!!,binding.correoEditText)
        }
        if (facturaActual.detallesPerfil.direccion != null){
            insertStringOnEditText(facturaActual.detallesPerfil.direccion!!,binding.ubicacionEditText)
        }
        if (facturaActual.impuestos != null){
            insertStringOnEditText(facturaActual.impuestos.toString(),binding.impuestosEditText)
            precios()
        }
        if(facturaActual.descuento != null){
            insertStringOnEditText(facturaActual.descuento.toString(),binding.descuentoEditText)
        }
        if (facturaActual.informacionAdicional != null){
            insertStringOnEditText(facturaActual.informacionAdicional,binding.infoAdicionalFacturaEditText)
        }
        if (facturaActual.serviciosProductos == null){
            facturaActual.serviciosProductos = ArrayList<Item>()
        }
        if (facturaActual.fechaValidez !=null){
            insertStringOnEditText(facturaActual.fechaValidez!!,binding.fechaVencimientoEditText)
        }

        listaServiciosProductos = facturaActual.serviciosProductos!!
        recyclerViewItems.adapter = ServiciosProductosAdapter(false) { position -> onClickItem(position)}
        (recyclerViewItems.adapter as ServiciosProductosAdapter).setListaServiciosProductos(listaServiciosProductos)
        recyclerViewItems.layoutManager = LinearLayoutManager(requireContext())
        precioItems()
    }
    private fun insertStringOnEditText(string: String, editText: EditText){
        editText.text  = Editable.Factory.getInstance().newEditable(string)
    }



}