package com.project.skill_hunt.ui


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val repo: CourseRepository,
    private val courseId: Long
) : ViewModel() {
    var course by mutableStateOf<CourseResponse?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadCourse()
    }

    private fun loadCourse() = viewModelScope.launch {
        try {
            val resp = repo.getCourseById(courseId)
            course = resp
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}
