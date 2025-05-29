package com.project.DOMAINLAYER.usecase15

// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/usecase15/AllUsersUnderTheSunMoonAndStars.kt

import com.project.DOMAINLAYER.UserDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FOR TEAM: This object acts as a shared cache for ALL user profiles that might be
 * needed by any part of the app, especially for displaying user details (names, avatars)
 * in contexts like messaging, course lists, etc.
 * Modules responsible for user management or fetching user lists should populate this.
 */
object AllUsersUnderTheSunMoonAndStars {

    private val _internalUsersList = MutableStateFlow<List<UserDataType>>(emptyList())

    /**
     * MESSAGING MODULE USE: Observes this Flow to get a list of all known users, or uses
     * `findUserById` for specific lookups.
     */
    val allUsersDataFlow: StateFlow<List<UserDataType>> = _internalUsersList.asStateFlow()

    /**
     * FOR TEAM: Call this from your module to provide a complete list of known users.
     * This is useful at app startup or when a full refresh of user data is available.
     * This will replace any existing list.
     *
     * @param users A `List` of `com.project.DOMAINLAYER.UserDataType` instances.
     *
     * Example from your module:
     *   val allKnownUsers = fetchAllUsersFromOurBackend() // Assuming this returns List<UserDataType>
     *   AllUsersUnderTheSunMoonAndStars.setAllUsers(allKnownUsers)
     */
    fun setAllUsers(users: List<UserDataType>) {
        _internalUsersList.value = users
    }

    /**
     * FOR TEAM: (Optional convenience) Call this to add a single user to the cache
     * or update their details if they already exist in the list.
     *
     * @param user A `com.project.DOMAINLAYER.UserDataType` instance.
     */
    fun addOrUpdateUser(user: UserDataType) {
        val currentList = _internalUsersList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == user.id }
        if (index != -1) {
            currentList[index] = user // Update existing
        } else {
            currentList.add(user) // Add new
        }
        _internalUsersList.value = currentList
    }

    /**
     * MESSAGING MODULE USE: Can call this to synchronously get details for a specific user ID
     * from the current list.
     */
    fun findUserById(userId: String): UserDataType? {
        return _internalUsersList.value.find { it.id == userId }
    }
}