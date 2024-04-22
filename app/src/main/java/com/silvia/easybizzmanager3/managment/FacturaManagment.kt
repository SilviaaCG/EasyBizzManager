package com.silvia.easybizzmanager3.managment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.silvia.easybizzmanager3.models.Factura
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FacturaManagment {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid
    val databaseReference = FirebaseDatabase.getInstance().getReference("usuarios").child(uid!!).child("facturas")

    fun agregarFactura(factura: Factura){
        databaseReference.child(factura.numeroFactura).setValue(factura)
    }

    fun actualizarFactura(numeroFactura:Int,facturaActualizada:Factura){
        databaseReference.child(numeroFactura.toString()).setValue(facturaActualizada)
    }

    fun eliminarFactura(numeroFactura:Int){
        databaseReference.child(numeroFactura.toString()).removeValue()
    }

    suspend fun obtenerFacturas():List<Factura> = suspendCoroutine { continuation ->
        val listener = object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val facturas = mutableListOf<Factura>()
                for (facturaSnapshot in snapshot.children){
                    val factura = facturaSnapshot.getValue(Factura::class.java)
                    factura?.let {
                        facturas.add(it)
                    }
                }
                continuation.resume(facturas)
                databaseReference.removeEventListener(this)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        }
        databaseReference.addValueEventListener(listener)
    }

}