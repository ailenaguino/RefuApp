package com.example.refuapp.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.refuapp.databinding.ActivityLoginBinding
import com.example.refuapp.ui.fragment.BottomNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth
        setListeners()
    }

    private fun setListeners(){
        binding.btnGoToSignin.setOnClickListener {
            goToSignin()
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = binding.txtEmailInput.text.toString()
        val password = binding.txtPasswordInput.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()) {
            binding.txtPasswordInputLayout.error = null
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.i("FirebaseAuth", "Se logueó el usuario")
                        goToHome()
                    } else {
                        binding.txtPasswordInputLayout.error = "Email y/o contraseña no válidos"
                        Log.i("FirebaseAuth", "No se pudo loguear el usuario", task.exception)
                    }
                }
        }else{
            binding.txtPasswordInputLayout.error = "Complete todos los campos"
        }
    }

    private fun goToSignin(){
        val intent = Intent(this, SigninActivity::class.java)
        startActivity(intent)
    }

    private fun goToHome(){
        val intent = Intent(this, BottomNavigationActivity::class.java)
        startActivity(intent)
    }
}