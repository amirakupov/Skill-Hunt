package com.project.DOMAINLAYER.usecase15

// File: Skill-Hunt/app/src/main/java/com/project/DOMAINLAYER/usecase15/CurrentLoggedInUser.kt

import com.project.DOMAINLAYER.UserDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * FOR TEAM: This object acts as a shared 'letterbox' for the currently logged-in user's data.
 * The authentication module should update this whenever the user's login status changes.
 * The messaging UI will observe `dataFlow` to react to these updates.
 */
object CurrentLoggedInUser {
    private val _userDataSource = MutableStateFlow<UserDataType?>(null)

    /**
     * MESSAGING MODULE USE: Observes this Flow to get the current UserDataType or null.
     */
    val dataFlow: StateFlow<UserDataType?> = _userDataSource.asStateFlow()

    /**
     * FOR TEAM: Call this method from the authentication module to provide the
     * details of the user who has just logged in, or to clear it on logout.
     *
     * @param newUserData An instance of `com.project.DOMAINLAYER.UserDataType` representing the
     *                    logged-in user, or `null` if the user has logged out.
     *
     * Example from module:
     *   // After successful login:
     *   val userDetails = UserDataType(id="user123", displayName="Alice Wonderland", profileImageUrl="url...")
     *   CurrentLoggedInUser.update(userDetails)
     *
     *   // After logout:
     *   CurrentLoggedInUser.update(null)
     */
    fun update(newUserData: UserDataType?) {
        _userDataSource.value = newUserData
    }
}