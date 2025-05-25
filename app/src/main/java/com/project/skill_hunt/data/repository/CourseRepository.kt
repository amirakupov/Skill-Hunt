package com.project.skill_hunt.data.repository

import com.project.skill_hunt.ApiService
import com.project.skill_hunt.CourseCreateRequest
import com.project.skill_hunt.CourseResponse

class CourseRepository(private val api: ApiService) {

    suspend fun createCourse(courseData: CourseCreateRequest): CourseResponse {
        return api.createCourse(courseData)
        // No local saving/caching for now, directly relying on API
    }

    suspend fun getAllCourses(): List<CourseResponse> {
        return api.getAllCourses()
    }

    suspend fun getCourseDetails(courseId: String): CourseResponse {
        return api.getCourseDetails(courseId)
    }
}