package com.silvia.easybizzmanager3.ui.factura

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.silvia.easybizzmanager3.databinding.ItemProductoServicioListBinding
import com.silvia.easybizzmanager3.models.Item


class BuscadorItemsAdapter(context: Context, val items: ArrayList<Item>, private val onClienteSelected: (Item) -> Unit)
    : ArrayAdapter<Item>(context, android.R.layout.simple_dropdown_item_1line, items) {
    private var itemsFiltrados: ArrayList<Item> = items
    override fun getCount(): Int {
        return itemsFiltrados.size
    }

    override fun getItem(position: Int): Item? {
        return itemsFiltrados[position]
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemProductoServicioListBinding
        val view: View

        if (convertView == null) {
            binding = ItemProductoServicioListBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemProductoServicioListBinding
            view = convertView
        }

        val item = getItem(position)

        if (item != null) {
            binding.nombreTextView.text = item.nombre
            binding.precioTextView.text = item.precioUnitario.toString()
            binding.eliminarItemButton.visibility = View.GONE
        }
        view.setOnClickListener {
            onClienteSelected(item!!)
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (!constraint.isNullOrEmpty()) {
                    val suggestions = mutableListOf<Item>()
                    for (item in items) {
                        if (item.nombre.contains(constraint, ignoreCase = true) ||
                            if(!item.descripcion.isNullOrEmpty()) item.descripcion!!.contains(constraint, ignoreCase = true)
                            else " ".contains(constraint, ignoreCase = true)) {
                            suggestions.add(item)
                        }
                    }
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                } else {
                    filterResults.values = items
                    filterResults.count = items.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    itemsFiltrados = results.values as ArrayList<Item>
                    notifyDataSetChanged()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                val item = resultValue as Item
                return item.nombre
            }
        }
    }
}