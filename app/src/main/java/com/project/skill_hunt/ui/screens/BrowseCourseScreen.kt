package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.ui.BrowseCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseCoursesScreen(
    vm: BrowseCoursesViewModel,
    navToDetail: (Long) -> Unit,
    navBack: () -> Unit // <--- pass this from the screen above
) {
    val allCourses = vm.courses
    val err        = vm.errorMessage

    val categories = remember(allCourses) {
        listOf("All") + allCourses.map { it.category.trim() }.distinct().sorted()
    }

    var selectedCategory by remember { mutableStateOf("All") }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    val filteredCourses = remember(allCourses, selectedCategory) {
        if (selectedCategory == "All") allCourses
        else allCourses.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse All Courses") },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Courses",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(onClick = { filterMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter by category"
                        )
                    }

                    DropdownMenu(
                        expanded = filterMenuExpanded,
                        onDismissRequest = { filterMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            onClick = {
                                selectedCategory = "All"
                                filterMenuExpanded = false
                            }
                        )
                        categories.drop(1).forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    filterMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            err?.let { message ->
                Text("Error loading courses: $message", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (filteredCourses.isEmpty() && err == null) {
                Text("No courses available in “$selectedCategory”.")
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredCourses) { course ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { navToDetail(course.id) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(course.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text("${course.category} • ${course.skillLevel}", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(4.dp))
                                Text(course.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(4.dp))
                                Text("By: ${course.userEmail} • ${course.createdAt.take(10)}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(course: CourseResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${course.category} • ${course.skillLevel}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = course.description,
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "By: ${course.userEmail} • ${course.createdAt.take(10)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

