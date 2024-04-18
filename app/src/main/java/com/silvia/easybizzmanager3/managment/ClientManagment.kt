package com.silvia.easybizzmanager3.managment

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.silvia.easybizzmanager3.models.Client
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.toList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ClientManagment {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid

    val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(uid!!).child("clientes")
    fun agregarCliente(cliente: Client){
        databaseReference.child(cliente.id).setValue(cliente)
    }
    fun eliminarCliente(clienteId: String){
        databaseReference.child(clienteId).removeValue()
    }
    fun actualizarCliente(clienteId:String, clienteActualizado:Client){
        databaseReference.child(clienteId).setValue(clienteActualizado)
    }

    suspend fun obtenerClientesFlow(): List<Client> = suspendCoroutine { continuation ->
       val listener = object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {
               val clientes = mutableListOf<Client>()
               for (itemSnapshot in snapshot.children) {
                   val dataClass = itemSnapshot.getValue(Client::class.java)
                   dataClass?.let {
                       clientes.add(it)
                   }
               }
               // Se completa la corrutina con la lista de clientes obtenida
               continuation.resume(clientes)
               // Se remueve el listener después de obtener los datos
               databaseReference.removeEventListener(this)
           }

           override fun onCancelled(error: DatabaseError) {
               // En caso de error, se completa la corrutina con una excepción
               continuation.resumeWithException(error.toException())
           }
       }
       // Se agrega el listener a la base de datos
       databaseReference.addValueEventListener(listener)
   }







}