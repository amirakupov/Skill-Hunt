package com.project.skill_hunt.data.repository

import com.project.skill_hunt.ApiService
import com.project.skill_hunt.LoginRequest
import com.project.skill_hunt.LoginResponseWithToken
import com.project.skill_hunt.RegisterRequest
import com.project.skill_hunt.data.TokenPreferences


class AuthRepository(
    private val api: ApiService,
    private val prefs: TokenPreferences
) {
    suspend fun register(email: String, pass: String) =
        api.register(RegisterRequest(email, pass))

    suspend fun login(email: String, pass: String): LoginResponseWithToken {
        val resp = api.login(LoginRequest(email, pass))
        prefs.saveToken(resp.token)
        return resp
    }

    suspend fun getProtectedMessage(): String =
        api.getProtected().string()

    suspend fun logout() {
        prefs.clearToken()
    }

    suspend fun currentToken(): String? =
        prefs.getToken()
}
