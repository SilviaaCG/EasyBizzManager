package com.silvia.easybizzmanager3.ui.dialogos

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.DialogoAgregarProductoServicioBinding
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.generarId
import com.silvia.easybizzmanager3.ui.productos_servicios.ProductosYServiciosViewModel

class AgregarItemDialog(private val item1: Item?,private val callback: (Item) -> Unit): DialogFragment() {

    private val binding by lazy { DialogoAgregarProductoServicioBinding.inflate(requireActivity().layoutInflater) }
    private val isServicioButton by lazy { binding.isServicioButton }
    private val isProductoButton by lazy { binding.isProductoButton }
    private val grupoButtons by lazy { binding.grupoButtons }
    private val nombreEditText by lazy { binding.nombreEditText }
    private val categoriaEditText by lazy { binding.categoriaEditText }
    private val descripcionEditText by lazy { binding.descripcionEditText }
    private val precioUnitarioEditText by lazy { binding.precioUnitarioEditText }
    private val cantidadEditText by lazy { binding.cantidadEditText }
    private lateinit var item: Item
    private val viewModel: ProductosYServiciosViewModel by activityViewModels()

    private fun inputsCorrectos(): Boolean {

        val nombre = nombreEditText.text.toString().trim()
        val precioUnitario = precioUnitarioEditText.text.toString().trim()
        val cantidad = cantidadEditText.text.toString().trim()

        // Verificar que el nombre, precio unitario y cantidad no estén vacíos
        if (nombre.isEmpty()) {
            nombreEditText.error = "Este campo es obligatorio."
            return false
        }
        if (precioUnitario.isEmpty()){
            precioUnitarioEditText.error = "Este campo es obligatorio."
            return false
        }
        if (cantidad.isEmpty()){
            cantidadEditText.error = "Inserta una cantidad."
            return false
        }

        // Verificar que el precio unitario y la cantidad sean números válidos
        return try {
            precioUnitario.toDouble()
            cantidad.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root.apply {
            rellenarInputs()
        })
            .setTitle("Agregar nuevo Producto o Servicio")
            .setIcon(R.drawable.add_servicio_producto_cian)
            .setPositiveButton("Guardar"){dialog, _ ->

                when(viewModel.accionItem.value){
                    1 ->{
                        crearItemNuevo()
                    }
                    2 ->{
                        seleccionarItem()
                    }
                }

            }
            .setNegativeButton("Cancelar",
                DialogInterface.OnClickListener { dialog, _ ->
                    Toast.makeText(requireContext(),"Operación cancelada", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                })
        return builder.create()


    }

    private fun seleccionarItem() {
        if (inputsCorrectos()) {
            item = Item(
                generarId(),
                nombreEditText.text.toString(),
                null,
                categoriaEditText.text.toString(),
                descripcionEditText.text.toString(),
                precioUnitarioEditText.text.toString().toDouble(),
                cantidadEditText.text.toString().toInt(),
                (precioUnitarioEditText.text.toString()
                    .toDouble() * cantidadEditText.text.toString().toInt()),
                isProducto()
            )
            callback(item)
            dialog!!.dismiss()
        }else{
            Toast.makeText(requireContext(),"Error. Es necesario introducir la cantidad.", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }
    }

    private fun rellenarInputs() {
        when(viewModel.accionItem.value){
            1 ->{
                item = Item()
            }
            2 ->{
                item = item1!!
                if (item.isProducto){
                    isProductoButton.isChecked = true
                }else{
                    isServicioButton.isChecked = true
                }
                grupoButtons.isEnabled = false

                ajustes().insertStringOnEditText(item.nombre,nombreEditText)
                nombreEditText.isEnabled = false

                if (!item.categoria.isNullOrEmpty()){
                    ajustes().insertStringOnEditText(item.categoria!!, categoriaEditText)
                }
                categoriaEditText.isEnabled = false

                if (!item.descripcion.isNullOrEmpty()){
                    ajustes().insertStringOnEditText(item.descripcion!!, descripcionEditText)
                }
                descripcionEditText.isEnabled = false

                ajustes().insertStringOnEditText(item.precioUnitario.toString(), precioUnitarioEditText)
                precioUnitarioEditText.isEnabled = false

            }
        }

    }


    private fun crearItemNuevo() {
        if (inputsCorrectos()){
            item = Item(
                generarId(),
                nombreEditText.text.toString(),
                null,
                if(!categoriaEditText.text.isNullOrEmpty()) categoriaEditText.text.toString() else null,
                if(!descripcionEditText.text.isNullOrEmpty()) descripcionEditText.text.toString() else null,
                precioUnitarioEditText.text.toString().toDouble(),
                cantidadEditText.text.toString().toInt(),
                (precioUnitarioEditText.text.toString().toDouble() * cantidadEditText.text.toString().toInt()),
                isProducto()
            )
            callback(item)
            viewModel.agregarItemNuevo(Item(item.id,item.nombre,null,
                if (!item.imagen.isNullOrEmpty())item.imagen else null,
                if (!item.descripcion.isNullOrEmpty()) item.descripcion else null,
                item.precioUnitario, 0, 0.0, item.isProducto))
            dialog!!.dismiss()
        }else{
            Toast.makeText(requireContext(),"Error. Es necesario introducir el nombre, el precio y la cantidad.", Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
        }
    }

    private fun isProducto(): Boolean {
        return when {
            isProductoButton.isChecked -> true
            isServicioButton.isChecked -> false
            else -> throw IllegalArgumentException("Ningún botón está seleccionado")
        }
    }
}