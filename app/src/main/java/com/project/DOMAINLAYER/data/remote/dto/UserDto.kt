package com.project.DOMAINLAYER.data.remote.dto

// Assuming kotlinx.serialization for JSON parsing, add if not present
// import kotlinx.serialization.Serializable

// @Serializable // If using kotlinx.serialization
data class UserDto(
    val id: String, // Or Int, match backend
    val username: String?, // Or other fields like email
    val displayName: String?, // Or firstName, lastName
    val profileImageUrl: String?
    // Add any other relevant fields from your backend's user response
)