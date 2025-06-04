package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.ui.CourseListViewModel
import com.project.skill_hunt.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    vm: CourseListViewModel,
    onAddCourse: () -> Unit,
    navBack: () -> Unit
) {
    val list = vm.courses
    val err = vm.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Courses") },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onAddCourse,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonBlue)
                    ) {
                        Text("Add")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(lighterBlue)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            err?.let {
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (list.isEmpty()) {
                Text("No courses yet. Tap ‘Add’ to create one.")
            } else {
                LazyColumn {
                    items(list) { course ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = darkestBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text(course.title, style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                Text(
                                    course.category + " • " + course.skillLevel,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Black
                                )
                                Text(
                                    course.description,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.Black
                                )
                                Text("When: ${course.availability}", style = MaterialTheme.typography.bodySmall, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

