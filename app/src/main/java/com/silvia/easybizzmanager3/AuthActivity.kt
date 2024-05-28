package com.silvia.easybizzmanager3
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.silvia.easybizzmanager3.databinding.ActivityAuthBinding
import com.silvia.easybizzmanager3.ui.ajustes.DetallesPerfilFragment
import java.util.Arrays


class AuthActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAuthBinding
    companion object {
        private const val TAG = "AuthActivity"
        private const val RC_SIGN_IN = 9001
    }
    // vars
    private lateinit var auth: FirebaseAuth
    lateinit var email : EditText
    lateinit var passw : EditText
    lateinit var callbackManager:CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        screenSplash.setKeepOnScreenCondition{false}

        callbackManager = CallbackManager.Factory.create()

        // Configurar opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Obtener el ID del cliente web desde el archivo de recursos
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/userinfo.profile"))
            .build()
        // Cambiar el color de la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.azul_cian)
        }
        //Esconder barra
        supportActionBar?.hide()

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Firebase AppCheck
        FirebaseApp.initializeApp(this)

        // Inizializacion de vars
        auth = Firebase.auth
        email = binding.emailEditText
        passw = binding.passwordEditTextText

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

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            googleSignInClient.signOut() // Esto cierra la sesión actual de Google
                .addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }

        }
    }







    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this,"El usuario no existe", Toast.LENGTH_SHORT).show()
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
        // Pasar el resultado de la actividad al SDK de Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    if (isNewUser) {
                        // El usuario es nuevo, mostrar el fragmento de registro
                        showRegistro()
                    } else {
                        // El usuario ya está registrado, mostrar la pantalla principal
                        showHome()
                    }
                    // Registro exitoso
                    val user = auth.currentUser


                } else {
                    // Registro fallido
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Error al iniciar sesión.", Toast.LENGTH_SHORT).show()
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
                            showRegistro()
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
    private fun showRegistro(){
        // Crear una instancia del fragmento
        val fragment = DetallesPerfilFragment()
        // Iniciar una transacción para agregar el fragmento al contenedor
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedorFragments, fragment)
        transaction.commit()
    }

    private fun appCheck() {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory( PlayIntegrityAppCheckProviderFactory.getInstance())
    }

    // Desactivar botón de andriod de return
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}