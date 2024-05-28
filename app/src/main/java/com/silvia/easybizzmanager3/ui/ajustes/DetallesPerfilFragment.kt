package com.silvia.easybizzmanager3.ui.ajustes

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.installations
import com.silvia.easybizzmanager3.AuthActivity
import com.silvia.easybizzmanager3.MainActivity
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.FragmentDetallesPerfilBinding

import com.silvia.easybizzmanager3.managment.AuthManager
import com.silvia.easybizzmanager3.models.DetallesPerfil
import com.silvia.easybizzmanager3.models.NumberContact

class DetallesPerfilFragment : Fragment() {

    private var _binding: FragmentDetallesPerfilBinding? = null
    private val binding get() = _binding!!
    private val authManager = AuthManager()
    private val viewModelDetallesPerfil: DetallesPerfilViewModel by viewModels()

    private val nombreEditText by lazy { binding.nombreEditText }
    private val apellidosEditText by lazy { binding.apellidosEditText }
    private val nombreEmpresaEditText by lazy { binding.nombreEmpresaEditText }
    private val numeroEmpresaEditText by lazy { binding.numeroEmpresaEditText }
    private val fixSpinner by lazy { binding.fixSpinner }
    private val numeroContactoEditText by lazy { binding.numeroContactoEditText }
    private val correoEditText by lazy { binding.correoEditText }
    private val correoContactoEditText by lazy { binding.correoContactoEditText }
    private val ubicacionEditText by lazy { binding.ubicacionEditText }
    private val guardarButton by lazy { binding.siguienteButton }
    private lateinit var detallesPerfilActuales:DetallesPerfil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetallesPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initButton()
        volverButton()
        initComponentes()
        return  root

    }

    private fun initComponentes() {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        AuthManager().recuperarDetalles { detalles->
            if (detalles != null) {
                viewModelDetallesPerfil.insertarDetallesActuales(detalles)
                detallesPerfilActuales = detalles

            }else{
                detallesPerfilActuales = DetallesPerfil()
            }
            rellenaCampos()
        }
    }

    private fun rellenaCampos() {
        if (detallesPerfilActuales!=null){
            if (!detallesPerfilActuales.nombre.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.nombre!!,nombreEditText)
            }
            if (!detallesPerfilActuales.apellidos.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.apellidos!!,apellidosEditText)
            }
            if (!detallesPerfilActuales.correo.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.correo!!,correoEditText)
            }
            if (!detallesPerfilActuales.correoContacto.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.correoContacto!!,correoContactoEditText)
            }
            if (!detallesPerfilActuales.nombreEmpresa.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.nombreEmpresa!!,nombreEmpresaEditText)
            }
            if (!detallesPerfilActuales.numeroEmpresa.isNullOrEmpty()){
                insertStringOnEditText(detallesPerfilActuales.numeroEmpresa!!,numeroEmpresaEditText)
            }
            if (detallesPerfilActuales.numeroContacto!=null){
                fixPredeterminado(detallesPerfilActuales.numeroContacto!!.prefix)
                insertStringOnEditText(detallesPerfilActuales.numeroContacto!!.number.toString(),numeroContactoEditText)
            }
            if (detallesPerfilActuales.direccion!=null){
                insertStringOnEditText(detallesPerfilActuales.numeroContacto!!.number.toString(),numeroContactoEditText)
            }
            insertStringOnEditText( FirebaseAuth.getInstance().currentUser!!.email!!,correoEditText)
        }
    }
    private fun fixPredeterminado(prefix:String) {
        // Obtén las cadenas del array de recursos
        val items = resources.getStringArray(R.array.sufijos)
        // Configura el adaptador del Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fixSpinner.adapter = adapter
        // Establecer el ítem seleccionado basado en la cadena
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




    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val currentActivity = requireActivity()
            when (currentActivity) {
                is AuthActivity -> {
                    parentFragmentManager.popBackStack()
                    showAuth()
                }
                is MainActivity -> {
                    findNavController().navigate(R.id.nav_home)
                    // Cerrar el fragmento actual
                    parentFragmentManager.popBackStack()

                    // Mostrar ActionBar si es posible
                    (currentActivity as? AppCompatActivity)?.supportActionBar?.show()
                }
            }


        }
    }
    private fun showAuth() {
        val authIntent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(authIntent)
    }

    private fun inputsCorrectos(): Boolean {
        var isvalid = true
        if (nombreEmpresaEditText.text.isNullOrEmpty()){
            nombreEmpresaEditText.error = "El nombre de la empresa es obligatorio"
            isvalid = false
        }
        if (nombreEditText.text.isNullOrEmpty()){
            nombreEditText.error = "El nombre es obligatorio"
            isvalid = false
        }
        if (apellidosEditText.text.isNullOrEmpty()){
            apellidosEditText.error = "Los apellidos son obligatorios obligatorio"
            isvalid = false
        }
        return isvalid
    }

    private fun initButton() {
        guardarButton.setOnClickListener {
            if (inputsCorrectos()) {
                val detalles = DetallesPerfil(
                    if (!nombreEditText.text.isNullOrEmpty()) nombreEditText.text!!.trim()
                        .toString() else null,
                    if (!apellidosEditText.text.isNullOrEmpty()) apellidosEditText.text!!.toString() else null,
                    if (!nombreEmpresaEditText.text.isNullOrEmpty()) nombreEmpresaEditText.text!!.toString() else null,
                    if (!numeroEmpresaEditText.text.isNullOrEmpty()) numeroEmpresaEditText.text!!.toString() else null,
                    if (!numeroContactoEditText.text.isNullOrEmpty())
                        NumberContact(
                            fixSpinner.selectedItem.toString(),
                            numeroContactoEditText.text!!.toString().toInt()
                        )
                    else null,
                    correoEditText.text.toString(),
                    if (!correoContactoEditText.text.isNullOrEmpty()) correoContactoEditText.text!!.toString() else null,
                    if (!ubicacionEditText.text.isNullOrEmpty()) ubicacionEditText.text!!.toString() else null,
                )
                if (detallesPerfilActuales != null) {
                    authManager.actualizarDetalles(detalles)
                } else {
                    authManager.agregarDetalles(detalles)
                }
                navigateToNextScreen()
            }



        }
    }

    private fun navigateToNextScreen() {val currentActivity = requireActivity()
        when (currentActivity) {
            is AuthActivity -> {
                showHome()
            }
            is MainActivity -> {
                findNavController().navigate(R.id.nav_home)
            }
        }

        // Cerrar el fragmento actual
        parentFragmentManager.popBackStack()

        // Mostrar ActionBar si es posible
        (currentActivity as? AppCompatActivity)?.supportActionBar?.show()
    }
    private fun showHome() {
        val homeIntent = Intent(requireContext(), MainActivity::class.java)
        startActivity(homeIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(com.rajat.pdfviewer.R.menu.menu, menu)
    }


}