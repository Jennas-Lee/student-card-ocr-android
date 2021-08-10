package io.github.jennas.studentcardocr

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecuter: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        val camera_capture_button: ImageButton = findViewById(R.id.camera_capture_button)
        camera_capture_button.setOnClickListener { takePhoto() }

        outputDirectory = getOutputDirectory()

        cameraExecuter = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)

                    val image: Image? = imageProxy.image

                    val mainProcess: MainProcess = MainProcess()

                    var imageBytes: MultipartBody.Part? = null
                    if (image != null) {
                        imageBytes = mainProcess.imageToBytes(image)
                    }

                    if (imageBytes != null) {
                        mainProcess.callKakaoAPI(imageBytes)
                    }

//                    val planes: Array<out Image.Plane>? = image?.planes
//                    val yBuffer: ByteBuffer = planes!![0].buffer
//                    val uBuffer: ByteBuffer = planes!![1].buffer
//                    val vBuffer: ByteBuffer = planes!![2].buffer
//
//                    val ySize: Int = yBuffer.remaining()
//                    val uSize: Int = uBuffer.remaining()
//                    val vSize: Int = vBuffer.remaining()
//
//                    val nv21: ByteArray = byteArrayOf((ySize + uSize + vSize).toByte())
//
//                    yBuffer.get(nv21, 0, ySize)
//                    vBuffer.get(nv21, ySize, vSize)
//                    vBuffer.get(nv21, ySize + vSize, uSize)
//
//                    val yuvImage: YuvImage =
//                        YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
//                    val out: ByteArrayOutputStream = ByteArrayOutputStream()
//                    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
//
//                    val imageBytes: ByteArray = out.toByteArray()
//                    // end
//
//
//                    val buffer: ByteBuffer = image.planes[0].buffer
//                    val bytes: ByteArray = byteArrayOf(buffer.capacity().toByte())
//                    val byte = buffer.capacity().toByte()
//                    val stream: ByteArrayOutputStream = ByteArrayOutputStream()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val viewFinder: PreviewView = findViewById(R.id.viewFinder)

            // Preview
            val preview = Preview.Builder()
                .setTargetResolution(Size(viewFinder.width, viewFinder.height))
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))

        imageCapture = ImageCapture.Builder().build()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecuter.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val view: PreviewView = findViewById(R.id.viewFinder)
        val params = view.layoutParams

        params.apply {
            width = view.width
            height = (view.width / 1.58).toInt()
        }
        view.apply {
            layoutParams = params
        }
    }
}