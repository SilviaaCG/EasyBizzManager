package com.silvia.easybizzmanager3

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.silvia.easybizzmanager3.databinding.ActivityMainBinding
import com.silvia.easybizzmanager3.databinding.AppBarMainBinding
import com.silvia.easybizzmanager3.databinding.FragmentDetallesPerfilBinding
import com.silvia.easybizzmanager3.managment.AuthManager
import com.silvia.easybizzmanager3.ui.ajustes.DetallesPerfilFragment
import com.silvia.easybizzmanager3.ui.ajustes.DetallesPerfilViewModel
import com.silvia.easybizzmanager3.ui.client.ClientFragment
import com.silvia.easybizzmanager3.ui.factura.FacturaFragment
import com.silvia.easybizzmanager3.ui.home.HomeFragment
import com.silvia.easybizzmanager3.ui.productos_servicios.contenedorServiciosProductosFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    // Find the NavController

    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        screenSplash.setKeepOnScreenCondition { false }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        drawerLayout = binding.drawerLayout

        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener(this)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        val toogle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        if(savedInstanceState == null){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, HomeFragment()).commit()
            navView.setCheckedItem(R.id.nav_home)
        }
        /**
         * val navController = findNavController(R.id.nav_host_fragment_content_main)
         *         // Passing each menu ID as a set of Ids because each
         *         // menu should be considered as top level destinations.
         *         appBarConfiguration = AppBarConfiguration(
         *             setOf(
         *                 R.id.nav_home, R.id.nav_clientes, R.id.nav_facturas, R.id.nav_productos_servicios
         *             ), drawerLayout
         *         )
         *         setupActionBarWithNavController(navController, appBarConfiguration)
         *         navView.setupWithNavController(navController)
         *         navView.setNavigationItemSelectedListener { item ->
         *             when (item.itemId) {
         *                 R.id.nav_productos_servicios -> {
         *                     navController.navigate(R.id.contenedorServiciosProductosFragment)
         *                     drawerLayout.closeDrawers()
         *                     true
         *                 }
         *                 R.id.nav_clientes ->{
         *                     navController.navigate(R.id.nav_clientes)
         *                     drawerLayout.closeDrawers()
         *                     true
         *                 }
         *                 R.id.nav_facturas ->{
         *                     navController.navigate(R.id.nav_facturas)
         *                     drawerLayout.closeDrawers()
         *                     true
         *                 }
         *                 R.id.nav_home ->{
         *                     navController.navigate(R.id.nav_home)
         *                     drawerLayout.closeDrawers()
         *                     true
         *                 }
         *
         *                 else -> false
         *             }
         *         }
         */




    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.actionSettings -> {
                abrirFragment(DetallesPerfilFragment())
                true
            }
            R.id.actionLogOut -> {
                AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Está seguro de que quiere cerrar sesión?")
                    .setPositiveButton("Sí") { dialog, _ ->
                        FirebaseAuth.getInstance().signOut()
                        showAuth()
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
                true
            }

            else -> false
        }
    }
    private fun showAuth() {
        val authIntent = Intent(this, AuthActivity::class.java)
        startActivity(authIntent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> navController.navigate(R.id.nav_home)
            R.id.nav_clientes -> navController.navigate(R.id.nav_clientes)

            R.id.nav_productos_servicios -> navController.navigate(R.id.contenedorServiciosProductosFragment)
            R.id.nav_facturas -> navController.navigate(R.id.nav_facturas)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(   GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun abrirFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_content_main, fragment)
        transaction.addToBackStack(null) // Añadir a la pila para poder volver atrás
        transaction.commit()
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



}
