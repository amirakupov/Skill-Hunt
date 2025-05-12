
package com.project.skill_hunt

import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    @POST("api/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseWithToken

    @GET("api/protected")
    suspend fun getProtected(): ResponseBody  // returns plain-text greeting
}
