package com.project.skill_hunt.data.model

import kotlinx.serialization.Serializable

// ... your existing Auth models ...

@Serializable
data class CourseCreateRequest(
    val title: String,
    val description: String,
    val location: String = "Online", // Default or user-defined
    // Add other fields: availability details, category, price, etc.
    // Example: val category: String,
    // Example: val price: Double? = null
)

@Serializable
data class CourseResponse(
    // Response after creating or fetching a course
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val mentorId: String, // To know who created it
    // ... other fields from CourseCreateRequest ...
    // Example: val category: String,
    // Example: val price: Double? = null
    // Example: val mentorName: String (if backend provides it conveniently)
)