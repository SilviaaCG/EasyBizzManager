package com.silvia.easybizzmanager3.ui.client

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentImagenBinding
import com.silvia.easybizzmanager3.databinding.FragmentInfoClientBinding

class ImagenFragment : Fragment() {

    private var _binding: FragmentImagenBinding? = null
    private val binding get() = _binding!!


    companion object {
        // URI de la imagen seleccionada o capturada
        private var imageUri: Uri? = null

        // Códigos de permisos y solicitudes para la selección de imágenes y la apertura de la cámara
        private const val PERMISSION_CODE_IMAGE_PICK = 1000
        private const val IMAGE_PICK_CODE = 1001
        private const val PERMISSION_CODE_CAMERA_CAPTURE = 2000
        private const val OPEN_CAMERA_CODE = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagenBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }



}