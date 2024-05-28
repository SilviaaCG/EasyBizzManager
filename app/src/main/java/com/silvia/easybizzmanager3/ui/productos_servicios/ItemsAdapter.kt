package com.silvia.easybizzmanager3.ui.productos_servicios

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemFacturaListBinding
import com.silvia.easybizzmanager3.databinding.ItemProductoListBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.models.TransformImage
import com.silvia.easybizzmanager3.ui.factura.FacturaAdapter
import com.squareup.picasso.Picasso


class ItemsAdapter( val onItemClickListener:(Item) -> Unit): RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {
    var itemsList = ArrayList<Item>()
    fun setListaItems(lista: ArrayList<Item>) {
        itemsList = lista
        notifyDataSetChanged()
    }
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemProductoListBinding.bind(view)
        private val categoriaTextView by lazy { binding.categoriaTextView }
        private val nombreTextView by lazy { binding.nombreTextView }
        private val descripcionTextView by lazy { binding.descripcionTextView }
        private val precioTextView by lazy { binding.precioTextView }
        private val ImageView by lazy { binding.clientImageView }

        fun bind(
            item: Item,
            onItemClickListener: (Item) -> Unit,
        ) {
            if (!item.imagen.isNullOrEmpty()){
                Picasso.get().load(item.imagen
                ).transform(TransformImage(20F)).into(ImageView)
            }
            if (!item.categoria.isNullOrEmpty()){
                categoriaTextView.visibility = View.VISIBLE
                categoriaTextView.text = item.categoria
            }else{
                categoriaTextView.visibility = View.GONE
            }
            if(!item.descripcion.isNullOrEmpty()){
                descripcionTextView.text = item.descripcion
            }else{
                descripcionTextView.text = " "
            }
            nombreTextView.text = item.nombre
            precioTextView.text = item.precioUnitario.toString()
            itemView.setOnClickListener { onItemClickListener(item) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_producto_list,
                parent,
                false
            )
        return ItemsAdapter.ItemViewHolder(view)
    }

    override fun getItemCount(): Int = itemsList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemsList[position], onItemClickListener)
    }
}