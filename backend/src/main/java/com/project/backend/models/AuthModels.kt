package com.project.backend.models

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlinx.serialization.Serializable

@Serializable data class RegisterRequest(val email: String, val password: String)
@Serializable data class RegisterResponse(val id: String, val email: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class LoginResponse(val email: String)


object UserRepo {
    private val users = mutableMapOf<String, String>()
    fun add(email: String, hash: String) { users[email] = hash }
    fun exists(email: String) = users.containsKey(email)
    fun verify(email: String, plain: String): Boolean =
        users[email]?.let { hash ->
            BCrypt.verifyer().verify(plain.toCharArray(), hash).verified
        } ?: false
}
