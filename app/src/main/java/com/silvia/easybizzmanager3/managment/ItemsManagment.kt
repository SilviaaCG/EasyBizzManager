package com.silvia.easybizzmanager3.managment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.models.Item
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ItemsManagment {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(uid!!).child("items")
    fun agregarItem(item: Item){
        databaseReference.child(item.id).setValue(item)
    }

    fun actualizarItem(id:String, itemActualizado:Item){
        databaseReference.child(id).setValue(itemActualizado)
    }

    fun eliminarItem(id: String){
        databaseReference.child(id).removeValue()
    }
    suspend fun obtenerItems():List<Item> = suspendCoroutine { continuation ->
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Item>()
                for (itemSnapshot in snapshot.children){
                    val item = itemSnapshot.getValue(Item::class.java)
                    item?.let {
                        items.add(it)
                    }
                }
                continuation.resume(items)
                databaseReference.removeEventListener(this)
            }
            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }
        databaseReference.addValueEventListener(listener)
    }

}