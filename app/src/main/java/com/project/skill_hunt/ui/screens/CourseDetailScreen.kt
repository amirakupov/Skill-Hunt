package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment           // ← make sure this is imported
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.skill_hunt.data.repository.CourseRepository
import com.project.skill_hunt.ui.CourseDetailViewModel
import com.project.skill_hunt.ui.CourseDetailViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: Long,
    repo: CourseRepository,
    navUp: () -> Unit
) {
    // Create the ViewModel with our factory
    val viewModel: CourseDetailViewModel = viewModel(
        factory = CourseDetailViewModelFactory(repo, courseId)
    )

    val course = viewModel.course
    val err    = viewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Details") },
                navigationIcon = {
                    IconButton(onClick = navUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                err?.let { message ->
                    Text(
                        text = "Error loading course: $message",
                        color = MaterialTheme.colorScheme.error
                    )
                    return@Column
                }

                if (course == null) {
                    // Here is where we center the loader horizontally:
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    // Display all fields from CourseResponse
                    Text("Title: ${course.title}", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))

                    Text("Category: ${course.category}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))

                    Text("Skill Level: ${course.skillLevel}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))

                    Text("Location: ${course.locationType}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))

                    Text("Availability: ${course.availability}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(4.dp))

                    Text("Contact Info: ${course.contactInfo}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))

                    Text("Description:", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(course.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))

                    Text("Offered by: ${course.userEmail}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))

                    Text("Posted on: ${course.createdAt.take(10)}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    )
}
