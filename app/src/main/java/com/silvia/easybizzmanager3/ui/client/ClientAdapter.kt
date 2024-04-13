package com.silvia.easybizzmanager3.ui.client

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemClientListBinding
import com.silvia.easybizzmanager3.models.Client
import com.squareup.picasso.Picasso


class ClientAdapter(val clients:List<Client>,
                    private val onClickListener: (Client) -> Unit,
                    val onClickCall:(Int) -> Unit
            ):RecyclerView.Adapter<ClientAdapter.ClientViewHolder>()
    {

    class ClientViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemClientListBinding.bind(view)


            @SuppressLint("SuspiciousIndentation")
            fun bind(
                client: Client,
                onClickListener:(Client) -> Unit,
                onClickCall:(Int) -> Unit
            ){
            Picasso.get().load(client.image).into(binding.itemClientImageView)
            binding.itemClientNombreTextView.text = client.nombre + " " + client.apellidos
                itemView.setOnClickListener{
                    if (binding.clientLayoutButtons.isGone ) {
                        onClickListener(client)
                        binding.clientLayoutButtons.visibility = View.VISIBLE
                    } else {
                        binding.clientLayoutButtons.visibility = View.GONE
                    }

                }
            binding.callButton.setOnClickListener{
                onClickCall(adapterPosition)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_list, parent, false)
        return ClientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  clients.size
    }


    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        (holder as ClientViewHolder).bind(clients[position],onClickListener,onClickCall)

    }
}