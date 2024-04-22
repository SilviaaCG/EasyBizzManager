package com.silvia.easybizzmanager3.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentFacturaBinding
import com.silvia.easybizzmanager3.databinding.FragmentInfoFacturaBinding

class InfoFacturaFragment : Fragment() {

    private var _binding: FragmentInfoFacturaBinding? = null
    private val binding get() = _binding!!
    private val facturaViewModel: FacturaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentInfoFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
}