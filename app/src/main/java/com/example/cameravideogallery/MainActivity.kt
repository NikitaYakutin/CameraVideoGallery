package com.example.cameravideogallery

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraButton = findViewById<Button>(R.id.button_camera)
        val mediaButton = findViewById<Button>(R.id.button_media)
        val galleryButton = findViewById<Button>(R.id.button_gallery)

        cameraButton.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        mediaButton.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        galleryButton.setOnClickListener {
            startActivity(Intent(this, ImageGalleryActivity::class.java))
        }
    }
}
