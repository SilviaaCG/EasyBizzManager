package com.silvia.easybizzmanager3.ui.factura

import PrintDocumentAdapterWrapper
import android.content.Context.PRINT_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentInfoFacturaBinding
import com.silvia.easybizzmanager3.managment.FacturaManagment
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.ui.dialogos.AgregarPagoDialog
import com.silvia.easybizzmanager3.ui.dialogos.EliminarFacturaDialog
import com.silvia.easybizzmanager3.ui.dialogos.EliminarPagoDialog
import com.silvia.easybizzmanager3.ui.productos_servicios.ServiciosProductosAdapter
import com.squareup.picasso.Picasso
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InfoFacturaFragment: Fragment()  {
    private var _binding: FragmentInfoFacturaBinding? = null
    private val binding get() = _binding!!
    private val facturaViewModel: FacturaViewModel by activityViewModels()
    private val facturaManagment: FacturaManagment = FacturaManagment()
    private var accion:Int = 0
    private lateinit var facturaActual: Factura
    private lateinit var listaServiciosProductos: ArrayList<Item>
    private var cantidadPagada: Double = 0.0

    /**LAYOUT**/
    private val numeroFacturaTextView by lazy { binding.numeroFacturaTextView }
    private val fechaEmisionFacturaTextView by lazy { binding.fechaEmisionFacturaTextView }
    private val importeTotalTextView by lazy { binding.importeTotalTextView }
    private val importePagadoTextView by lazy { binding.importePagadoTextView }
    private val estadoImageView by lazy { binding.estadoImageView }
    private val clienteImageView by lazy { binding.clientImageView }
    private val nombreClienteTextView by lazy { binding.NombreClienteTextView }
    private val impuestosTextView by lazy { binding.impuestosTextView }
    private val descuentoTextView by lazy { binding.descuentoTextView }
    private val infoAdicionalTextView by lazy { binding.infoAdicionalTextView }
    private val itemsRecyclerView by lazy { binding.productosServiciosRecyclerView }
    private val historialPagosRecyclerView by lazy { binding.pagosRecyclerView }
    private val añadirPagoButton by lazy { binding.aAdirPagoButton }
    private val imprimirButton by lazy { binding.imprimirButton }
    private val compartirButton by lazy { binding.compartirButton }
    private val editarFacturaButton by lazy { binding.editarFacturaButton }
    private val eliminarButton by lazy { binding.eliminarButton }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_facturas)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentInfoFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initVariables()
        initRecyclerViews()
        initComponentes()
        initButtons()



        return root
    }



    private fun generarPDF() {
        val pdfGenerator = GeneradorPDFFactura(requireActivity())
        facturaViewModel.insertarFileActual(File(pdfGenerator.generarYGuardarFacturaPdf(facturaActual)))
    }


    private fun initRecyclerViews() {
        //RecyclerView Items
        if (facturaActual.serviciosProductos != null){
            itemsRecyclerView.adapter = ServiciosProductosAdapter(false){position -> onClickItem(position)}
            (itemsRecyclerView.adapter as ServiciosProductosAdapter).setListaServiciosProductos(facturaActual.serviciosProductos!!)
            itemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
        //RecyclerView pagos
        if(facturaActual.pagos != null){
            historialPagosRecyclerView.adapter = PagoAdapter{position -> onPagoClickListener(position)}
            (historialPagosRecyclerView.adapter as PagoAdapter).setListaPagos(facturaActual.pagos!!)
            historialPagosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun onPagoClickListener(position: Int) {
        val eliminarPagoDialog = EliminarPagoDialog()
        eliminarPagoDialog.setEliminarPagoDialogListener(object : EliminarPagoDialog.EliminarPagoDialogListener {
            override fun onDialogPositiveClick(dialog: DialogFragment) {
                facturaActual.pagos!!.removeAt(position)
                calcularCantidadPagada()
                calcularEstado()
                (historialPagosRecyclerView.adapter as PagoAdapter).setListaPagos(facturaActual.pagos!!)
                facturaViewModel.actualizarFactura(facturaActual.numeroFactura.toInt(),facturaActual)
                generarPDF()
                Snackbar.make(requireView(), "Se ha eliminado el pago correctamente", Snackbar.LENGTH_SHORT).show()
            }
            override fun onDialogNegativeClick(dialog: DialogFragment) {
                eliminarPagoDialog.dismiss()
                Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
            }
        })
        eliminarPagoDialog.show(requireActivity().supportFragmentManager, "EliminarPagoDialog")
    }

    private fun onClickItem(position:Int){
        Log.d("INFO","Item position: $position")
    }


    private fun initButtons() {
        añadirPagoButton.setOnClickListener {
        if (!facturaPagada()){
            if (facturaActual.pagos!=null){
                val agregarPagoDialog = AgregarPagoDialog{pago ->
                    facturaActual.pagos!!.add(pago)
                    calcularCantidadPagada()
                    calcularEstado()
                    generarPDF()
                    (historialPagosRecyclerView.adapter as PagoAdapter).setListaPagos(facturaActual.pagos!!)
                    facturaViewModel.actualizarFactura(facturaActual.numeroFactura.toInt(),facturaActual)
                }
                agregarPagoDialog.show(requireActivity().supportFragmentManager,"agregarPagoDialog")
            }


        }else{
            Snackbar.make(requireView(), "Factura ya pagada", Snackbar.LENGTH_SHORT).show()
        }
        }

        editarFacturaButton.setOnClickListener {
            facturaViewModel.insertarFacturaActual(facturaActual)
            facturaViewModel.insertarAccionFactura(2)
            findNavController().navigate(R.id.action_contenedorFacturaFragment_to_editarFacturaFragment)
        }


        eliminarButton.setOnClickListener {
            val dialogoEliminarFactura = EliminarFacturaDialog()
            dialogoEliminarFactura.setEliminarFacturaDialogListener(object :EliminarFacturaDialog.EliminarFacturaDialogListener{
                override fun onDialogPositiveClick(dialog: DialogFragment) {
                    facturaManagment.eliminarFactura(facturaActual.numeroFactura.toInt())
                    findNavController().navigate(R.id.action_contenedorFacturaFragment_to_nav_facturas)
                    Snackbar.make(requireView(), "Se ha eliminado la factura correctamente", Snackbar.LENGTH_SHORT).show()
                }

                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    dialogoEliminarFactura.dismiss()
                    Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
                }
            })
            dialogoEliminarFactura.show(requireActivity().supportFragmentManager,"EliminarFacturaDialog")
        }


        compartirButton.setOnClickListener {
            // Generar el PDF
            generarPDF()
            val pdfFile = facturaViewModel.fileActual.value

            if (pdfFile != null && pdfFile.exists()) {
                // Obtener el URI del archivo PDF
                val pdfUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".provider",
                    File(requireContext().filesDir, "factura_${facturaActual.numeroFactura}.pdf")
                )

                if (pdfUri != null) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        type = "application/pdf"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    // Iniciar el intent
                    startActivity(Intent.createChooser(intent, "Compartir PDF"))
                } else {
                    Toast.makeText(requireContext(), "No se encontró el archivo PDF", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(), "No se encontró el archivo PDF", Toast.LENGTH_SHORT).show()
            }
        }

        imprimirButton.setOnClickListener {
            // Generar el PDF
            generarPDF()

            val pdfFile = facturaViewModel.fileActual.value

            if (pdfFile != null && pdfFile.exists()) {
                val pdfUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".provider",
                    pdfFile // Usamos el pdfFile directamente
                )

                val printManager = requireActivity().getSystemService(PRINT_SERVICE) as PrintManager
                val jobName = "${getString(R.string.app_name)} Document"

                try {
                    printManager.print(jobName, PrintDocumentAdapterWrapper(requireContext(), pdfUri), null)
                } catch (e: Exception) {
                    // Manejar errores de impresión
                    Toast.makeText(requireContext(), "Error al imprimir el documento", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Maneja el caso en el que no se encuentra el archivo PDF
            }
        }
    }


    private fun calcularEstado(  ) {
        val fechaActual = LocalDate.now()
        if (!facturaPagada()){
            if (facturaActual.fechaValidez != null && facturaActual.fechaValidez!!.isNotEmpty()){
                if (fechaActual.isAfter(LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))){
                        facturaActual.estado = Factura.EstadoFactura.RETRASO
                    }
                    else if (fechaActual.isBefore(LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))&& facturaActual.pagos!!.isNotEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PAGO_PARCIAL
                    }else if(fechaActual.isBefore(LocalDate.parse(
                        facturaActual.fechaValidez,
                        DateTimeFormatter.ofPattern("dd-M-yyyy")))&& facturaActual.pagos!!.isEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PENDIENTE
                    }
            }else{
                if (facturaActual.pagos!!.isNotEmpty()){
                    facturaActual.estado = Factura.EstadoFactura.PAGO_PARCIAL
                }else{
                    facturaActual.estado = Factura.EstadoFactura.PENDIENTE

                }

            }
        }else{
            facturaActual.estado = Factura.EstadoFactura.PAGADO
        }


        actualizarEstado()
    }

    private fun facturaPagada(): Boolean {
        return if (cantidadPagada >= facturaActual.importeTotal){
             true
        }else{
            false
        }
    }
    private fun calcularCantidadPagada(){

        if (facturaActual.pagos != null){
            var suma = 0.0
            for (pago in facturaActual.pagos!!){
                suma += pago.cantidad
            }
            cantidadPagada = suma

        }else{
            cantidadPagada = 0.0
        }
        importePagadoTextView.text = cantidadPagada.toString()

    }

    private fun initComponentes() {
        if (facturaActual!=null){
            numeroFacturaTextView.text = "#" + facturaActual.numeroFactura
            fechaEmisionFacturaTextView.text = facturaActual.fechaEmision
            importeTotalTextView.text = facturaActual.importeTotal.toString()
            calcularCantidadPagada()
            calcularEstado()
            if (!facturaActual.comprador.image.isNullOrEmpty()){
                Picasso.get().load(facturaActual.comprador.image ).transform(CircleTransform()).into(clienteImageView)

            }
            nombreClienteTextView.text = facturaActual.comprador.nombre + " " + facturaActual.comprador.apellidos
            impuestosTextView.text = facturaActual.impuestos.toString()
            descuentoTextView.text = facturaActual.descuento.toString()
            if (facturaActual.informacionAdicional!=null){
                infoAdicionalTextView.text = facturaActual.informacionAdicional
            }
        }


    }

    private fun actualizarEstado() {
        when (facturaActual.estado){
            Factura.EstadoFactura.SIN_ESTADO ->{
                estadoImageView.setImageResource(R.drawable.label_azul)

            }
            Factura.EstadoFactura.PENDIENTE -> {

                estadoImageView.setImageResource(R.drawable.label_pendiente)
            }
            Factura.EstadoFactura.PAGO_PARCIAL -> {

                estadoImageView.setImageResource(R.drawable.label_pagoparcial)
            }
            Factura.EstadoFactura.RETRASO -> {

                estadoImageView.setImageResource(R.drawable.label_retraso)
            }
            Factura.EstadoFactura.PAGADO -> {
                estadoImageView.setImageResource(R.drawable.label_pagado)
            }
        }
    }

    private fun initVariables() {
        facturaActual = facturaViewModel.facturaActual.value!!
        accion = facturaViewModel.accionFactura.value!!
    }
}