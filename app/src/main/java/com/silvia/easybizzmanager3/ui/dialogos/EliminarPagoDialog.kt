package com.silvia.easybizzmanager3.ui.dialogos

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.silvia.easybizzmanager3.R

class EliminarPagoDialog : DialogFragment() {

    interface EliminarPagoDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    private lateinit var listener: EliminarPagoDialogListener

    fun setEliminarPagoDialogListener(listener: EliminarPagoDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Estás seguro de que quieres eliminar este pago?")
                .setIcon(R.drawable.billete_cian_24)
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