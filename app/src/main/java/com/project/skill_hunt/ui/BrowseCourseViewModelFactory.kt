package com.project.skill_hunt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.data.repository.CourseRepository

class BrowseCoursesViewModelFactory(
    private val repo: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowseCoursesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrowseCoursesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
