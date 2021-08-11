package io.github.jennas.studentcardocr

import android.graphics.*
import android.media.Image
import android.util.Base64
import android.util.Log
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitClient
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitErrorResult
import io.github.jennas.studentcardocr.restapi.KakaoRetrofitResult
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class MainProcess {

    fun imageToBytes(image: Image): String {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes: ByteArray = ByteArray(buffer.capacity())

        buffer.get(bytes)
        val bitmapImage: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)

        val stream: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        Log.i("BASE64 : ", Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT))
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)

//        return MultipartBody.Part.createFormData(
//            "image",
//            "image",
//            RequestBody.create(MediaType.parse("multipart/form-data"), stream.toByteArray())
//        )
    }

    fun callKakaoAPI(imageBytes: String): String? {
        val authorization: String = "KakaoAK " + BuildConfig.X_Kakao_Restapi_Key
        var data: String? = null


        KakaoRetrofitClient.api.requestOcr(
            authorization, imageBytes
        ).enqueue(object : Callback<KakaoRetrofitResult> {
            override fun onResponse(
                call: Call<KakaoRetrofitResult>,
                response: Response<KakaoRetrofitResult>
            ) {
                data = response.body().toString()
                val error =
                    KakaoRetrofitClient.retrofit.responseBodyConverter<KakaoRetrofitErrorResult>(
                        KakaoRetrofitErrorResult::class.java,
                        KakaoRetrofitErrorResult::class.java.annotations
                    ).convert(response.errorBody())

                Log.i("response code", response.code().toString())
                Log.i("error response", error?.msg!!)
                Log.i("response", data!!)
                Log.i("key", authorization)
            }

            override fun onFailure(call: Call<KakaoRetrofitResult>, t: Throwable) {
                Log.e("KAKAO OCR API ERROR! ", t.message.toString())
            }
        })

        return data
    }
}