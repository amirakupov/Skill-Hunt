package com.project.skill_hunt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.skill_hunt.data.repository.CourseRepository


class CourseListViewModelFactory(
    private val repo: CourseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown VM class")
    }
}
