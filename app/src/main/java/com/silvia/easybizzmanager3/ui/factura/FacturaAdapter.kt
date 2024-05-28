package com.silvia.easybizzmanager3.ui.factura

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemFacturaListBinding
import com.silvia.easybizzmanager3.models.Factura
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FacturaAdapter(val onInfoClickListener: (Factura) -> Unit):RecyclerView.Adapter<FacturaAdapter.FacturaViewHolder>() {
    var facturaList = ArrayList<Factura>()
    fun setListaFacturas(facturaLista: ArrayList<Factura>) {
        facturaList = facturaLista
        notifyDataSetChanged()
    }

    class FacturaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemFacturaListBinding.bind(view)
        fun bind(
            factura: Factura,
            onInfoClickListener: (Factura) -> Unit,
        ) {

            itemView.setOnClickListener {
                onInfoClickListener(factura)
            }
            binding.itemFacturaNumeroFactura.text = "#" + factura.numeroFactura

            if (factura.fechaValidez.toString().isNotEmpty()) {
                binding.estadoImageView.setImageResource(obtenerEstado(factura))
            }
            calcularEstado(factura,binding.estadoImageView)
            binding.NombreClienteTextView.text =
                factura.comprador.nombre + " " + factura.comprador.apellidos
            binding.itemFacturaPrecioTotal.text = factura.importeTotal.toString()


        }
        private fun calcularEstado( facturaActual:Factura, imageImageView: ImageView ) {
            val fechaActual = LocalDate.now()
            if (!facturaPagada(facturaActual)){
                if (facturaActual.fechaValidez != null && facturaActual.fechaValidez!!.isNotEmpty()){
                    if (fechaActual.isAfter(LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))){
                        facturaActual.estado = Factura.EstadoFactura.RETRASO
                    }
                    else if (fechaActual.isBefore(LocalDate.parse(
                            facturaActual.fechaValidez,
                            DateTimeFormatter.ofPattern("dd-M-yyyy")))&& facturaActual.pagos!!.isNotEmpty()){
                        facturaActual.estado = Factura.EstadoFactura.PAGO_PARCIAL
                    }else if(fechaActual.isBefore(LocalDate.parse(
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

        private fun actualizarEstado(facturaActual: Factura,imageImageView: ImageView) {
            when (facturaActual.estado){
                Factura.EstadoFactura.SIN_ESTADO ->{
                    imageImageView.setImageResource(R.drawable.label_azul)

                }
                Factura.EstadoFactura.PENDIENTE -> {

                    imageImageView.setImageResource(R.drawable.label_pendiente)
                }
                Factura.EstadoFactura.PAGO_PARCIAL -> {

                    imageImageView.setImageResource(R.drawable.label_pagoparcial)
                }
                Factura.EstadoFactura.RETRASO -> {

                    imageImageView.setImageResource(R.drawable.label_retraso)
                }
                Factura.EstadoFactura.PAGADO -> {
                    imageImageView.setImageResource(R.drawable.label_pagado)
                }
            }
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

        fun obtenerEstado(factura: Factura): Int {
            // Obtener la fecha actual
            val fechaActual = LocalDate.now()
            var draw:Int = 0
            // Comparar las fechas para determinar el estado de la factura
            if (factura.pagos!!.isEmpty() || factura.pagos == null) {
                return when {
                    fechaActual.isBefore(
                        LocalDate.parse(
                            factura.fechaValidez.toString(),
                            DateTimeFormatter.ofPattern("dd-M-yyyy")
                        )
                    ) -> {
                        // Pendiente
                        R.drawable.label_pendiente
                    }
                    fechaActual.isAfter(
                        LocalDate.parse(
                            factura.fechaValidez.toString(),
                            DateTimeFormatter.ofPattern("dd-M-yyyy")
                        )
                    ) -> {
                        // Retrasado
                        R.drawable.label_retraso
                    }

                    else -> {
                        // Pagado
                        R.drawable.label_pendiente
                    }
                }
            }else{
                return R.drawable.label_pagoparcial
            }

        }


        private fun obtenerFechaFormateada(): Any {
            val fechaActual = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            return fechaActual.format(formatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_factura_list,
                parent,
                false
            )
        return FacturaViewHolder(view)
    }

    override fun getItemCount(): Int = facturaList.size

    override fun onBindViewHolder(
        holder: FacturaViewHolder,
        position: Int,
    ) {
        holder.bind(facturaList[position],onInfoClickListener)
    }

}

