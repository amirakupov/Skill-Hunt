package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.ui.CourseListViewModel
import com.project.skill_hunt.ui.theme.lightBlue
import com.project.skill_hunt.ui.theme.lighterBlue

@Composable
fun CourseListScreen(
    vm: CourseListViewModel,
    onAddCourse: () -> Unit
) {
    val list = vm.courses
    val err  = vm.errorMessage

    Column(
        Modifier
            .fillMaxSize()
            .background(lighterBlue)
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Courses", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onAddCourse, shape = RoundedCornerShape(12.dp)) {
                Text("Add")
            }
        }
        Spacer(Modifier.height(8.dp))

        err?.let {
            Text("Error: $it", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        if (list.isEmpty()) {
            Text("No courses yet. Tap ‘Add’ to create one.")
        } else {
            LazyColumn {
                items(list) { course ->
                    Card(Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text(course.title, style = MaterialTheme.typography.titleMedium)
                            Text(course.category + " • " + course.skillLevel, style = MaterialTheme.typography.bodySmall)
                            Text(course.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("When: ${course.availability}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
