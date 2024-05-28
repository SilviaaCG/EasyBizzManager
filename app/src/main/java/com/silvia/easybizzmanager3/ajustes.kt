package com.silvia.easybizzmanager3

import android.app.Activity
import android.text.Editable
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class ajustes {
    fun abrirFragmento(fragmentoAbierto: Fragment,fragmento: Fragment) {
        val transaction = fragmentoAbierto.requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_content_main, fragmento)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun insertStringOnEditText(string: String, editText: EditText){
        editText.text  = Editable.Factory.getInstance().newEditable(string)
    }
    fun mostrarAppBar(activity:AppCompatActivity){
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }
    fun esconderAppBar(activity:AppCompatActivity){
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    fun seleccionarFragment(activity:AppCompatActivity,id:Int){

        val navigationView = activity?.findViewById<NavigationView>(R.id.nav_view)
        navigationView?.let {
            val menuItem = it.menu.findItem(id)
            menuItem.isChecked = true
        }
    }
    fun insertarTitulo(string: String, activity: AppCompatActivity){
        // Cambiar el título del AppBar de forma segura
        val activity = activity
        activity?.supportActionBar?.title = string
    }
}