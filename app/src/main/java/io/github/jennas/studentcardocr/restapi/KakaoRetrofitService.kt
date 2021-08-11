package io.github.jennas.studentcardocr.restapi

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface KakaoRetrofitService {
    @Headers("Content-Type: multipart/form-data")
//    @Multipart
//    @POST("v2/vision/text/ocr")
    @GET("/api")
    fun requestOcr(
        @Header("Authorization") authorization: String,
        @Query("image") image: String
//        @Body image: RequestBody
    ): Call<KakaoRetrofitResult>
}