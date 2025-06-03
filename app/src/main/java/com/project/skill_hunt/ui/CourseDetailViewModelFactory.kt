package com.project.skill_hunt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.data.repository.CourseRepository

class CourseDetailViewModelFactory(
    private val repo: CourseRepository,
    private val courseId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseDetailViewModel(repo, courseId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
