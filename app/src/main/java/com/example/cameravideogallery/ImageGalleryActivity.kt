package com.example.cameravideogallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImageGalleryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_PICK = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_gallery)
        val BackButton = findViewById<Button>(R.id.goback)
        imageView = findViewById(R.id.imageViewGallery)
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK)

        BackButton.setOnClickListener {
            finish() // Завершает текущую активность и возвращается к главной
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            imageView.setImageURI(selectedImage)
        }
    }


}
