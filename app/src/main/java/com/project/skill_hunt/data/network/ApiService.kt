package com.project.skill_hunt

import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {
    // --- Existing Auth Endpoints ---
    @POST("api/register")
    suspend fun register(@Body req: RegisterRequest): RegisterResponse

    @POST("api/login")
    suspend fun login(@Body req: LoginRequest): LoginResponseWithToken

    @GET("api/protected")
    suspend fun getProtected(): ResponseBody

    // --- New Course/Listing Endpoints ---
    @POST("api/courses") // Use @Body for the request object
    suspend fun createCourse(@Body courseData: CourseCreateRequest): CourseResponse

    @GET("api/courses")
    suspend fun getAllCourses(): List<CourseResponse> // Returns a list of all courses

    @GET("api/courses/{id}")
    suspend fun getCourseDetails(@Path("id") courseId: String): CourseResponse // Returns details for one course


        // --- Messaging Endpoints ---

        @GET("api/messaging/conversations")
        suspend fun getConversationSnippets(): List<ConversationSnippet>

        @GET("api/messaging/conversations/{conversationId}/messages")
        suspend fun getMessagesForConversation(@Path("conversationId") conversationId: String): List<androidx.datastore.core.Message>

        // To start a new conversation or send a message (backend can handle creating conversation if needed)
        @POST("api/messaging/messages")
        suspend fun sendMessage(@Body request: SendMessageRequest): androidx.datastore.core.Message // Backend returns the sent message

        // Optional: An endpoint to mark messages as read could be useful
        // @POST("api/messaging/conversations/{conversationId}/read")
        // suspend fun markConversationAsRead(@Path("conversationId") conversationId: String): ResponseBody
}