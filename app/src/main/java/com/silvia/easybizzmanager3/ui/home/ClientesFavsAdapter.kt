package com.silvia.easybizzmanager3.ui.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemClientListBinding
import com.silvia.easybizzmanager3.databinding.ItemClienteFavListBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.silvia.easybizzmanager3.ui.client.ClientAdapter
import com.squareup.picasso.Picasso
import java.io.File

class ClientesFavsAdapter(val onClickListener: (Client) -> Unit):
    RecyclerView.Adapter<ClientesFavsAdapter.ClientesFavsViewHolder>() {

    var clientesFavsList = ArrayList<Client>()
    fun setLista(lista:ArrayList<Client>){
        clientesFavsList = lista
        notifyDataSetChanged()
    }
    class ClientesFavsViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemClienteFavListBinding.bind(view)
        fun bind(
            client: Client,
            onClickListener: (Client) -> Unit){
            if (!client.image.isNullOrEmpty()){
                Picasso.get().load(
                    client.image).transform(CircleTransform()).into(binding.clientImageView)
            }
            binding.nombreClienteTextView.text = client.nombre
            itemView.setOnClickListener {
                onClickListener(client)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesFavsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cliente_fav_list, parent, false)
        return ClientesFavsViewHolder(view)
    }
    override fun getItemCount(): Int = clientesFavsList.size
    override fun onBindViewHolder(holder: ClientesFavsViewHolder, position: Int) {
        (holder as ClientesFavsAdapter.ClientesFavsViewHolder).bind(clientesFavsList[position],onClickListener)
    }
}