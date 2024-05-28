package com.silvia.easybizzmanager3.ui.client

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemClientListBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.silvia.easybizzmanager3.models.Factura
import com.squareup.picasso.Picasso
import java.io.File


class ClientAdapter(val clients:List<Client>,
                    private val onClickListener: (Client) -> Unit
            ):RecyclerView.Adapter<ClientAdapter.ClientViewHolder>()
    {
    var listaClientes = clients
        fun insertarlista(lista:ArrayList<Client>){
            listaClientes = lista
            notifyDataSetChanged()
        }

    class ClientViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemClientListBinding.bind(view)

            @SuppressLint("SuspiciousIndentation")
            fun bind(
                client: Client,
                onClickListener: (Client) -> Unit,


            ){
                if (!client.image.isNullOrEmpty()){
                    Picasso.get().load(client.image
                    ).transform(CircleTransform()).into(binding.clientImageView)
                }
            binding.itemClientNombreTextView.text = client.nombre + " " + client.apellidos
                itemView.setOnClickListener {
                    onClickListener(client)
                }

    }
        private fun calcularNumFacturas(client: Client,listaFactura: ArrayList<Factura>): Int {
            var suma = 0
            for (factura in listaFactura){
                if (factura.comprador == client){
                    suma++
                }
            }
            return suma
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_list, parent, false)
        return ClientViewHolder(view)
    }

    override fun getItemCount(): Int = listaClientes.size


    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        (holder as ClientViewHolder).bind(listaClientes[position],onClickListener)
    }


}
