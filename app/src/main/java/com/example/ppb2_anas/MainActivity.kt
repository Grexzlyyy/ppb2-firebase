package com.example.ppb2_anas

import android.content.Intent
import androidx.credentials.GetCredentialRequest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.ppb2_anas.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityMainBinding
    private lateinit var credentialmanager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        credentialmanager = CredentialManager.create(this)
        auth = Firebase.auth

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerEvents()
    }

    fun registerEvents() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                val request = prepareRequest()
                loginByGoogle(request)
            }
        }
    }

    fun prepareRequest() : GetCredentialRequest {
        val serverClient = "884920763205-fu2umruv6lj9ffs525dnvssibje3du49.apps.googleusercontent.com"

        val googleOption = GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClient)
            .build()

        val request = GetCredentialRequest
            .Builder()
            .addCredentialOption(googleOption)
            .build()

        return request

    }

    suspend fun loginByGoogle(request:GetCredentialRequest) {
        try {
            val result = credentialmanager.getCredential(
                context = this,
                request = request
            )

            val credential = result.credential
            val idToken = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseLoginCallback(idToken.idToken)

        } catch (exc: NoCredentialException) {
            Toast.makeText(this, "Login Gagal :" + exc.message, Toast.LENGTH_LONG).show()
        } catch (exc: Exception) {
            Toast.makeText(this, "Login Gagal :" + exc.message, Toast.LENGTH_LONG).show()
        }
    }

    fun firebaseLoginCallback(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_LONG).show()
//                        pindah ke halaman todlist
                    toTodoListPage()
                } else {
                    Toast.makeText(this, "Login gagal", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun toTodoListPage() {
        val intent = Intent(this, TaksActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            toTodoListPage()
        }
    }
}