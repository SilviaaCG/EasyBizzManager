package com.silvia.easybizzmanager3.ui.productos_servicios


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes

import com.silvia.easybizzmanager3.databinding.FragmentInfoProductosYServiciosBinding
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.TransformImage
import com.silvia.easybizzmanager3.ui.dialogos.EliminarClienteDialog
import com.silvia.easybizzmanager3.ui.dialogos.EliminarItemDialog
import com.squareup.picasso.Picasso

class infoProductosYServiciosFragment : Fragment() {

    private var _binding: FragmentInfoProductosYServiciosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosYServiciosViewModel by activityViewModels()
    private val categoriaTextView by lazy { binding.categoriaTextView }
    private val nombreTextView by lazy { binding.nombreTextView }
    private val precioTextView by lazy { binding.precioTextView }
    private val itemImageView by lazy { binding.itemImageView }
    private val descripcionTextView by lazy { binding.descripcionTextView }
    private val editarButton by lazy { binding.editarButton }
    private val eliminarButton by lazy { binding.eliminarButton }
    private lateinit var itemActual: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_productos_servicios)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInfoProductosYServiciosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initVariables()
        initComponentes()
        volverButton()
        initButtons()

        return root
    }

    private fun initButtons() {
        editarButton.setOnClickListener {
            viewModel.insertarItemActual(itemActual)
            viewModel.insertarAccionItem(2)
            findNavController().navigate(R.id.action_infoProductosYServiciosFragment_to_editarItemsFragment)
        }
        eliminarButton.setOnClickListener {
            var dialogoEliminarItem = EliminarItemDialog(itemActual.isProducto)
            dialogoEliminarItem.setEliminarItemDialogListener(object :EliminarItemDialog.EliminarItemDialogListener{
                override fun onDialogPositiveClick(dialog: DialogFragment) {
                    viewModel.eliminarItem(itemActual.id)
                    findNavController().navigate(R.id.action_infoProductosYServiciosFragment_to_contenedorServiciosProductosFragment)
                    Snackbar.make(requireView(), "Se ha eliminado el cliente con nombre ${itemActual.nombre} correctamente", Snackbar.LENGTH_SHORT).show()
                    (activity as? AppCompatActivity)?.supportActionBar?.show()
                }
                override fun onDialogNegativeClick(dialog: DialogFragment) {
                    dialogoEliminarItem.dismiss()
                    Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
                }
            })
            dialogoEliminarItem.show(requireActivity().supportFragmentManager, "EliminarItemDialog")

            }
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().navigate(R.id.action_infoProductosYServiciosFragment_to_contenedorServiciosProductosFragment)

        }
    }

    private fun initVariables() {
        if (viewModel.itemActual.value!=null){
            itemActual = viewModel.itemActual.value!!
        }
    }

    private fun initComponentes() {
        if (itemActual!=null){
            if (!itemActual.categoria.isNullOrEmpty()){
                categoriaTextView.visibility = View.VISIBLE
                categoriaTextView.text = itemActual.categoria
            }else{
                categoriaTextView.visibility = View.GONE
            }
        }
        precioTextView.text = itemActual.precioUnitario.toString()
        nombreTextView.text = itemActual.nombre
        if (!itemActual.imagen.isNullOrEmpty()){
            Picasso.get().load(itemActual.imagen).transform(TransformImage(20F)).into(itemImageView)
        }
        if (!itemActual.descripcion.isNullOrEmpty()){
            descripcionTextView.text = itemActual.descripcion
        }else{
            descripcionTextView.text = "No hay descripción"
        }
    }


}