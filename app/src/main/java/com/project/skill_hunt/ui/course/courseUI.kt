package com.project.skill_hunt.ui.createcourse // New UI package

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.skill_hunt.CourseCreateRequest
import com.project.skill_hunt.domain.usecase.course.SubmitCourseUseCase
import kotlinx.coroutines.launch

sealed class CreateCourseUiState {
    object Idle : CreateCourseUiState()
    object Loading : CreateCourseUiState()
    data class Success(val courseId: String) : CreateCourseUiState()
    data class Error(val message: String) : CreateCourseUiState()
}

class CreateCourseViewModel(
    private val submitCourseUseCase: SubmitCourseUseCase
) : ViewModel() {

    var uiState by mutableStateOf<CreateCourseUiState>(CreateCourseUiState.Idle)
        private set

    // Mutable states for form fields
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    // Add other fields as needed (location, category, etc.)

    fun createCourse() {
        viewModelScope.launch {
            uiState = CreateCourseUiState.Loading
            val courseData = CourseCreateRequest(
                title = title.trim(),
                description = description.trim()
                // ... map other form fields ...
            )
            // Basic client-side validation (can be more sophisticated)
            if (title.isBlank() || description.isBlank()) {
                uiState = CreateCourseUiState.Error("Title and Description cannot be empty.")
                return@launch
            }

            submitCourseUseCase(courseData)
                .onSuccess { createdCourse ->
                    uiState = CreateCourseUiState.Success(createdCourse.id)
                }
                .onFailure { exception ->
                    uiState = CreateCourseUiState.Error(exception.message ?: "Failed to create course")
                }
        }
    }

    fun resetState() {
        uiState = CreateCourseUiState.Idle
        // Optionally clear form fields too if needed
    }
}