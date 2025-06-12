package com.project.skill_hunt.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.data.repository.CourseRepository
import kotlinx.coroutines.launch

class BrowseCoursesViewModel(
    private val repo: CourseRepository
) : ViewModel() {
    var courses by mutableStateOf<List<CourseResponse>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadAllCourses()
    }

    fun loadAllCourses() = viewModelScope.launch {
        try {
            courses = repo.getAllCourses()
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}
