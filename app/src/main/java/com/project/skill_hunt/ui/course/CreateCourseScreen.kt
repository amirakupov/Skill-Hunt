package com.project.skill_hunt.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.text.isBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(
    // The factory will be provided from where instantiate this screen (e.g., MainActivity or NavHost)
    viewModelFactory: CreateCourseViewModelFactory,
    onCourseCreatedSuccessfully: (String) -> Unit // Callback with courseId to navigate or show success
) {
    val viewModel: CreateCourseViewModel = viewModel(factory = viewModelFactory)
    val uiState = viewModel.uiState

    // Handle UI state changes, e.g., navigation on success
    LaunchedEffect(uiState) {
        if (uiState is CreateCourseUiState.Success) {
            onCourseCreatedSuccessfully(uiState.courseId)
            viewModel.resetState() // Reset state after handling
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create New Course") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Enter Course Details",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = { Text("Course Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState is CreateCourseUiState.Error && viewModel.title.isBlank() // Example error indication
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Course Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Multi-line
                isError = uiState is CreateCourseUiState.Error && viewModel.description.isBlank() // Example error
            )

            // Add other OutlinedTextFields for location, category, price, etc.
            // Example:
            // OutlinedTextField(
            //     value = viewModel.category,
            //     onValueChange = { viewModel.category = it },
            //     label = { Text("Category (e.g., Programming, Music)") },
            //     modifier = Modifier.fillMaxWidth()
            // )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.createCourse() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is CreateCourseUiState.Loading // Disable button when loading
            ) {
                if (uiState is CreateCourseUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Course")
                }
            }

            if (uiState is CreateCourseUiState.Error) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (uiState is CreateCourseUiState.Success) {
                // Success message is handled by navigation callback,
                // but could also show a temporary success message here if desired before navigation.
            }
        }
    }
}