package com.silvia.easybizzmanager3.ui.dialogos


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.DialogoAgregarPagoBinding
import com.silvia.easybizzmanager3.models.Pago
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AgregarPagoDialog(private val callback: (Pago) -> Unit): DialogFragment() {
    private val binding by lazy { DialogoAgregarPagoBinding.inflate(requireActivity().layoutInflater) }
    private val metodoPagoEditText by lazy { binding.metodoPagoEditText }
    private val cantidadEditText  by lazy { binding.cantidadEditText }
    private val fechaEditText by lazy { binding.fechaEditText }
    private lateinit var pago:Pago


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            fechaEditText.setText(obtenerFechaActual())
            fechaEditText.setOnClickListener {
                val datePicker = DatePickerFragment { day, month, year ->
                    fechaEditText.setText("$day-${month+1}-$year") }
                datePicker.show(requireActivity().supportFragmentManager, "datePicker")
            }
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Nuevo pago.")
                .setIcon(R.drawable.billete_cian_24)
                .setView(binding.root)
                .setPositiveButton("Guardar",
                    DialogInterface.OnClickListener { dialog, _ ->
                        if (inputsCorrectos()){
                            pago = Pago(metodoPagoEditText.text.toString(),
                                cantidadEditText.text.toString().toDouble(),
                                fechaEditText.text.toString())
                            callback(pago)
                            dialog.dismiss()
                        }else{
                            Toast.makeText(requireContext(),"Alguno de los campos son inválidos o estan vacíos", Toast.LENGTH_SHORT).show()
                        }

                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(requireContext(),"Operación cancelada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    })
            return builder.create()

    }


    fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val fechaActual = Date()
        return formato.format(fechaActual)
    }

    private fun inputsCorrectos(): Boolean {
        val metodoPago = metodoPagoEditText.text.toString().trim()
        val cantidadStr = cantidadEditText.text.toString().trim()
        val fecha = fechaEditText.text.toString().trim()

        // Verificar si los campos están vacíos
        if (metodoPago.isEmpty() || cantidadStr.isEmpty() || fecha.isEmpty()) {
            return false
        }

        // Verificar si la cantidad es un número decimal válido
        return try {
            val cantidad = cantidadStr.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }

    }
}