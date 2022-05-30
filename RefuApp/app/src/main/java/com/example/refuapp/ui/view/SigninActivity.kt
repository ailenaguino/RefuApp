package com.example.refuapp.ui.view

import android.R.attr
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.refuapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.R.attr.password
import java.util.regex.Matcher
import java.util.regex.Pattern


class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth

        setListeners()
    }

    private fun setListeners(){
        binding.btnSignin.setOnClickListener {
            if(binding.txtEmailSigninInput.text.toString()!= "" &&
                binding.txtPasswordSigninInput1.text.toString() != "" &&
                    binding.txtPasswordSigninInput2.text.toString() != ""){
                createUserWithEmailAndPassword()
            }else{
                binding.txtEmailSigninInputLayout.error = "Por favor completar todos los campos"
            }
        }
    }

    private fun createUserWithEmailAndPassword() {
        if (checkPasswords()) {
            auth.createUserWithEmailAndPassword(
                binding.txtEmailSigninInput.text.toString(),
                binding.txtPasswordSigninInput1.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        writeSpecificEmailErrors(task.exception?.javaClass?.canonicalName ?: "")
                    }
                }
        }
    }

    private fun checkPasswords(): Boolean{
        binding.txtPasswordSigninInputLayout1.error = null
        binding.txtPasswordSigninInputLayout2.error = null
        binding.txtEmailSigninInputLayout.error = null
        return if(!(binding.txtPasswordSigninInput1.text.toString() == binding.txtPasswordSigninInput2.text.toString())){
            binding.txtPasswordSigninInputLayout2.error = "Las contraseñas no coinciden"
            false
        }else if(binding.txtPasswordSigninInput1.text.toString().length < 6){
            binding.txtPasswordSigninInputLayout1.error = "La contraseña debe tener al menos 6 caracteres"
            false
        } else if(!passwordRegex()){
            binding.txtPasswordSigninInputLayout1.error =
                "La contraseña debe tener al menos un número, una letra en minúscula, y una letra en mayúscula"
            false
        } else {
            true
        }
    }


    private fun passwordRegex(): Boolean{
        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(binding.txtPasswordSigninInput1.text.toString())

        return matcher.matches()

    }

    private fun writeSpecificEmailErrors(error:String){
        binding.txtPasswordSigninInputLayout1.error = null
        binding.txtPasswordSigninInputLayout2.error = null
        binding.txtEmailSigninInputLayout.error = null
        when(error){
            "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException" ->
                binding.txtEmailSigninInputLayout.error = "Email no válido"
            "com.google.firebase.auth.FirebaseAuthUserCollisionException" ->
                binding.txtEmailSigninInputLayout.error = "El email ya está en uso"
        }
    }
}