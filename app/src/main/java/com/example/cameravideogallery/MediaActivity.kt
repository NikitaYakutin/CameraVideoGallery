package com.example.cameravideogallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log

class MediaActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private var mediaPlayer: MediaPlayer? = null
    private val REQUEST_VIDEO_PICK = 1
    private val REQUEST_AUDIO_PICK = 2
    private val REQUEST_PERMISSION = 100
    private val REQUEST_STORAGE_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        videoView = findViewById(R.id.videoView)
        val playVideoButton = findViewById<Button>(R.id.button_play_video)
        val playAudioButton = findViewById<Button>(R.id.button_play_audio)

        // Проверка разрешений на доступ к хранилищу
        checkStoragePermission(playVideoButton, playAudioButton)
    }

    private fun checkStoragePermission(playVideoButton: Button, playAudioButton: Button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для Android 11 и выше проверяем MANAGE_EXTERNAL_STORAGE
            if (Environment.isExternalStorageManager()) {
                setupMediaButtons(playVideoButton, playAudioButton)
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, REQUEST_STORAGE_PERMISSION)
            }
        } else {
            // Для Android ниже 11 проверяем READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            } else {
                setupMediaButtons(playVideoButton, playAudioButton)
            }
        }
    }

    private fun setupMediaButtons(playVideoButton: Button, playAudioButton: Button) {
        // Обработка кнопки для видео
        playVideoButton.setOnClickListener {
            val pickVideoIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            try {
                startActivityForResult(pickVideoIntent, REQUEST_VIDEO_PICK)
            } catch (e: Exception) {
                Log.e("MediaActivity", "Ошибка при попытке выбрать видео: ${e.message}")
                Toast.makeText(this, "Ошибка при выборе видео", Toast.LENGTH_SHORT).show()
            }
        }

        // Обработка кнопки для аудио
        playAudioButton.setOnClickListener {
            val pickAudioIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "audio/*"
            }
            try {
                startActivityForResult(pickAudioIntent, REQUEST_AUDIO_PICK)
            } catch (e: Exception) {
                Log.e("MediaActivity", "Ошибка при попытке выбрать аудио: ${e.message}")
                Toast.makeText(this, "Ошибка при выборе аудио", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setupMediaButtons(findViewById(R.id.button_play_video), findViewById(R.id.button_play_audio))
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VIDEO_PICK -> {
                if (resultCode == RESULT_OK) {
                    val selectedVideoUri: Uri? = data?.data
                    if (selectedVideoUri != null) {
                        videoView.setVideoURI(selectedVideoUri)
                        videoView.start()
                    } else {
                        Toast.makeText(this, "Ошибка: URI видео не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ошибка при выборе видео", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_AUDIO_PICK -> {
                if (resultCode == RESULT_OK) {
                    val selectedAudioUri: Uri? = data?.data
                    if (selectedAudioUri != null) {
                        playAudio(selectedAudioUri)
                    } else {
                        Toast.makeText(this, "Ошибка: URI аудио не найден", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Ошибка при выборе аудио", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_STORAGE_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                    setupMediaButtons(findViewById(R.id.button_play_video), findViewById(R.id.button_play_audio))
                } else {
                    Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAudio(audioUri: Uri) {
        mediaPlayer?.release()  // Освобождаем предыдущий MediaPlayer
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@MediaActivity, audioUri)
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
