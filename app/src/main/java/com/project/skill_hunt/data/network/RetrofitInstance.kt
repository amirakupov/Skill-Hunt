package com.project.skill_hunt.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.project.skill_hunt.data.network.ApiService
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class TokenInterceptor(
    private val tokenProvider: suspend () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val req = chain.request()
        val token = runBlocking { tokenProvider() }
        return if (token != null) {
            chain.proceed(
                req.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            )
        } else {
            chain.proceed(req)
        }
    }
}

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun create(tokenProvider: suspend () -> String?): ApiService {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenProvider))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
}
