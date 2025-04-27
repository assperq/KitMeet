package com.example.profile.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePickerAndroid(private val activity: ComponentActivity) : ImagePicker {

    private var onImagePickedCallback: ((String) -> Unit)? = null

    private val launcher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri: Uri? = result.data?.data
        if (result.resultCode == Activity.RESULT_OK && uri != null) {
            onImagePickedCallback?.invoke(uri.toString())
        }
    }

    override fun pickImage(onImagePicked: (String) -> Unit) {
        onImagePickedCallback = onImagePicked
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }
}