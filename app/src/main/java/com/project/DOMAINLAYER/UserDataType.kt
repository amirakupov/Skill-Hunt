// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/UserDataType.kt
package com.project.DOMAINLAYER // Note: Package is just 'com.project.DOMAINLAYER'

/**
 * A common data type representing a user across different use cases and modules.
 * This defines the standard structure for user information within the domain layer.
 */
data class UserDataType(
    val id: String,
    val displayName: String,
    val profileImageUrl: String? = null
    // Add other common user fields that might be relevant across different use cases.
)