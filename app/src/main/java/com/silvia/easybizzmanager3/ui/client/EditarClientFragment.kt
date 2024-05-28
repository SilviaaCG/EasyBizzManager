package com.silvia.easybizzmanager3.ui.client

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.google.android.material.snackbar.Snackbar
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentEditarClientBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import com.silvia.easybizzmanager3.models.generarId
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditarClientFragment : Fragment() {

    private var _binding: FragmentEditarClientBinding? = null
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var clienteActual: Client
    private var accion:Int = 0
    private lateinit var generadorImagenUri:GeneradorImagenUri

    private val imagenImageView by lazy { binding.clientImageView }
    private val nombreEditText by lazy { binding.nombreEditText }
    private val apellidosEditText by lazy { binding.apellidosEditText }
    private val fixSpinner by lazy { binding.fixNumeroSpinner }
    private val numeroEditText by lazy { binding.numeroEditText }
    private val direccionEditText by lazy { binding.direccionEditText }
    private val correoEditText by lazy { binding.correoEditText }
    private val agregarImagenButton by lazy { binding.fabAgregarFoto }
    private val guardarButton by lazy { binding.guardarButton }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generadorImagenUri = GeneradorImagenUri(this)
        ajustes().esconderAppBar(requireActivity() as AppCompatActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditarClientBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initVariables()
        volverButton()
        initComponentes()
        initButtons()

        return root
    }

    private fun volverButton() {

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            when(accion){
                1->{
                    findNavController().navigate(R.id.action_editarClientFragment_to_nav_clientes)
                    Snackbar.make(requireView(), "Operación cancelada", Snackbar.LENGTH_SHORT).show()
                }
                2->{
                    clientViewModel.setClienteActual(clienteActual)
                    findNavController().navigate(R.id.action_editarClientFragment_to_infoClientFragment)

                }
            }

        }

    }

    private fun inputsCorrectos(): Boolean {
        val nombre = nombreEditText.text.toString().trim()
        val apellidos = apellidosEditText.text.toString().trim()
        val numero = numeroEditText.text.toString().trim()
        val direccion = direccionEditText.text.toString().trim()
        val correo = correoEditText.text.toString().trim()

        // Validar número si no está vacío
        if (numero.isNotEmpty()) {
            if (!numero.matches("\\d+".toRegex())) {
                numeroEditText.error = "El número debe contener solo dígitos"
                return false
            }
        }
        // Validar dirección si no está vacía
        if (direccion.isNotEmpty()) {
            if (direccion.length < 5) {
                direccionEditText.error = "La dirección debe tener al menos 5 caracteres"
                return false
            }
        }


        // Validar campos requeridos
        if (nombre.isEmpty()) {
            nombreEditText.error = "El nombre es obligatorio"
            return false
        }

        if (apellidos.isEmpty()) {
            apellidosEditText.error = "Los apellidos son obligatorios"
            return false
        }

        // Validar formato de correo si no está vacío
        if (!correo.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            correoEditText.error = "Correo electrónico no válido"
            return false
        }


        return true
    }

    private fun initButtons() {
        guardarButton.setOnClickListener {
            when(accion){
                1->{
                    if (inputsCorrectos()){
                        var cliente = Client(
                            generarId(),
                            clienteActual.image,
                            nombreEditText.text.toString().trim(),
                            apellidosEditText.text.toString(),
                            if(!numeroEditText.text.isNullOrEmpty())
                                NumberContact(fixSpinner.selectedItem.toString(),numeroEditText.text.toString().toInt())
                            else null,
                            if(!direccionEditText.text.isNullOrEmpty()) direccionEditText.text.toString() else null,
                            if(!correoEditText.text.isNullOrEmpty()) correoEditText.text.toString().trim() else null,
                            false
                        )
                        clientViewModel.agregarClienteNuevo(cliente)
                        clientViewModel.insertarAccion(2)
                        clientViewModel.setClienteActual(cliente)
                        Snackbar.make(requireView(), "Cliente creado correctamente", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_editarClientFragment_to_infoClientFragment)
                    }
                }
                2->{
                    if (inputsCorrectos()){
                        var cliente = Client(
                            clienteActual.id,
                            clienteActual.image,
                            nombreEditText.text.toString().trim(),
                            apellidosEditText.text.toString(),
                            if(!numeroEditText.text.isNullOrEmpty())
                                NumberContact(fixSpinner.selectedItem.toString(),numeroEditText.text.toString().toInt())
                            else null,
                            if(!direccionEditText.text.isNullOrEmpty()) direccionEditText.text.toString() else null,
                            if(!correoEditText.text.isNullOrEmpty()) correoEditText.text.toString().trim() else null,
                            clienteActual.favorito
                        )
                        clientViewModel.actualizarCliente(clienteActual.id,cliente)
                        clientViewModel.insertarAccion(2)
                        clientViewModel.setClienteActual(cliente)
                        Snackbar.make(requireView(), "Cliente actualizado correctamente", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_editarClientFragment_to_infoClientFragment)
                    }

                }
            }
        }
        agregarImagenButton.setOnClickListener{

            generadorImagenUri.agregarImagen { uri ->

                    if (uri != null) {
                        clienteActual.image = uri.toString()
                        Picasso.get().load(clienteActual.image).transform(CircleTransform()).into(imagenImageView)

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

    private fun initVariables() {
        if (clientViewModel.clienteActual!=null){
            clienteActual = clientViewModel.clienteActual.value!!
        }
        if(clientViewModel.accion != null){
            accion = clientViewModel.accion.value!!
        }
    }

    private fun initComponentes() {
        when(accion){
            1 ->{
                //nada
            }
            2 ->{
                clienteActual = clientViewModel.clienteActual.value!!
                rellenarInputs()
            }
        }
    }

    private fun rellenarInputs() {
        if (clienteActual!=null){
            if (!clienteActual.image.isNullOrEmpty()) {
                Picasso.get().load(clienteActual.image).transform(CircleTransform())
                    .into(imagenImageView)
            }
            insertStringOnEditText(clienteActual.nombre, nombreEditText)
            insertStringOnEditText(clienteActual.apellidos, apellidosEditText)
            if (clienteActual.number!=null){
                fixPredeterminado()
            }
            if (clienteActual.correo!=null){
                insertStringOnEditText(clienteActual.correo!!,correoEditText)
            }
            if(clienteActual.direccion!=null){
                insertStringOnEditText(clienteActual.direccion!!,direccionEditText)
            }
        }



    }


    private fun fixPredeterminado() {
        // Obtén las cadenas del array de recursos
        val items = resources.getStringArray(R.array.sufijos)

        // Configura el adaptador del Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fixSpinner.adapter = adapter

        // Establecer el ítem seleccionado basado en la cadena
        val prefix = clienteActual.number?.prefix
        if (prefix != null) {
            val position = items.indexOf(prefix)
            if (position >= 0) {
                fixSpinner.setSelection(position)
            }
        }
    }

    private fun insertStringOnEditText(string: String, editText: EditText){
        editText.text  = Editable.Factory.getInstance().newEditable(string)
    }

}

