package com.silvia.easybizzmanager3.ui.factura

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TableLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentContenedorFacturaBinding
import com.silvia.easybizzmanager3.databinding.FragmentFacturaBinding

class ContenedorFacturaFragment : Fragment() {
    private var _binding: FragmentContenedorFacturaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:PaginaAdapter
    private val facturaViewModel: FacturaViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_facturas)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentContenedorFacturaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initAdapterPaginas()
        volverButton()
        return root
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_contenedorFacturaFragment_to_nav_facturas)
        }
    }

    private fun initAdapterPaginas() {
        adapter = PaginaAdapter(requireActivity().supportFragmentManager, requireActivity().lifecycle)
        binding.viewPager2.adapter = adapter
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager2.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

}