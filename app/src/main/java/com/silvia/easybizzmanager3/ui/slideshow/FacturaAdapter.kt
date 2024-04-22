package com.silvia.easybizzmanager3.ui.slideshow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemFacturaListBinding
import com.silvia.easybizzmanager3.models.Factura

class FacturaAdapter(val onInfoClickListener: (Factura) -> Unit):RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>() {
   var facturaList = ArrayList<Factura>()
   fun setListaFacturas(facturaLista:ArrayList<Factura>){
       facturaList = facturaLista
       notifyDataSetChanged()
   }
    class FacturaViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemFacturaListBinding.bind(view)
        fun bind(
            factura: Factura,
            onInfoClickListener: (Factura) -> Unit,
        ){
            onInfoClickListener(factura)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_factura_list,
                parent,
                false
            )
        return FacturaViewHolder(view)
    }

    override fun getItemCount(): Int = facturaList.size

    override fun onBindViewHolder(
        holder: FacturaViewHolder,
        position: Int,
    ) {
        holder.bind(facturaList[position],onInfoClickListener)
    }

}
