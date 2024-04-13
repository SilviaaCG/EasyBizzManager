package com.silvia.easybizzmanager3.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.silvia.easybizzmanager3.AuthActivity
import com.silvia.easybizzmanager3.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Saludo
        homeViewModel.setEmail(user?.email.toString()?:"no email provided")
        homeViewModel.email.observe(viewLifecycleOwner) {
            binding.saludoTextView.text = "Hola " + it + "!"
        }

        binding.signOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            showAuth()
        }
        return root
    }

    private fun showAuth() {
        val authIntent = Intent(context, AuthActivity::class.java)
        startActivity(authIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}