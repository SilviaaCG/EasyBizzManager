package com.silvia.easybizzmanager3.ui.register

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentHomeBinding
import com.silvia.easybizzmanager3.databinding.FragmentRegistro1Binding

class Registro1Fragment : Fragment() {
    private var _binding: FragmentRegistro1Binding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = Registro1Fragment()
    }
    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistro1Binding.inflate(inflater, container, false)
        val root: View = binding.root
        initButton()

        return root
    }

    private fun initButton() {
        binding.siguienteButton.setOnClickListener {
            // En el FragmentoA
            val registro2Fragment = Registro2Fragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedorFragments, registro2Fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}