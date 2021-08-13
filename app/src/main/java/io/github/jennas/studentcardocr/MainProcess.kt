package io.github.jennas.studentcardocr

import android.graphics.*
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class MainProcess : Thread {
    var socket: Client? = null
    var showProgress: ShowProgress? = null
    var image: Image? = null
    var imageData: String? = null

    constructor(socket: Client, progress: ShowProgress, image: Image?) {
        this.socket = socket
        this.showProgress = progress
        this.image = image
    }

    override fun run() {
        imageProcessing()
        sendImageData()
    }

    private fun imageProcessing() {
        val buffer: ByteBuffer = this.image!!.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())

        buffer.get(bytes)
        val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        imageData =
            Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP).toString()
    }

    private fun sendImageData() {
        showProgress!!.setProgress(40, "사진 전송 중")
        socket!!.send("{\"process\": 1, \"image\": \"$imageData\"}")
    }
}