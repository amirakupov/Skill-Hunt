package com.project.skill_hunt.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.data.model.AddCourseRequest
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repo: CourseRepository
) : ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
    var successResponse by mutableStateOf<CourseResponse?>(null)

    fun addCourse(req: AddCourseRequest) = viewModelScope.launch {
        try {
            val resp = repo.addCourse(req)
            successResponse = resp
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }
}