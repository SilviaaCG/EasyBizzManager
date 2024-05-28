package com.silvia.easybizzmanager3.ui.register

// MyAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.databinding.ItemFuncionListBinding
import com.silvia.easybizzmanager3.models.Funcion

class FuncionesAdapter(private val funciones: List<Funcion>, private val onClickListener: (String) -> Unit) :
    RecyclerView.Adapter<FuncionesAdapter.FuncionesViewHolder>() {

    class FuncionesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemFuncionListBinding.bind(view)

        fun bind(funcion:Funcion, onClickListener: (String) -> Unit){
            binding.textView.text = funcion.nombre
            binding.textView.setCompoundDrawablesWithIntrinsicBounds(funcion.icono,null,null,null)
            itemView.setOnClickListener {
                if (binding.checkBox.isChecked){
                    binding.checkBox.isChecked = false
                }else{
                    binding.checkBox.isChecked = true
                }
            }
            binding.abautButton.setOnClickListener {
                onClickListener(funcion.descripcion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuncionesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_funcion_list, parent, false)
        return FuncionesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FuncionesViewHolder, position: Int) {
        (holder as FuncionesAdapter.FuncionesViewHolder).bind(funciones[position],onClickListener)
    }

    override fun getItemCount(): Int = funciones.size

}
