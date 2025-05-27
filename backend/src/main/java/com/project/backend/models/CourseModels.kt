package com.project.backend.models

import kotlinx.serialization.Serializable


@Serializable
data class AddCourseRequest(
    val title: String,
    val category: String,
    val description: String,
    val skillLevel: String,            // e.g. "Beginner", "Intermediate", "Advanced"
    val locationType: String,          // "meet-up" or "online"
    val availability: String,          // freeform or structured (e.g. "Weekends")
    val contactInfo: String            // e.g. email or phone
)

@Serializable
data class CourseResponse(
    val id: Long,
    val userEmail: String,             // from JWT
    val title: String,
    val category: String,
    val description: String,
    val skillLevel: String,
    val locationType: String,
    val availability: String,
    val contactInfo: String,
    val createdAt: String              // ISO timestamp
)
