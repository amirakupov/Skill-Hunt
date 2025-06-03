package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.ui.BrowseCoursesViewModel


@Composable
fun BrowseCoursesScreen(
    vm: BrowseCoursesViewModel
) {
    val list = vm.courses
    val err  = vm.errorMessage

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("All Courses", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        err?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        if (list.isEmpty()) {
            Text("No courses available at the moment.")
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(list) { course ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(course.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${course.category} • ${course.skillLevel}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(course.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "By: ${course.userEmail} • ${course.createdAt.take(10)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
