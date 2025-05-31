package com.project.DOMAINLAYER.data.local.entity

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String?,
    val profileImageUrl: String?,
    // other fields from UserDataType
    val lastRefreshed: Long = System.currentTimeMillis() // For cache invalidation
)

// Transformation function
fun UserEntity.toUserDataType(): UserDataType {
    return UserDataType(id, displayName ?: "Unknown", profileImageUrl /*...*/)
}

fun UserDto.toUserEntity(): UserEntity {
    return UserEntity(id, displayName, profileImageUrl /*...*/)
}