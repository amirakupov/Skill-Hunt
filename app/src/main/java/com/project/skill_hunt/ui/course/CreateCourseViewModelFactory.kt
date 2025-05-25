package com.project.skill_hunt.ui.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.data.repository.CourseRepository
import com.project.skill_hunt.domain.usecase.course.SubmitCourseUseCase

class CreateCourseViewModelFactory(
    private val courseRepository: CourseRepository // Injected
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val submitCourseUseCase = SubmitCourseUseCase(courseRepository)
            return CreateCourseViewModel(submitCourseUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for CreateCourse")
    }
}