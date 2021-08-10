package io.github.jennas.studentcardocr.restapi

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoRetrofitClient {
//    private var instance: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()
    private const val BASE_URL = "https://dapi.kakao.com/"
    var api: KakaoRetrofitService

//    fun getInstance(): Retrofit {
//        if (instance == null) {
//            instance = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//        }
//
//        return instance!!.create()
//    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create(KakaoRetrofitService::class.java)
    }
}