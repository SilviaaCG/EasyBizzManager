package com.silvia.easybizzmanager3.ui.productos_servicios

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentEditarItemsBinding
import com.silvia.easybizzmanager3.databinding.FragmentServiciosBinding
import com.silvia.easybizzmanager3.managment.AuthManager
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.TransformImage
import com.silvia.easybizzmanager3.models.generarId
import com.silvia.easybizzmanager3.ui.client.GeneradorImagenUri
import com.squareup.picasso.Picasso


class EditarItemsFragment : Fragment() {
    private var _binding: FragmentEditarItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosYServiciosViewModel by activityViewModels()
    private val itemImageView by lazy { binding.itemImageView }
    private val agregarFotoButton by lazy { binding.agregarFotoButton }
    private val nombreEditText by lazy { binding.nombreEditText }
    private val categoriaEditText by lazy { binding.categoriaEditText }
    private val descripcionEditText by lazy { binding.descripcionEditText }
    private val precioUnitarioEditText by lazy { binding.precioUnitarioEditText }
    private val tipoRadioGroup by lazy { binding.tipoRadioGroup }
    private val isServicioButton by lazy { binding.isServicioButton }
    private val isProductoButton by lazy { binding.isProductoButton }
    private val radioGroupErrorTextView by lazy { binding.radioGroupErrorTextView }
    private val guardarButton by lazy { binding.guardarButton }
    private lateinit var itemActual: Item
    private var accion:Int = 0
    private lateinit var generadorImagenUri: GeneradorImagenUri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generadorImagenUri = GeneradorImagenUri(this)
        ajustes().esconderAppBar(activity as AppCompatActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditarItemsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ajustes().seleccionarFragment(requireActivity() as AppCompatActivity, R.id.nav_productos_servicios)

        initVariables()
        volverButton()
        initComponentes()
        initButtons()

        return root
    }

    private fun inputsCorrectos(): Boolean {
        var isValid = true

        if (nombreEditText.text.isNullOrBlank()) {
            nombreEditText.error = "Este campo es obligatorio"
            isValid = false
        } else {
            nombreEditText.error = null
        }

        // Check if the price is empty
        if (precioUnitarioEditText.text.isNullOrBlank()) {
            precioUnitarioEditText.error = "Este campo es obligatorio"
            isValid = false
        } else {
            precioUnitarioEditText.error = null
        }
        // Check if any radio button is selected
        if (tipoRadioGroup.checkedRadioButtonId == -1) {
            radioGroupErrorTextView.visibility = View.VISIBLE
            isValid = false
        } else {
            radioGroupErrorTextView.visibility = View.GONE
        }
        return isValid
    }

    private fun initButtons() {
        guardarButton.setOnClickListener {
            when(accion){
                1 ->{
                    if(inputsCorrectos()){
                        itemActual = Item(
                            generarId(),
                            nombreEditText.text.toString(),
                            if (!itemActual.imagen.isNullOrEmpty()) itemActual.imagen else null,
                            if (!categoriaEditText.text.isNullOrEmpty()) categoriaEditText.text!!.trim().toString() else null,
                            if (!descripcionEditText.text.isNullOrEmpty()) descripcionEditText.text!!.toString() else null,
                            precioUnitarioEditText.text.toString().toDouble(),
                            0,
                            0.0,
                            if (isProductoButton.isChecked) true else false
                        )
                        viewModel.agregarItemNuevo(itemActual)
                        viewModel.insertarAccionItem(2)
                        viewModel.insertarItemActual(itemActual)
                        findNavController().navigate(R.id.action_editarItemsFragment_to_infoProductosYServiciosFragment)
                        Snackbar.make(binding.root, "Se ha creado el ${if (isProductoButton.isChecked == true) "producto" else "servicio"} correctamente.", Snackbar.LENGTH_SHORT).show()
                    }
                }
                2 ->{
                    if(inputsCorrectos()){
                        itemActual = Item(
                            itemActual.id,
                            nombreEditText.text.toString(),
                            if (!itemActual.imagen.isNullOrEmpty()) itemActual.imagen else null,
                            if (!categoriaEditText.text.isNullOrEmpty()) categoriaEditText.text!!.trim().toString() else null,
                            if (!descripcionEditText.text.isNullOrEmpty()) descripcionEditText.text!!.toString() else null,
                            precioUnitarioEditText.text.toString().toDouble(),
                            0,
                            0.0,
                            if (isProductoButton.isChecked) true else false
                        )
                        viewModel.actualizarItem(itemActual.id,itemActual)
                        viewModel.insertarAccionItem(2)
                        viewModel.insertarItemActual(itemActual)
                        findNavController().navigate(R.id.action_editarItemsFragment_to_infoProductosYServiciosFragment)
                        Snackbar.make(binding.root, "Se ha actualizado los datos del ${if (itemActual.isProducto) "producto" else "servicio"} correctamente.", Snackbar.LENGTH_SHORT).show()
                    }

                }
                else->{
                    Toast.makeText(requireContext(),"no hay accion",Toast.LENGTH_SHORT).show()
                    viewModel.insertarAccionItem(1)
                }

            }
        }
        agregarFotoButton.setOnClickListener {
            generadorImagenUri.agregarImagen { uri ->
                if (uri != null) {
                    itemActual.imagen = uri.toString()
                    Picasso.get().load(itemActual.imagen).transform(TransformImage(20F)).into(itemImageView)
                } else {
                    Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        generadorImagenUri.onActivityResult(requestCode, resultCode, data)
    }


    private fun initComponentes() {
        viewModel.accionItem.observe(viewLifecycleOwner) { accion ->
            this.accion = accion
            when (accion) {
                1 -> {
                    // Acción específica cuando la acción es 1
                }

                2 -> {
                    // Acción específica cuando la acción es 2
                    rellenarCampos(viewModel.itemActual.value!!)
                }
            }
        }

    }

    private fun rellenarCampos(itemActual: Item) {
        if (!itemActual.imagen.isNullOrEmpty()){
            Picasso.get().load(itemActual.imagen).transform(TransformImage(20f)).into(itemImageView)
        }
        insertStringOnEditText(itemActual.nombre,nombreEditText)
        if (!itemActual.categoria.isNullOrEmpty()){
            insertStringOnEditText(itemActual.categoria!!,categoriaEditText)
        }
        if (!itemActual.descripcion.isNullOrEmpty()){
            insertStringOnEditText(itemActual.descripcion!!,descripcionEditText)
        }
        insertStringOnEditText(itemActual.precioUnitario.toString(),precioUnitarioEditText)
        if (itemActual.isProducto){
            isProductoButton.isChecked = true
        }else{
            isServicioButton.isChecked = true
        }
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (accion==2){
                viewModel.insertarItemActual(itemActual)
                viewModel.insertarAccionItem(2)
                findNavController().navigate(R.id.action_editarItemsFragment_to_infoProductosYServiciosFragment)

            }else{
                findNavController().navigate(R.id.action_editarItemsFragment_to_contenedorServiciosProductosFragment)
                Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
            }
        }


    }

    private fun initVariables() {
        if (viewModel.itemActual.value != null) {
            itemActual = viewModel.itemActual.value!!
        }

    }
    private fun insertStringOnEditText(string: String, editText: EditText){
        editText.text  = Editable.Factory.getInstance().newEditable(string)
    }


}