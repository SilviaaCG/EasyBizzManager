package com.silvia.easybizzmanager3.ui.client


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext

import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GeneradorImagenUri(private val fragment:Fragment) {

    private var callback: ((Uri?) -> Unit)? = null



    private fun openImagePicker() {
        ImagePicker.with(fragment)
            .cropSquare()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    fun agregarImagen(onImageSaved: (Uri?) -> Unit) {
        callback = onImageSaved
        openImagePicker()
    }


    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            guardarImagen(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(fragment.requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            callback?.invoke(null)
        } else {
            Toast.makeText(fragment.requireContext(), "Operación cancelada", Toast.LENGTH_SHORT).show()
            callback?.invoke(null)
        }
    }

    private fun guardarImagen(uri: Uri) {
        try {
            val inputStream = fragment.requireActivity().contentResolver.openInputStream(uri)
            val directory = File(fragment.requireContext().filesDir, "images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, "${generarId()}_image.png")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            MediaScannerConnection.scanFile(
                fragment.requireContext(),
                arrayOf(file.absolutePath),
                null,
                null
            )
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                callback?.invoke(Uri.fromFile(file))
            } else {
                Toast.makeText(fragment.requireContext(), "La imagen no ha sido encontrada", Toast.LENGTH_SHORT).show()
                callback?.invoke(null)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(fragment.requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            callback?.invoke(null)
        }
    }

    private fun generarId(): String {
        return System.currentTimeMillis().toString()
    }
}
