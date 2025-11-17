package com.example.lab_week_11_b

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_EXTERNAL_STORAGE = 3
    }

    private lateinit var providerFileManager: ProviderFileManager

    private var photoInfo: FileInfo? = null
    private var videoInfo: FileInfo? = null

    private var isCapturingVideo = false

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var takeVideoLauncher: ActivityResultLauncher<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        providerFileManager = ProviderFileManager(
            applicationContext,
            FileHelper(applicationContext),
            contentResolver,
            Executors.newSingleThreadExecutor(),
            MediaContentHelper()
        )

        // ===========================================
        // Register launcher FOTO
        // ===========================================
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    providerFileManager.insertImageToStore(photoInfo)
                }
            }

        // ===========================================
        // Register launcher VIDEO
        // ===========================================
        takeVideoLauncher =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
                if (success) {
                    providerFileManager.insertVideoToStore(videoInfo)
                }
            }

        // Tombol FOTO
        findViewById<Button>(R.id.photo_button).setOnClickListener {
            isCapturingVideo = false
            checkStoragePermission { openImageCapture() }
        }

        // Tombol VIDEO
        findViewById<Button>(R.id.video_button).setOnClickListener {
            isCapturingVideo = true
            checkStoragePermission { openVideoCapture() }
        }
    }

    // ===========================================
    // BUKA KAMERA FOTO
    // ===========================================
    private fun openImageCapture() {
        photoInfo =
            providerFileManager.generatePhotoUri(System.currentTimeMillis())

        val uri = photoInfo?.uri
        if (uri != null) {
            takePictureLauncher.launch(uri)
        }
    }

    // ===========================================
    // BUKA KAMERA VIDEO
    // ===========================================
    private fun openVideoCapture() {
        videoInfo =
            providerFileManager.generateVideoUri(System.currentTimeMillis())

        val uri = videoInfo?.uri
        if (uri != null) {
            takeVideoLauncher.launch(uri)
        }
    }

    // ===========================================
    // Permission Android 9 kebawah
    // ===========================================
    private fun checkStoragePermission(onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        } else {
            onPermissionGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_EXTERNAL_STORAGE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (isCapturingVideo) openVideoCapture()
            else openImageCapture()
        }
    }
}
