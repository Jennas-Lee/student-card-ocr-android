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

        showProgress =
            ShowProgress(findViewById(R.id.progressBar), findViewById(R.id.progressMessage))

        connectSocket()

        val camera_capture_button: ImageButton = findViewById(R.id.camera_capture_button)
        camera_capture_button.setOnClickListener { takePhoto() }

        cameraExecuter = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        if (!socket.isOpen) {
            showProgress.setProgress(0, "연결 시도중")
            connectSocket()

            if (!socket.isOpen) {
                return
            }
        }

        showProgress.setProgress(10, "사진 촬영중")

        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    showProgress.setError("사진 촬영 실패")
                }

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)

                    val image: Image? = imageProxy.image

                    showProgress.setProgress(20, "사진 촬영 성공")
                    showProgress.setProgress(30, "사진 처리중")
                    val mainProcess: MainProcess = MainProcess(socket, showProgress, image)

                    if (image != null) {
                        mainProcess.start()
                    }
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

    private fun connectSocket() {
        // TODO: Need Modify Exception Logic
        try {
            // 소켓 연결 성공 시
            showProgress.setProgress(0, "연결 시도중")
            socket = Client(java.net.URI("ws://XXX.XXX.XXX.XXX"), showProgress) // URL
            socket.connect()
            Thread.sleep(500)
            socket.send("{\"process\": 0}")
        } catch (e: Exception) {
            if (e.toString() == "org.java_websocket.exceptions.WebsocketNotConnectedException") {
                // 소켓 연결 실패 시
                showProgress.setError("연결 실패")
                Log.e("WebSocketConnectionError", e.toString())
            } else {
                // 소켓 연결 오류 시
                showProgress.setError("연결 오류")
                Log.e("WebSocketConnectionError", e.toString())
            }
            socket.close()
        }
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