package com.project.skill_hunt.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val email: String, val password: String)

@Serializable
data class RegisterResponse(val id: String, val email: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponseWithToken(val email: String, val token: String)

