package com.example.refuapp.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.refuapp.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth

        setListeners()
        getUserData()
    }

    private fun setListeners() {
        binding.btnAcceptChanges.setOnClickListener {
            changeUserData()
        }
        binding.btnEditProfilePicture.setOnClickListener {
            checkPermissions()
        }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data!!
            binding.ivEditProfilePicture.setImageURI(imageUri)
            Log.i("Foto", "$imageUri")
        }
    }

    private fun uploadImage() {
        if(imageUri!=null){
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val lastCharacters = imageUri.toString().substring(imageUri.toString().length-10)
            val fileName = formatter.format(now) + lastCharacters
            val storageReference = FirebaseStorage.getInstance().getReference("profilePictures/$fileName")
            storageReference.putFile(imageUri!!).addOnSuccessListener {
                Log.i("Storage", "Se pudo subir la imagen")
                changeUserProfilePicture(fileName)
            }.addOnFailureListener{
                Log.i("Storage", "No se pudo subir la imagen",)
            }
        }
    }


    private fun changeUserData() {
        val user = Firebase.auth.currentUser
        var email = ""
        user?.let {
            email = user.email.toString()
        }
        binding.txtEditNameInputLayout.error = null
        if(!binding.txtEditNameInput.text.isNullOrEmpty()) {
            database.collection("users").document(email)
                .update(
                    "biography",binding.txtEditBiographyInput.text.toString(),
                    "name",binding.txtEditNameInput.text.toString())
                .addOnSuccessListener {
                    if(imageUri!=null){
                        uploadImage()
                    }else{Log.i("Image Uri", "Image uri es null")}
                }
            finish()
        }else{
            binding.txtEditNameInputLayout.error = "Por favor ingresar un nombre"
        }
    }

    private fun changeUserProfilePicture(fileName: String?) {
        val user = Firebase.auth.currentUser
        var email = ""
        user?.let {
            email = user.email.toString()
        }
        FirebaseStorage.getInstance().reference.child("profilePictures/$fileName")
            .downloadUrl.addOnSuccessListener {
                Log.i("Obtener Url", it.toString())
                database.collection("users").document(email)
                    .update(
                        "profilePicture", it.toString()
                    )
            }.addOnFailureListener {
                Log.i("Obtener Url", "Fallo al obtener url")
            }
    }

    private fun getUserData() {
        val user = Firebase.auth.currentUser
        var email = ""
        user?.let {
            email = user.email.toString()
        }
        database.collection("users").document(email)
            .get().addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.data?.get("name")
                val biography = documentSnapshot.data?.get("biography")
                val profilePicture = documentSnapshot.data?.get("profilePicture")

                binding.txtEditNameInput.setText(name.toString(), TextView.BufferType.EDITABLE)
                binding.txtEditBiographyInput.setText(biography.toString(), TextView.BufferType.EDITABLE)
                Picasso.get().load(profilePicture.toString()).into(binding.ivEditProfilePicture)

            }.addOnFailureListener {
                Log.i("Firestore", "Falló la obtencion de los datos del usuario")
            }
    }

    private fun checkPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED){
            requestGalleryPermission()
        }else{
            selectImage()
        }
    }

    private fun requestGalleryPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //El usuario ya rechazó el permiso
        }else{
            //el usuario no los rechazo todavia, se piden los permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        //Codigo de verificacion, el siguiente metodo va a tener un listener que escucha cada vez que se acepte un permiso
            777)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage()
            }else{
                Toast.makeText(this, "Permisos rechazados", Toast.LENGTH_LONG).show()
            }
        }
    }
}