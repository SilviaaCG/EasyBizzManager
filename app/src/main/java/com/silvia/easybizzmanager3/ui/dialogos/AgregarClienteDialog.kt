package com.silvia.easybizzmanager3.ui.dialogos

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.DialogoAgregarClienteBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import com.silvia.easybizzmanager3.models.generarId

class AgregarClienteDialog(private val callback: (Client) -> Unit): DialogFragment() {

    private val binding by lazy { DialogoAgregarClienteBinding.inflate(requireActivity().layoutInflater) }
    private val nombre by lazy { binding.nombreEditText }
    private val apellidos by lazy { binding.apellidosEditText }
    private val telefono by lazy { binding.fixSpinner.selectedItem }
    private val numeroTelefono by lazy { binding.numeroContactoEditText }
    private val direccion by lazy { binding.direccionEditText }
    private val correo by lazy { binding.correoEditText }
    private lateinit var cliente: Client

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
            .setTitle("Agregar nuevo cliente")
            .setIcon(R.drawable.add_cliente_cian)
            .setPositiveButton("Guardar") { dialog, _ ->

                if (inputsCorrectos()) {
                    cliente = Client(
                        generarId(),
                        null,
                        nombre.text.toString(),
                        apellidos.text.toString(),
                        if(!numeroTelefono.text.isNullOrEmpty())
                            NumberContact(telefono.toString(),numeroTelefono.text.toString().toInt())
                        else null,
                        if(!direccion.text.isNullOrEmpty()) direccion.text.toString() else null,
                        if(!correo.text.isNullOrEmpty()) correo.text.toString().trim() else null,
                        false
                    )
                    callback(cliente)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(),"Error. Es necesario introducir el nombre y los apeliidos.", Toast.LENGTH_SHORT).show()
                    dialog!!.dismiss()
                }
            }
        return builder.create()
    }

    private fun inputsCorrectos(): Boolean {
        val nombreText = nombre.text.toString().trim()
        val apellidosText = apellidos.text.toString().trim()
        val numeroTelefonoText = numeroTelefono.text.toString().trim()

        // Verifica que el nombre, los apellidos y el número de teléfono no estén vacíos
        return nombreText.isNotEmpty() && apellidosText.isNotEmpty() && numeroTelefonoText.isNotEmpty()
    }


}
