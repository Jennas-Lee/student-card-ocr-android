package io.github.jennas.studentcardocr

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecuter: ExecutorService
    private lateinit var showProgress: ShowProgress
    private lateinit var socket: Client

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

        showProgress = ShowProgress(findViewById(R.id.progressBar), findViewById(R.id.progressMessage))
        showProgress.startProgress()

        try {   // 소켓 연결 성공 시
            socket = Client(java.net.URI("ws://192.168.137.1:5000"), showProgress) // URL
            socket.connect()

            showProgress.setProgress(0, "연결 완료")
        } catch (e: Exception) {    // 소켓 연결 실패 시
            showProgress.setError("연결 실패")
        }

        val camera_capture_button: ImageButton = findViewById(R.id.camera_capture_button)
        camera_capture_button.setOnClickListener { takePhoto() }

        cameraExecuter = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        showProgress.setProgress(10, "사진 촬영중")

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    showProgress.setError("사진 촬영 실패")
                }

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {super.onCaptureSuccess(imageProxy)

                    val image: Image? = imageProxy.image

                    showProgress.setProgress(20, "사진 촬영 성공")
                    showProgress.setProgress(30, "사진 처리중")
                    val mainProcess: MainProcess = MainProcess(socket, showProgress)

                    if (image != null) {
                        mainProcess.main(image)
                    }

//                    showProgress.setProgress(30, "사진 전송중")
                }
            }
        )

        showProgress.setProgress(20, "연결중")

//        socket.connect()

        socket.send("ping!")

//        socket.close()

//        showProgress.setProgress(100, "웹소켓 테스트 로직 종료")
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecuter.shutdown()
        socket.close()
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