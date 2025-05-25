// In com.project.skill_hunt.data.network.fake.FakeApiService.kt
package com.project.skill_hunt.data.network.fake

import androidx.compose.ui.semantics.error
import com.project.skill_hunt.ApiService // The REAL interface
import com.project.skill_hunt.CourseResponse
// ... other imports for models, delay, ResponseBody ...
import kotlinx.coroutines.delay
import okhttp3.ResponseBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeApiService : ApiService {
    private val dummyCourses = mutableListOf(
        CourseResponse(id = "1", title = "Kotlin for Beginners (Fake)", description = "Learn the basics of Kotlin programming.", location = "Online", mentorId = "mentorA"),
        CourseResponse(id = "2", title = "Advanced Jetpack Compose (Fake)", description = "Deep dive into Compose UI.", location = "Online", mentorId = "mentorB"),
        // ... more fake courses
    )
    // ... Implement ALL methods from the ApiService interface, returning fake/hardcoded data
    // For example:
    override suspend fun getAllCourses(): List<CourseResponse> {
        delay(500) // Simulate delay
        return dummyCourses.toList()
    }

    override suspend fun getCourseDetails(courseId: String): CourseResponse {
        delay(300)
        return dummyCourses.find { it.id == courseId }
            ?: throw HttpException(Response.error<CourseResponse>(404, "{\"error\":\"Fake course not found\"}".toResponseBody("application/json".toMediaTypeOrNull())))
    }

    // Implement fake versions of login, register, createCourse etc. if your UI flow depends on them.
    // ... (refer to previous examples for fake auth methods) ...
}