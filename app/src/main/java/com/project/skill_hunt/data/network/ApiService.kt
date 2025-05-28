package com.project.skill_hunt // Or your ApiService package

// Auth
import com.project.skill_hunt.data.model.RegisterRequest
import com.project.skill_hunt.data.model.RegisterResponse
import com.project.skill_hunt.data.model.LoginRequest
import com.project.skill_hunt.data.model.LoginResponseWithToken
// Course
import com.project.skill_hunt.data.model.CourseCreateRequest
import com.project.skill_hunt.data.model.CourseResponse
// Messaging
import com.project.skill_hunt.data.model.ConversationSnippet
import com.project.skill_hunt.data.model.SendMessageRequest
import com.project.skill_hunt.data.model.Message // IMPORTANT: This is your domain/network model

import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    // --- Auth Endpoints ---
    @POST("api/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseWithToken

    @GET("api/protected") // Example protected route
    suspend fun getProtected(): ResponseBody

    // --- Course/Listing Endpoints ---
    @POST("api/courses")
    suspend fun createCourse(@Body courseData: CourseCreateRequest): CourseResponse

    @GET("api/courses")
    suspend fun getAllCourses(): List<CourseResponse>

    @GET("api/courses/{id}")
    suspend fun getCourseDetails(@Path("id") courseId: String): CourseResponse

    // --- Messaging Endpoints ---
    @GET("api/messaging/conversations")
    suspend fun getConversationSnippets(): List<ConversationSnippet> // For conversation list

    @GET("api/messaging/conversations/{conversationId}/messages")
    suspend fun getMessagesForConversation(@Path("conversationId") conversationId: String): List<Message> // Uses your Message model

    @POST("api/messaging/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Message // Uses your Message model

    // Optional: An endpoint to mark messages as read could be useful
    // @POST("api/messaging/conversations/{conversationId}/read")
    // suspend fun markConversationAsRead(@Path("conversationId") conversationId: String): ResponseBody
}