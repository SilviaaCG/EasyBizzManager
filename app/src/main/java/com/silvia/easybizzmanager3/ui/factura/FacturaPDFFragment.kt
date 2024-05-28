package com.silvia.easybizzmanager3.ui.factura


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentFacturaPDFBinding
import com.silvia.easybizzmanager3.databinding.FragmentInfoFacturaBinding
import com.silvia.easybizzmanager3.models.Factura
import java.io.File
import kotlin.properties.Delegates


class FacturaPDFFragment : Fragment() {
    private var _binding: FragmentFacturaPDFBinding? = null
    private val binding get() = _binding!!
    private val facturaViewModel: FacturaViewModel by activityViewModels()
    //private val pdfView by lazy { binding.pdfView }
    private var pdfPath: String? = null
    private lateinit var factura:Factura
    private var boolean by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (facturaViewModel.facturaActual.value!=null ) {
            factura = facturaViewModel.facturaActual.value!!
            // Generar y guardar el PDF en onCreate para que solo se haga una vez
        }



        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFacturaPDFBinding.inflate(inflater, container, false)
        val root: View = binding.root


            val pdfGenerator = GeneradorPDFFactura(requireActivity())
            pdfPath = pdfGenerator.generarYGuardarFacturaPdf(factura)

        binding.pdfView.initWithFile(
            file = File(pdfPath)
        )


      return root
    }

}


