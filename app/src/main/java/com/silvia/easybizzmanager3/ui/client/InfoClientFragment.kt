package com.silvia.easybizzmanager3.ui.client

import android.R
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.silvia.easybizzmanager3.databinding.FragmentInfoClientBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.NumberContact
import com.silvia.easybizzmanager3.models.generarId
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
        binding.eliminarClientButton.setOnClickListener {
            // Muestra dialogo
            mostrarDialogo("Eliminar Cliente ${clienteActual!!.nombre}","¿Estas seguro de que quieres eliminar este cliente?"){
                clientViewModel.eliminarCliente(clienteActual!!.id)
                volverAtras()
            }
        }
        binding.fabAgregarFoto.setOnClickListener {
            agregarImagen()
        }

        binding.guardarButton.setOnClickListener {
            if(compruebaCampos()) {
                lifecycleScope.launch {
                    clienteExiste { existe, clienteEncontrado ->
                        if (existe) {
                            if (!clienteEncontrado!!.id.equals(clienteActual!!.id)){
                                // Muestra dialogo
                                mostrarDialogo("¡Cliente existente!", "Ya existe un cliente con este nombre. ¿Quieres remplazarlo?") {
                                    clientViewModel.actualizarCliente(
                                        clienteEncontrado!!.id, Client(
                                            clienteEncontrado!!.id,
                                            imagePath ?: "sin imagen",
                                            binding.clientNameEditText.text.toString(),
                                            binding.clientApellidosEditText.text.toString(),
                                            NumberContact(
                                                binding.fixNumeroSpinner.getItemAtPosition(binding.fixNumeroSpinner.selectedItemPosition).toString(),
                                                binding.clientNumeroEditText.text.toString().toInt()
                                            ),
                                            binding.clientDireccionEditText.text.toString(),
                                            binding.clientCorreoEditText.text.toString(),
                                            favorito!!
                                        )
                                    )
                                    desactivarEdicion()
                                }
                            }else{
                                clientViewModel.actualizarCliente(clienteActual!!.id,Client(
                                    clienteActual!!.id,
                                    imagePath ?: "sin imagen",
                                    binding.clientNameEditText.text.toString(),
                                    binding.clientApellidosEditText.text.toString(),
                                    NumberContact(
                                        binding.fixNumeroSpinner.getItemAtPosition(binding.fixNumeroSpinner.selectedItemPosition)
                                            .toString(),
                                        binding.clientNumeroEditText.text.toString().toInt()
                                    ),
                                    binding.clientDireccionEditText.text.toString(),
                                    binding.clientCorreoEditText.text.toString(),
                                    favorito!!
                                ))
                                desactivarEdicion()
                            }

                        } else if(clienteActual!!.id == "0"){
                            clientViewModel.agregarClienteNuevo(
                                Client(
                                    generarId(),
                                    imagePath ?: "sin imagen",
                                    binding.clientNameEditText.text.toString(),
                                    binding.clientApellidosEditText.text.toString(),
                                    NumberContact(
                                        binding.fixNumeroSpinner.getItemAtPosition(binding.fixNumeroSpinner.selectedItemPosition)
                                            .toString(),
                                        binding.clientNumeroEditText.text.toString().toInt()
                                    ),
                                    binding.clientDireccionEditText.text.toString(),
                                    binding.clientCorreoEditText.text.toString(),
                                    favorito!!
                                )
                            )
                            imagePath = null
                            desactivarEdicion()
                        }else{
                            clientViewModel.actualizarCliente(clienteActual!!.id,Client(
                                clienteActual!!.id,
                                imagePath ?: "sin imagen",
                                binding.clientNameEditText.text.toString(),
                                binding.clientApellidosEditText.text.toString(),
                                NumberContact(
                                    binding.fixNumeroSpinner.getItemAtPosition(binding.fixNumeroSpinner.selectedItemPosition)
                                        .toString(),
                                    binding.clientNumeroEditText.text.toString().toInt()
                                ),
                                binding.clientDireccionEditText.text.toString(),
                                binding.clientCorreoEditText.text.toString(),
                                favorito!!
                            ))
                            desactivarEdicion()
                        }
                    }
                }
            }
        }

        return root
    }
    private fun mostrarDialogo(titulo: String,mensaje: String,aceptarClickListener: () -> Unit){
        // Muestra dialogo
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar") { dialog, which ->
            aceptarClickListener()
        }
        builder.setNegativeButton("Cancelar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun volverAtras() {
        findNavController().navigate(com.silvia.easybizzmanager3.R.id.action_infoClientFragment_to_nav_clientes)
    }


    private fun clienteExiste(callback: (Boolean, Client?) -> Unit) {
        lifecycleScope.launch {
            clientViewModel.actualizarClientList()
        }

        clientViewModel.clientList.observe(viewLifecycleOwner) { clients ->
            var existe = false
            var clienteEncontrado: Client? = null
            for (client in clients) {
                if (client.nombre.equals(binding.clientNameEditText.text.toString().trim(),ignoreCase = true)
                    && client.apellidos.equals(binding.clientApellidosEditText.text.toString().trim(),ignoreCase = true)) {
                    existe = true
                    clienteEncontrado = client
                    break
                }
            }
            callback(existe, clienteEncontrado)
        }
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





    private fun activarEdicion() {
        binding.guardarButton.visibility = View.VISIBLE
        binding.layoutClientFacturas.visibility = View.GONE
        binding.layoutClientInfoOpciones.visibility = View.GONE
        binding.fabAgregarFoto.visibility = View.VISIBLE
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
        binding.fabAgregarFoto.visibility = View.GONE
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
                Uri.fromFile(
                    File(if (
                        !clienteActual?.image.equals("sin imagen")) clienteActual?.image
                    else "/data/user/0/com.silvia.easybizzmanager3/files/images/99d68521-3863-4f13-81a4-f9ed6380c63c_image.png"))
            ).transform(CircleTransform()).into(binding.clientImageView)
            imagePath = clienteActual?.image ?: "/data/user/0/com.silvia.easybizzmanager3/files/images/99d68521-3863-4f13-81a4-f9ed6380c63c_image.png"
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
            .cropSquare()
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


    private var imagePath: String? = null
    private fun guardarImagen(uri: Uri) {
        try {
            // Obtener el InputStream de la Uri
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            // Crear un archivo en el directorio de archivos privados de la aplicación
            val directory = File(requireContext().filesDir, "images")
            directory.mkdirs()
            if (directory.exists()){
                Toast.makeText(requireContext(),"El directorio: $directory , existe", Toast.LENGTH_SHORT).show()
                Log.d("DEPURADOR","Existe el directorio: $directory  ")
            }
            // Crea el directorio si no existe
            val file = File(directory, "${generarId()}_image.png")
            // Copiar los datos del InputStream al archivo
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            if (directory.exists()){
                Toast.makeText(requireContext(),"El archivo: $file , existe", Toast.LENGTH_SHORT).show()
                Log.d("DEPURADOR","Existe el archivo: $file  ")
            }
            // Insertar la imagen en el ImageView y ponerle la ruta al cliente

            // Notificar al sistema que se ha agregado un archivo
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(file.absolutePath),
                null,
                null
            )
            Toast.makeText(requireContext(), "Imagen guardada en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            if (file.exists()) {
                // Si el archivo existe, lo cargamos en un Bitmap
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                // Mostramos el Bitmap en el ImageView
                Picasso.get().load(Uri.fromFile(file)).transform(CircleTransform()).into(binding.clientImageView)
                imagePath = file.absolutePath
            } else {
                Toast.makeText(requireContext(), "La imagen no ha sido encontrada", Toast.LENGTH_SHORT).show()
            }
        }catch (e: IOException){
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

}

