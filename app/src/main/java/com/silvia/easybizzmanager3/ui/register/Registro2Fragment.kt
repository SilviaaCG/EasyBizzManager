package com.silvia.easybizzmanager3.ui.register

import android.graphics.drawable.Drawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentRegistro2Binding
import com.silvia.easybizzmanager3.models.Funcion

class Registro2Fragment : Fragment() {
    private var _binding: FragmentRegistro2Binding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = Registro2Fragment()
    }

    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegistro2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecycler()


        return root
    }

    private fun initRecycler() {
        binding.funcionesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val funciones = listOf(
            Funcion("Facturas","Función para crear facturas y llevar un segumiento de ellas.",
                requireContext().getDrawable(R.drawable.facturas)!!,false),
            Funcion("Clientes","Función para crear clientes y poder guardarlos con seguridad",requireContext().getDrawable(R.drawable.agenda)!!,false),
            Funcion("Inventario","Gestiona tus productos y servicios con facilidad.",requireContext().getDrawable(R.drawable.inventario)!!,false),
            Funcion("Citas","Recuerda siempre tus citas y a tus clientes también.",requireContext().getDrawable(R.drawable.citas)!!,false),
            Funcion("Tiquets","Tus clientes o empleados te pueden mandar tiquets para solucionarlos.",requireContext().getDrawable(R.drawable.tiquet)!!,false),
        )
        binding.funcionesRecyclerView.adapter = FuncionesAdapter(funciones, {descripcion -> onClickListener(descripcion)})
    }
    fun onClickListener(descripcion:String){
        Snackbar.make(requireView(), descripcion, Snackbar.LENGTH_SHORT).show()
    }
}