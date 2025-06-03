
package com.project.skill_hunt

import com.project.skill_hunt.data.model.AddCourseRequest
import com.project.skill_hunt.data.model.CourseResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    @POST("api/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseWithToken

    @GET("api/protected")
    suspend fun getProtected(): ResponseBody

    @POST("api/courses")
    suspend fun addCourse(@Body req: AddCourseRequest): CourseResponse

    @GET("api/courses")
    suspend fun getCourses(): List<CourseResponse>

    @GET("api/courses/all")
    suspend fun getAllCourses(): List<CourseResponse>
}
