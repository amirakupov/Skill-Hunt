package com.project.skill_hunt.data.repository

import com.project.skill_hunt.ApiService
import com.project.skill_hunt.data.model.AddCourseRequest
import com.project.skill_hunt.data.model.CourseResponse

class CourseRepository(private val api: ApiService) {
    suspend fun addCourse(req: AddCourseRequest): CourseResponse =
        api.addCourse(req)
    suspend fun getCourses(): List<CourseResponse> =
        api.getCourses()
    suspend fun getAllCourses(): List<CourseResponse> =
        api.getAllCourses()
    suspend fun getCourseById(id: Long): CourseResponse =
        api.getCourseById(id)
}