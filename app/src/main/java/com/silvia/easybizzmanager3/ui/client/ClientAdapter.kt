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
import com.squareup.picasso.Picasso
import java.io.File


class ClientAdapter(val clients:List<Client>,
                    private val onClickListener: (Client) -> Unit,
                    val onClickCall:(Int) -> Unit
            ):RecyclerView.Adapter<ClientAdapter.ClientViewHolder>()
    {
    var listaClientes = clients
    class ClientViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemClientListBinding.bind(view)


            @SuppressLint("SuspiciousIndentation")
            fun bind(
                client: Client,
                onClickListener: (Client) -> Unit,
                onClickCall: (Int) -> Unit
            ){
                Picasso.get().load(
                    Uri.fromFile(
                        File(if (
                            !client.image.equals("sin imagen")) client.image
                    else "/data/user/0/com.silvia.easybizzmanager3/files/images/99d68521-3863-4f13-81a4-f9ed6380c63c_image.png"))
                ).transform(CircleTransform()).into(binding.clientImageView)
            binding.itemClientNombreTextView.text = client.nombre + " " + client.apellidos
            binding.callButton.setOnClickListener{
                onClickCall(adapterPosition)
            }
                itemView.setOnClickListener {
                    binding.clientLayoutButtons.visibility =
                        if (binding.clientLayoutButtons.visibility == View.VISIBLE) View.GONE
                        else View.VISIBLE
                }
                binding.infoClientButton.setOnClickListener {
                    onClickListener(client)
                }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_list, parent, false)
        return ClientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  listaClientes.size
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        (holder as ClientViewHolder).bind(listaClientes[position],onClickListener,onClickCall)
    }


}