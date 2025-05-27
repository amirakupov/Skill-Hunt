package com.project.backend.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.project.backend.repo.IUserRepository
import com.project.backend.models.LoginResponse
import com.project.backend.models.RegisterResponse

class AuthService(private val repo: IUserRepository) {
    fun register(email: String, password: String): RegisterResponse {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        require(email.matches(emailRegex)) { "Invalid email" }
        require(password.length >= 8)     { "Password too short" }
        if (repo.exists(email)) error("User already exists")

        val hash = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        repo.add(email, hash)
        return RegisterResponse(id = email, email = email)
    }

    fun login(email: String, password: String): LoginResponse {
        if (!repo.verify(email, password)) error("Bad credentials")
        return LoginResponse(email)
    }
}
