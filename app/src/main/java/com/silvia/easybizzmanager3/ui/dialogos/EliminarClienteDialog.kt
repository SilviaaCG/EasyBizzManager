package com.silvia.easybizzmanager3.ui.dialogos

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.silvia.easybizzmanager3.R

class EliminarClienteDialog: DialogFragment() {
    interface EliminarClienteDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }
    private lateinit var listener: EliminarClienteDialog.EliminarClienteDialogListener

    fun setEliminarClienteDialogListener(listener: EliminarClienteDialog.EliminarClienteDialogListener) {
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Estás seguro de que quieres eliminar este cliente?")
                .setIcon(R.drawable.eliminar_cliente_cian_24)
                .setPositiveButton("Eliminar",
                    DialogInterface.OnClickListener { dialog, _ ->
                        listener.onDialogPositiveClick(this)
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, _ ->
                        listener.onDialogNegativeClick(this)
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}