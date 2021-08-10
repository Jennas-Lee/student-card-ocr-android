package io.github.jennas.studentcardocr.restapi

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface KakaoRetrofitService {
    @Multipart
    @POST("2/vision/text/ocr")
    fun requestOcr(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part
    ): Call<KakaoRetrofitResult>
}