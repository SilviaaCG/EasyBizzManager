package com.silvia.easybizzmanager3.ui.factura

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.core.content.ContextCompat
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemClientListBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.squareup.picasso.Picasso

class BuscadorClienteAdapter (context: Context, val clientes: ArrayList<Client>,private val onClienteSelected: (Client) -> Unit) : ArrayAdapter<Client>(context, android.R.layout.simple_dropdown_item_1line, clientes) {
    private var clientesFiltrados: ArrayList<Client> = clientes
    override fun getCount(): Int {
        return clientesFiltrados.size
    }

    override fun getItem(position: Int): Client? {
        return clientesFiltrados[position]
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemClientListBinding
        val view: View

        if (convertView == null) {
            binding = ItemClientListBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemClientListBinding
            view = convertView
        }

        val cliente = getItem(position)
        Picasso.get().cancelRequest(binding.clientImageView)
        binding.clientImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cliente))
        if (cliente != null) {
            binding.itemClientNombreTextView.text = "${cliente.nombre} ${cliente.apellidos}"
            if (!cliente.image.isNullOrEmpty()) {
                Picasso.get().load(cliente.image).transform(CircleTransform()).into(binding.clientImageView)
            }
        }
        view.setOnClickListener {
            onClienteSelected(cliente!!)
        }



        return view
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (!constraint.isNullOrEmpty()) {
                    val suggestions = mutableListOf<Client>()
                    for (cliente in clientes) {
                        if (cliente.nombre.contains(constraint, ignoreCase = true) ||
                            cliente.apellidos.contains(constraint, ignoreCase = true)) {
                            suggestions.add(cliente)
                        }
                    }
                    filterResults.values = suggestions
                    filterResults.count = suggestions.size
                } else {
                    filterResults.values = clientes
                    filterResults.count = clientes.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    clientesFiltrados = results.values as ArrayList<Client>
                    notifyDataSetChanged()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                val cliente = resultValue as Client
                return "${cliente.nombre} ${cliente.apellidos}"
            }
        }
    }
}