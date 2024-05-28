package com.silvia.easybizzmanager3.managment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.DetallesPerfil

class AuthManager {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(uid!!).child("detallesPerfil")
    // Agregar detalles del perfil
    fun agregarDetalles(detallesPerfil: DetallesPerfil) {
        databaseReference.setValue(detallesPerfil)
    }

    // Actualizar detalles del perfil
    fun actualizarDetalles(detallesPerfil: DetallesPerfil) {
        databaseReference.setValue(detallesPerfil)
    }

    // Eliminar detalles del perfil
    fun eliminarDetalles() {
        databaseReference.removeValue()
    }
    // Recuperar detalles del perfil
    fun recuperarDetalles(onComplete: (DetallesPerfil?) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val detallesPerfil = snapshot.getValue(DetallesPerfil::class.java)
                onComplete(detallesPerfil)
            }

            override fun onCancelled(error: DatabaseError) {
                onComplete(null)
            }
        })
    }

}