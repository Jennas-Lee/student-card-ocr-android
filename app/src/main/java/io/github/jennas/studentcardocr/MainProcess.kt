package io.github.jennas.studentcardocr

import android.graphics.*
import android.media.Image
import android.util.Log
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitClient
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitResult
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class MainProcess {

    fun imageToBytes(image: Image): MultipartBody.Part {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes: ByteArray = ByteArray(buffer.capacity())
        buffer.get(bytes)
        val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        val requestBody: RequestBody =
            RequestBody.create(MediaType.parse("image/jpg"), stream.toByteArray())

        return MultipartBody.Part.createFormData("image", "IMAGE.jpeg", requestBody)
    }

    fun callKakaoAPI(imageBytes: MultipartBody.Part): String? {
        val authorization: String = "KAKAO_AK " + BuildConfig.X_Kakao_Restapi_Key
        var data: String? = null


        KakaoRetrofitClient.api.requestOcr(
            authorization, imageBytes
        ).enqueue(object : Callback<KakaoRetrofitResult> {
            override fun onResponse(
                call: Call<KakaoRetrofitResult>,
                response: Response<KakaoRetrofitResult>
            ) {
                data = response.body()!!.result.toString()
                Log.i("response", data.toString())
            }

            override fun onFailure(call: Call<KakaoRetrofitResult>, t: Throwable) {
                Log.e("KAKAO OCR API ERROR! ", t.message.toString())
            }
        })

        return data
    }
}