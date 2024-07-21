package com.ajeng.ta.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http:192.168.0.56:8081/TA_1/"

    // Buat instance OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val apiService: ApiService by lazy {
        createApiService()
    }

    private fun createApiService(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gunakan OkHttpClient yang telah dibuat
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
