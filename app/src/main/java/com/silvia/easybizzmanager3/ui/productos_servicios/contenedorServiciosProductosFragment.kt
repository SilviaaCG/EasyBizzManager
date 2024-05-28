package com.silvia.easybizzmanager3.ui.productos_servicios

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.silvia.easybizzmanager3.R
import com.silvia.easybizzmanager3.ajustes
import com.silvia.easybizzmanager3.databinding.FragmentContenedorServiciosProductosBinding
import com.silvia.easybizzmanager3.models.Item
import com.silvia.easybizzmanager3.ui.factura.PaginaAdapter

class contenedorServiciosProductosFragment : Fragment() {

    private var _binding: FragmentContenedorServiciosProductosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductosYServiciosViewModel by activityViewModels()
    private lateinit var adapter:PaginaItemAdapter
    private val agregarButton by lazy { binding.agregarButton }
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentContenedorServiciosProductosBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ajustes().seleccionarFragment(activity as AppCompatActivity,R.id.nav_productos_servicios)
        ajustes().insertarTitulo( "Inventario",requireActivity() as AppCompatActivity)
        ajustes().mostrarAppBar(requireActivity() as AppCompatActivity)
        volverButton()
        initAdapterPaginas()
        initButtons()
        initMenu()


        return root
    }

    private fun volverButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.nav_home)
        }
    }

    private fun initMenu() {
        // Encuentra el DrawerLayout
        drawerLayout = requireActivity().findViewById(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        drawerToggle.syncState()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerLayout.removeDrawerListener(drawerToggle)
        _binding = null
    }

    private fun initButtons() {
        agregarButton.setOnClickListener {
            viewModel.insertarItemActual(Item())
            viewModel.insertarAccionItem(1)
            findNavController().navigate(R.id.action_contenedorServiciosProductosFragment_to_editarItemsFragment)
        }
    }

    private fun initAdapterPaginas() {
        adapter = PaginaItemAdapter(requireActivity().supportFragmentManager, requireActivity().lifecycle)
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