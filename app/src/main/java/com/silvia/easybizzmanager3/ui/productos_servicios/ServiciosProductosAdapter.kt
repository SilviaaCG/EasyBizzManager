package com.silvia.easybizzmanager3.ui.productos_servicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemProductoServicioListBinding
import com.silvia.easybizzmanager3.models.Item


class ServiciosProductosAdapter(
    val isEditable: Boolean,
    val onClickListener: (Int) -> Unit
): RecyclerView.Adapter<ServiciosProductosAdapter.ServiciosProductosViewHolder>() {
    var serviciosProductosList = ArrayList<Item>()
    fun setListaServiciosProductos(serviciosProductosLista:ArrayList<Item>){
        serviciosProductosList = serviciosProductosLista
        notifyDataSetChanged()
    }
    class ServiciosProductosViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemProductoServicioListBinding.bind(view)
        fun bind(item: Item, onClickListener: (Int) -> Unit,isEditable: Boolean){
            binding.nombreTextView.text = item.nombre
            binding.precioTextView.text = (item.cantidad * item.precioUnitario).toString()
            binding.cantidadTextView.text = item.cantidad.toString()
            binding.eliminarItemButton.setOnClickListener{
                onClickListener(this.adapterPosition)
            }
            if (isEditable){
                binding.eliminarItemButton.visibility = View.VISIBLE
            }else{
                binding.eliminarItemButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiciosProductosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_producto_servicio_list,
                parent,
                false
            )
        return ServiciosProductosViewHolder(view)
    }
    override fun getItemCount(): Int = serviciosProductosList.size
    override fun onBindViewHolder(holder: ServiciosProductosViewHolder, position: Int) {
        (holder as ServiciosProductosViewHolder).bind(serviciosProductosList[position],onClickListener,isEditable)
    }

}