package io.github.jennas.studentcardocr

import android.graphics.*
import android.media.Image
import android.util.Base64
import android.util.Log
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitClient
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitErrorResult
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class MainProcess {
    var socket: Client? = null
    var showProgress: ShowProgress? = null
    var imageData: String? = null

    constructor(socket: Client, progress: ShowProgress) {
        this.socket = socket
        this.showProgress = progress
    }

    fun main(image: Image) {
        // 1
        imageProcessing(image)
        sendImageData()
    }

    private fun imageProcessing(image: Image) {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())

        buffer.get(bytes)
        val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        Log.i("BASE64 : ", Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP))

        imageData =
            Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP).toString()
    }

    private fun sendImageData() {
        showProgress!!.setProgress(40, "사진 전송 중")
        socket!!.send("{\"process\": 1, \"image\": \"$imageData\"}")
    }
}