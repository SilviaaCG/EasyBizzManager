package com.silvia.easybizzmanager3.ui.client

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.silvia.easybizzmanager3.databinding.ItemClientListBinding
import com.silvia.easybizzmanager3.models.CircleTransform
import com.silvia.easybizzmanager3.models.Client
import com.squareup.picasso.Picasso
import java.io.File

class ClientSpinnerAdapter (context: Context,
                            private val clients: List<Client>)
    : ArrayAdapter<Client>(context, 0, clients) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }
    @SuppressLint("ViewHolder")
    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemClientListBinding.inflate(LayoutInflater.from(context), parent, false)
        val client = clients[position]

        if (!client.image.isNullOrEmpty()){
            Picasso.get().load(client.image).transform(CircleTransform()).into(binding.clientImageView)
        }


        binding.itemClientNombreTextView.text = "${client.nombre} ${client.apellidos}"

        return binding.root
    }

    }

