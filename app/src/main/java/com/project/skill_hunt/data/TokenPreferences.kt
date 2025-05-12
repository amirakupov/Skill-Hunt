package com.project.skill_hunt.data

import android.content.Context

class TokenPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    companion object { private const val KEY_TOKEN = "jwt_token" }

    suspend fun saveToken(token: String) =
        prefs.edit().putString(KEY_TOKEN, token).apply()

    suspend fun getToken(): String? =
        prefs.getString(KEY_TOKEN, null)

    suspend fun clearToken() =
        prefs.edit().remove(KEY_TOKEN).apply()
}
