package com.silvia.easybizzmanager3.ui.factura

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.silvia.easybizzmanager3.models.Factura

class PaginaAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle)
{
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 ->{
                InfoFacturaFragment()
            }
            1 ->{
                FacturaPDFFragment()
            }

            else -> {
                InfoFacturaFragment()
            }
        }
    }




}