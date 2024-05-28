package com.silvia.easybizzmanager3.ui.factura

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemPagoListBinding
import com.silvia.easybizzmanager3.databinding.ItemProductoServicioListBinding
import com.silvia.easybizzmanager3.models.Pago

class PagoAdapter(val onPagoClickListener: (Int)->Unit): RecyclerView.Adapter<PagoAdapter.PagoViewHolder>() {
    var pagosList = ArrayList<Pago>()
    fun setListaPagos(lista:ArrayList<Pago>){
        pagosList = lista
        notifyDataSetChanged()
    }
    class PagoViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemPagoListBinding.bind(view)
        fun bind(pago: Pago,onPagoClickListener: (Int) -> Unit){
            binding.metodoPagoTextView.text = pago.modoPago
            binding.fechaTextView.text = pago.fecha
            binding.precioTextView.text = pago.cantidad.toString()
            binding.eliminarPago.setOnClickListener{
                onPagoClickListener(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_pago_list,
                parent,
                false
            )
        return PagoAdapter.PagoViewHolder(view)
    }

    override fun getItemCount(): Int = pagosList.size

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        (holder as PagoViewHolder).bind(pagosList[position], onPagoClickListener)
    }

}