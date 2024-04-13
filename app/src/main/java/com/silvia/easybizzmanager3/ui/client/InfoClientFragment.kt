package com.silvia.easybizzmanager3.ui.client

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.silvia.easybizzmanager3.databinding.FragmentInfoClientBinding
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import com.squareup.picasso.Picasso


class InfoClientFragment : Fragment() {

    private var _binding: FragmentInfoClientBinding? = null
    private val clientViewModel: ClientViewModel by activityViewModels()
    private val binding get() = _binding!!
    private var clienteActual: Client? = null
    private var favorito:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoClientBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Inicializa vista
        rellenaCampos()
        initFix()
        if (clientViewModel.isEditable.value!!){
           activarEdicion()
        }else{
            desactivarEdicion()
        }

        // Botones
        binding.editarClientButton.setOnClickListener {
            clientViewModel.setEditable(true)
            activarEdicion()
        }

        binding.guardarButton.setOnClickListener {
            if(compruebaCampos()){
                if(clienteExiste()){
                    showDialogo("Ya existe!","Este cliente ya existe en tus clientes")
                }else{
                    clientViewModel.agregarClienteNuevo(creaCliente())
                }
            }
        }



        return root
    }

    private fun clienteExiste(): Boolean {
        // comprueba si hay un nombre igual existente y saca dialogo para remplazar
        var existe = false
        clientViewModel.clientList.observe(viewLifecycleOwner){
            for (client in it){
                if(client.nombre.equals(binding.clientNameEditText.text.toString())
                    && client.apellidos.equals(binding.clientApellidosEditText.text.toString())){
                    existe = true
                }
            }
        }
        return existe
    }


    private fun compruebaCampos(): Boolean {
        if(binding.clientNameEditText.text.toString() != null && binding.clientNumeroEditText.text.toString() != null){
            try {
                binding.clientNumeroEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
               showError("Error","El número solo debe contener números")
                return false
            }
            if (binding.clientCorreoEditText.text.toString() != null &&!esCorreoElectronico(binding.clientCorreoEditText.text.toString())){
                showError("Error","El correo introducido no es válido")
                return false
            }
        }else{
            return false
        }
    return true
    }
    fun esCorreoElectronico(correo: String): Boolean {
        val patronCorreo = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return patronCorreo.matches(correo)
    }

    private fun showError(titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showDialogo(titulo: String, mensaje :String) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(titulo)
            builder.setMessage(mensaje)
            builder.setPositiveButton("Aceptar"){dialog, which ->
                clientViewModel.actualizarCliente(
                    clientViewModel.buscarClientePorNombre(binding.clientNameEditText.text.toString(),
                        binding.clientApellidosEditText.text.toString())
                    ,creaCliente())
            }
            builder.setNegativeButton("Cancelar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()

    }

    private fun creaCliente(): Client {
        val cliente = Client("0", " ",binding.clientNameEditText.text.toString()
            ,binding.clientApellidosEditText.text.toString(), NumberContact(binding.fixNumeroSpinner.getItemAtPosition(binding.fixNumeroSpinner.selectedItemPosition).toString(),
            binding.clientNumeroEditText.text.toString().toInt()),binding.clientDireccionEditText.text.toString(),
            binding.clientCorreoEditText.text.toString(),favorito!!)
        return cliente
    }

    private fun activarEdicion() {
        binding.guardarButton.visibility = View.VISIBLE
        binding.layoutClientFacturas.visibility = View.GONE
        binding.layoutClientInfoOpciones.visibility = View.GONE
        binding.clientImageView.setOnClickListener{
            agregarImagen()
        }
        val editable = true
        binding.fixNumeroSpinner.setEnabled(editable)
        setEditableEditText(binding.clientNameEditText,editable)
        setEditableEditText(binding.clientApellidosEditText,editable)
        setEditableEditText(binding.clientNumeroEditText,editable)
        setEditableEditText(binding.clientCorreoEditText,editable)
        setEditableEditText(binding.clientDireccionEditText,editable)
    }

    //https://github.com/gupitao/KotlinPickImageFromGalery/blob/master/app/src/main/java/com/example/fotosbootcamp/MainActivity.kt



    private fun desactivarEdicion() {
        binding.guardarButton.visibility = View.GONE
        binding.layoutClientFacturas.visibility = View.VISIBLE
        binding.layoutClientInfoOpciones.visibility = View.VISIBLE
        binding.clientImageView.setOnClickListener(null)
        val editable = false
        binding.fixNumeroSpinner.setEnabled(editable)
        setEditableEditText(binding.clientNameEditText,editable)
        setEditableEditText(binding.clientApellidosEditText,editable)
        setEditableEditText(binding.clientNumeroEditText,editable)
        setEditableEditText(binding.clientCorreoEditText,editable)
        setEditableEditText(binding.clientDireccionEditText,editable)
    }

    private fun initFix() {
        if (clienteActual != null) {
            val fixes = listOf("+34","+1","+44","+33","+49")
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, fixes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.fixNumeroSpinner.adapter = adapter
            val index = fixes.indexOf(clienteActual?.number!!.prefix)
            if (index != -1) {
                binding.fixNumeroSpinner.setSelection(index)
            }
        }
    }
    private fun rellenaCampos() {
        clienteActual = clientViewModel.clienteActual.value
        if (clienteActual != null) {
            favorito = clienteActual?.favorito!!
            if (favorito){
                binding.favoritoButton.setBackgroundResource(
                    R.drawable.star_on
                )
            }else{
                binding.favoritoButton.setBackgroundResource(
                    R.drawable.star_off
                )
            }
            Picasso.get().load(
                Uri.parse(
                    if (
                        clienteActual?.image != null && clienteActual?.image!!.isNotBlank())
                        clienteActual?.image.toString()
                    else "android.resource://drawable/ic_sin_imagen_foreground.xml"
                )).into(binding.clientImageView)
            insertStringOnEditText(clienteActual?.nombre!!,binding.clientNameEditText)
            insertStringOnEditText(clienteActual?.apellidos!!,binding.clientApellidosEditText)
            insertStringOnEditText(clienteActual?.number!!.number.toString(),binding.clientNumeroEditText)
            insertStringOnEditText(clienteActual?.correo!!,binding.clientCorreoEditText)
            insertStringOnEditText(clienteActual?.direccion!!,binding.clientDireccionEditText)
        }

    }

    // Funciones optimizadas
    private fun insertStringOnEditText(string: String, editText: EditText){
        editText.text  = Editable.Factory.getInstance().newEditable(string)
    }
    private fun setEditableEditText(label: EditText, b: Boolean) {
        label.setEnabled(b)
        if (b){
            label.backgroundTintList = null
        }else{
            label.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        }
    }

    // Galeria o Camara
    private lateinit var imgProfile: String
    private fun agregarImagen() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            // Use Uri object instead of File to avoid storage permissions
            guardarImagen(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }



    private fun guardarImagen(uri: Uri) {
        // Obtener el Bitmap a partir de la Uri
        val imagenBitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)

        // Convertir el Bitmap a un Drawable
        val drawable = BitmapDrawable(resources, imagenBitmap)

        // Obtener el directorio de mipmap
        val mipmapDirectory = requireContext().resources.openRawResource(R.mipmap.ic_launcher)

    }
}

