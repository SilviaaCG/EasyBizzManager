package com.silvia.easybizzmanager3

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.silvia.easybizzmanager3.databinding.ActivityAuthBinding
import com.silvia.easybizzmanager3.databinding.ActivityMainBinding

class AuthActivity : AppCompatActivity() {

    /**
     * IDEA
     * - Mostrar en auth fragments del registro para guardar los datos
     */

    private lateinit var binding: ActivityAuthBinding
    companion object {
        private const val TAG = "AuthActivity"
        private const val RC_SIGN_IN = 9001
    }
    // vars
    private lateinit var auth: FirebaseAuth
    lateinit var email : EditText
    lateinit var passw : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Obtener el ID del cliente web desde el archivo de recursos
            .requestEmail()
            .build()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase AppCheck
        FirebaseApp.initializeApp(this)

        // Inizializacion de vars
        auth = Firebase.auth
        email = binding.emailEditText
        passw = binding.passwordEditTextText


        title = "Autenticación"

        // Botones

        binding.signUpButton.setOnClickListener{
            // Registrar
            registraUsuario()
        }

        binding.signInButton.setOnClickListener{
            // Iniciar sesion
            iniciarSesionUsuario()
        }

        binding.googleButton.setOnClickListener{
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtener la cuenta de Google
                val account = task.getResult(ApiException::class.java)
                // Autenticar con Firebase
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    val user = auth.currentUser
                    // Continuar con la lógica de tu aplicación después del registro
                    showHome()
                } else {
                    // Registro fallido
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private fun iniciarSesionGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    // Obtener los datos de la cuenta de google
                    val account = task.getResult(ApiException::class.java)
                    registraUsuario()
                }catch (e:ApiException){
                    e.printStackTrace()
                }
            }
        }
        // Crear un intent para iniciar la actividad de inicio de sesión de Google
        val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun iniciarSesionUsuario() {
        if (email.text.isNotEmpty() && passw.text.isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.text.toString(),passw.text.toString())
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome()
                    }else{
                        showAlert("Error","Se ha producido un error de autenticación al usuario")
                    }
                }
        }
    }

    private fun registraUsuario() {

            if (email.text.isNotEmpty() && passw.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email.text.toString(), passw.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome()
                        } else if(passw.length() < 6){
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", it.exception)
                            Toast.makeText(
                                baseContext,
                                "La contraseña debe tener 6 o más caracteres",
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else{
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", it.exception)
                            Toast.makeText(
                                baseContext,
                                "Error al crear el usuario",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            } else {
                showAlert("Error", "Se ha producido un error de autenticación al usuario")
            }

    }

    private fun showAlert(title: String, mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome() {
        val homeIntent = Intent(this, MainActivity::class.java)
        startActivity(homeIntent)
    }

    private fun appCheck() {
        //FirebaseAppCheck.getInstance().installAppCheckProviderFactory( PlayIntegrityAppCheckProviderFactory.getInstance())
    }

    // Desactivar botón de andriod de return
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}