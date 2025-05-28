package com.project.skill_hunt.data.network.fake

// Remove unused Compose imports if not needed here:
// import androidx.compose.ui.semantics.error // Likely unused
// import androidx.compose.ui.semantics.password // Likely unused

import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.password
import com.project.skill_hunt.ApiService // The REAL interface from your main project structure

// Import your data models
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.data.model.CourseCreateRequest // Assuming you'll need this for createCourse
import com.project.skill_hunt.data.model.RegisterRequest
import com.project.skill_hunt.data.model.RegisterResponse
import com.project.skill_hunt.data.model.LoginRequest
import com.project.skill_hunt.data.model.LoginResponseWithToken

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class FakeApiService : ApiService { // Implements the original ApiService interface

    private var nextCourseId = 3 // To generate unique IDs for new fake courses

    private val dummyCourses = mutableListOf(
        CourseResponse(id = "1", title = "Kotlin for Beginners (Fake)", description = "Learn the basics of Kotlin programming.", location = "Online", mentorId = "mentorA"),
        CourseResponse(id = "2", title = "Advanced Jetpack Compose (Fake)", description = "Deep dive into Compose UI.", location = "Online", mentorId = "mentorB")
        // Add more fake courses if desired
    )

    // --- Auth Endpoints ---
    override suspend fun register(req: RegisterRequest): RegisterResponse {
        delay(300) // Simulate network delay
        // Simulate successful registration
        return RegisterResponse(id = "fake_user_${req.email.hashCode()}", email = req.email)
    }

    override suspend fun login(req: LoginRequest): LoginResponseWithToken {
        delay(300) // Simulate network delay
        // Simulate a successful login for a test user
        if (req.email == "test@example.com" && req.password == "password") {
            return LoginResponseWithToken(token = "fake-jwt-token-${System.currentTimeMillis()}", email = req.email)
        }
        // Simulate an error for incorrect login
        throw HttpException(
            Response.error<LoginResponseWithToken>(
                401, // Unauthorized
                "{\"error\":\"Invalid credentials\"}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )
    }

    override suspend fun getProtected(): ResponseBody {
        delay(100)
        return "Access granted to fake protected data. Welcome!".toResponseBody("text/plain".toMediaTypeOrNull())
    }

    // --- Course/Listing Endpoints ---
    override suspend fun createCourse(courseData: CourseCreateRequest): CourseResponse {
        delay(500) // Simulate network delay
        val newCourse = CourseResponse(
            id = "course_${nextCourseId++}",
            title = courseData.title,
            description = courseData.description,
            location = courseData.location,
            mentorId = "fake_mentor_id_for_new_course" // Or derive from a fake logged-in user
        )
        dummyCourses.add(newCourse)
        return newCourse
    }

    override suspend fun getAllCourses(): List<CourseResponse> {
        delay(800) // Simulate network delay
        return dummyCourses.toList() // Return a copy to prevent external modification
    }

    override suspend fun getCourseDetails(courseId: String): CourseResponse {
        delay(400) // Simulate network delay
        return dummyCourses.find { it.id == courseId }
            ?: throw HttpException(
                Response.error<CourseResponse>(
                    404, // Not Found
                    "{\"error\":\"Fake course with id '$courseId' not found.\"}".toResponseBody("application/json".toMediaTypeOrNull())
                )
            )
    }
}