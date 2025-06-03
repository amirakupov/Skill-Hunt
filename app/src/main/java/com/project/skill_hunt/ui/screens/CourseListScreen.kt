package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.ui.CourseListViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextOverflow


@Composable
fun CourseListScreen(
    vm: CourseListViewModel,
    onAddCourse: () -> Unit
) {
    val list = vm.courses
    val err  = vm.errorMessage

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment   = Alignment.CenterVertically
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
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
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

