package com.example.usdelacamara

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.usdelacamara.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.icu.util.Calendar
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_CODE
            )
        }

        binding.buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).also {
                intent.resolveActivity(packageManager)?.also {
                    createVideoFile()
                    val videoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.usdelacamara",
                        file
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)

                }
            }
            startForResult.launch(intent)
        }
    }

    lateinit var file: File

    private fun getCurrentDateTime(): Any {
        return Calendar.getInstance().time
    }

    fun createVideoFile() {
        val dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val sdfDate = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        val data = sdfDate.format(Date())
        file = File.createTempFile("Video_${data}_", ".mp4", dir)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val videoUri: Uri? = result.data?.data
            if (videoUri != null) {
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.start()
            } else {
                binding.videoView.setVideoURI(Uri.fromFile(file))  // Mostrar el archivo directamente si no hay Uri de datos
                binding.videoView.start()
            }
        }
    }
}