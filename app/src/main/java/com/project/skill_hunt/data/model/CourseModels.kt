package com.project.skill_hunt.data.model // Or just com.project.skill_hunt if you prefer

import kotlinx.serialization.Serializable

@Serializable
data class CourseCreateRequest(
    val title: String,
    val description: String,
    val location: String = "Online"
    // ... any other fields
)

@Serializable
data class CourseResponse(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val mentorId: String
    // ... any other fields
)