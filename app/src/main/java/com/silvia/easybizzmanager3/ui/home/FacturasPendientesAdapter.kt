package com.silvia.easybizzmanager3.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemFacturaInicioListBinding
import com.silvia.easybizzmanager3.models.Factura
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FacturasAdapter(val boolean: Boolean,val onItemClickListener: (Factura) -> Unit): RecyclerView.Adapter<FacturasAdapter.FacturasViewHolder>(){
    var facturasPendientesList = ArrayList<Factura>()
    fun setLista(lista:ArrayList<Factura>){
        facturasPendientesList = lista
        notifyDataSetChanged()
    }
    class FacturasViewHolder(val view: View):RecyclerView.ViewHolder(view) {
        val binding = ItemFacturaInicioListBinding.bind(view)
        fun bind(factura: Factura, onItemClickListener: (Factura) -> Unit, boolean: Boolean){
           binding.numeroFacturaTextView.text = "#${factura.numeroFactura}"
            binding.NombreClienteTextView.text = factura.comprador.nombre
            binding.precioTextView.text = factura.importeTotal.toString()
            itemView.setOnClickListener {
                onItemClickListener(factura)
            }
            calcularEstado(factura,binding.estadoImageView)
            if (!boolean){
                binding.estadoImageView.visibility = View.GONE
            }else{
                binding.estadoImageView.visibility = View.VISIBLE
            }
        }


        private fun calcularEstado( facturaActual:Factura, imageImageView: ImageView) {
            val fechaActual = LocalDate.now()
            if (!facturaPagada(facturaActual)){
                if (facturaActual.fechaValidez != null && facturaActual.fechaValidez!!.isNotEmpty()){
                    if (fechaActual.isAfter(
                            LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))){
                        facturaActual.estado = Factura.EstadoFactura.RETRASO
                    }
                    else if (fechaActual.isBefore(
                            LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))&& facturaActual.pagos!!.isNotEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PAGO_PARCIAL
                    }else if(fechaActual.isBefore(
                            LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))&& facturaActual.pagos!!.isEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PENDIENTE
                    }
                }else{
                    if (facturaActual.pagos!!.isNotEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PAGO_PARCIAL
                    }else{
                        facturaActual.estado = Factura.EstadoFactura.PENDIENTE

                    }
                }
            }else{
                facturaActual.estado = Factura.EstadoFactura.PAGADO
            }

            actualizarEstado(facturaActual,imageImageView)
        }

        private fun facturaPagada(factura: Factura): Boolean {
            var cantidadPagada = 0.0
            if (factura.pagos != null){
                for (pago in factura.pagos!!){
                    cantidadPagada += pago.cantidad
                }
            }
            return if (cantidadPagada >= factura.importeTotal){
                true
            }else{
                false
            }
        }

        private fun actualizarEstado(facturaActual: Factura,imageImageView: ImageView) {
            when (facturaActual.estado){
                Factura.EstadoFactura.SIN_ESTADO ->{
                    imageImageView.setBackgroundResource(R.drawable.label_azul)

                }
                Factura.EstadoFactura.PENDIENTE -> {

                    imageImageView.setBackgroundResource(R.drawable.label_pendiente)
                }
                Factura.EstadoFactura.PAGO_PARCIAL -> {

                    imageImageView.setBackgroundResource(R.drawable.label_pagoparcial)
                }
                Factura.EstadoFactura.RETRASO -> {
                    imageImageView.setBackgroundResource(R.drawable.label_retraso)
                }
                Factura.EstadoFactura.PAGADO -> {
                    imageImageView.setBackgroundResource(R.drawable.label_pagado)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_factura_inicio_list,parent,false)
        return  FacturasViewHolder(view)

    }

    override fun getItemCount(): Int = facturasPendientesList.size

    override fun onBindViewHolder(holder: FacturasViewHolder, position: Int) {
        (holder as FacturasViewHolder).bind(facturasPendientesList[position],onItemClickListener, boolean)
    }
}