package com.project.DOMAINLAYER.data.remote.dto

// Expected by: BackendApiRepository
// To be defined in: com.project.DOMAINLAYER.data.remote.dto.UserBackendDto.kt


// Assume kotlinx.serialization or similar for JSON mapping
// import kotlinx.serialization.Serializable

// @Serializable
data class UserBackendDto(
    val id: String,                 // Unique identifier for the user
    val username: String?,          // Login username, might be null if not exposed
    val displayName: String,        // Name to show in the UI
    val profileImageUrl: String?,   // URL for the user's avatar
    val email: String?,             // User's email, might be null or not included for privacy
    val lastSeenTimestamp: Long?    // Optional: epoch milliseconds for when the user was last active
    // Any other fields your backend provides for a user profile
)