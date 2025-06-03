package com.project.backend.models

import kotlinx.serialization.Serializable


@Serializable
data class AddCourseRequest(
    val title: String,
    val category: String,
    val description: String,
    val skillLevel: String,
    val locationType: String,
    val availability: String,
    val contactInfo: String
)

@Serializable
data class CourseResponse(
    val id: Long,
    val userEmail: String,
    val title: String,
    val category: String,
    val description: String,
    val skillLevel: String,
    val locationType: String,
    val availability: String,
    val contactInfo: String,
    val createdAt: String
)
