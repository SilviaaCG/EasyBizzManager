package com.silvia.easybizzmanager3.managment

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.silvia.easybizzmanager3.models.Client
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ClientManagment {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    private val database: DatabaseReference= Firebase.database.reference.child("clientes")

    fun agregarCliente(cliente: Client){
        val key = database.push().key
        if(key!=null){
            database.child(key).setValue(cliente)
        }
    }
    fun eliminarCliente(clienteId: String){
        database.child(clienteId).removeValue()
    }
    fun actualizarCliente(clienteId:String, clienteActualizado:Client){
        database.child(clienteId).setValue(clienteActualizado)
    }
    fun obtenerClientesFlow():Flow<List<Client>>{
        val flow = callbackFlow {
            val listener = database.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val clientes = snapshot.children.mapNotNull { snapshot ->
                        val cliente = snapshot.getValue(Client::class.java)
                        snapshot.key?.let { cliente?.copy(id = it) }
                    }
                    trySend(clientes.filter { it.id == uid }).isSuccess
                }
                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })
            awaitClose{database.removeEventListener(listener)}
        }
        return flow
    }




}