package com.project.backend.service

import com.project.backend.models.AddCourseRequest
import com.project.backend.models.CourseResponse
import com.project.backend.repo.ICourseRepository
import io.ktor.server.auth.jwt.JWTPrincipal

class CourseService(private val repo: ICourseRepository) {
    fun addCourse(req: AddCourseRequest, principal: JWTPrincipal): CourseResponse {
        val email = principal.payload.getClaim("email").asString()
        return repo.addCourse(req, email)
    }
    fun getCourses(principal: JWTPrincipal): List<CourseResponse> {
        val email = principal.payload.getClaim("email").asString()
        return repo.getCoursesByUser(email)
    }
    fun getAllCourses(): List<CourseResponse> {
        return repo.getAllCourses()
    }
    fun getCourseById(id: Long): CourseResponse? {
        return repo.getCourseById(id)
    }
}
