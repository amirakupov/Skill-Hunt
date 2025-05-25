package com.project.skill_hunt.domain.usecase.course // Adjust package as needed

import com.project.skill_hunt.CourseCreateRequest
import com.project.skill_hunt.CourseResponse
import com.project.skill_hunt.data.repository.CourseRepository

class SubmitCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseData: CourseCreateRequest): Result<CourseResponse> {
        // Add any business logic here if needed before submitting
        // e.g., validation (though server-side validation is also crucial)
        return try {
            val createdCourse = courseRepository.createCourse(courseData)
            Result.success(createdCourse)
        } catch (e: Exception) {
            // Log error, map to domain-specific error, etc.
            Result.failure(e)
        }
    }
}