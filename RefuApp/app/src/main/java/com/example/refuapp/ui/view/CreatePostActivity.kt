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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.refuapp.databinding.ActivityCreatePostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val database = Firebase.firestore
    private var imageUri: Uri? = null
    private lateinit var email: String
    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        auth = Firebase.auth
        val user = Firebase.auth.currentUser
        email = ""
        user?.let {
            email = user.email.toString()
        }
        setListeners()
    }

    private fun setListeners() {
        binding.btnAddPostImage.setOnClickListener {
            checkPermissions()
        }
        binding.btnPublishPost.setOnClickListener {
            uploadPost()
        }
    }

    private fun uploadPost() {
        if(checkEmptyFields()){
            val fileName = uploadImage()
            val downloadUrl = getDownloadUrl(fileName)
            val title = binding.txtEditPostTitleInput.text.toString()
            val body = binding.txtEditPostBodyInput.text.toString()
            val isSensitiveContent = binding.cbIsSensitiveContent.isChecked

            val post = hashMapOf(
                "title" to title,
                "body" to body,
                "isSensitiveContent" to isSensitiveContent,
                "postPicture" to downloadUrl
            )
        }
    }

    private fun getDownloadUrl(fileName: String?): String {
        var url = ""
        FirebaseStorage.getInstance().reference.child("profilePictures/$fileName")
            .downloadUrl.addOnSuccessListener {
                Log.i("Obtener Url", it.toString())
                url = it.toString()
            }.addOnFailureListener {
                Log.i("Obtener Url", "Fallo al obtener url")
            }
        return url
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

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            imageUri = data?.data!!
            binding.ivCreatePostImage.setImageURI(imageUri)
            Log.i("Foto", "$imageUri")
        }
    }

    private fun uploadImage(): String {
        var fileName = ""
        if(imageUri!=null){
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val lastCharacters = imageUri.toString().substring(imageUri.toString().length-10)
            fileName = formatter.format(now) + lastCharacters
            val storageReference = FirebaseStorage.getInstance().getReference("postPictures/$fileName")
            storageReference.putFile(imageUri!!).addOnSuccessListener {
                Log.i("Storage", "Se pudo subir la imagen del post")
            }.addOnFailureListener{
                Log.i("Storage", "No se pudo subir la imagen del post",)
            }
        }
        return fileName
    }

    private fun checkEmptyFields():Boolean{
        binding.txtEditPostTitleInputLayout.error = null
        binding.txtEditPostBodyInputLayout.error = null
        return if(binding.txtEditPostTitleInput.text.toString().isEmpty()){
            binding.txtEditPostTitleInputLayout.error = "Se debe colocar un titulo"
            false
        }else if(binding.txtEditPostBodyInput.text.toString().isEmpty()){
            binding.txtEditPostBodyInputLayout.error = "Se debe colocar un cuerpo"
            false
        }else{
            true
        }
    }
}