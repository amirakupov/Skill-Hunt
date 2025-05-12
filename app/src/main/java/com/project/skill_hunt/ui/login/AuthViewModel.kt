package com.project.skill_hunt.ui.login
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var authToken by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            authToken = repo.currentToken()
        }
    }

    fun register(email: String, pass: String) = viewModelScope.launch {
        try {
            repo.register(email, pass)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun login(email: String, pass: String) = viewModelScope.launch {
        try {
            val resp = repo.login(email, pass)
            authToken = resp.token
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun fetchProtected(onResult: (String) -> Unit) = viewModelScope.launch {
        try {
            onResult(repo.getProtectedMessage())
        } catch (e: Exception) {
            onResult("Error: ${e.message}")
        }
    }

    fun logout(onDone: () -> Unit) = viewModelScope.launch {
        repo.logout()
        authToken = null
        onDone()
    }
}
