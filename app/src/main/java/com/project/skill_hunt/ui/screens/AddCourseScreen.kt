package com.project.skill_hunt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.project.skill_hunt.data.model.AddCourseRequest
import com.project.skill_hunt.data.model.CourseResponse
import com.project.skill_hunt.ui.CourseViewModel
import com.project.skill_hunt.ui.theme.lighterBlue

@Composable
fun AddCourseScreen(
    vm: CourseViewModel,
    onSuccess: (CourseResponse) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("online") }
    var availability by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    val err = vm.errorMessage
    val success = vm.successResponse

    LaunchedEffect(success) {
        success?.let { onSuccess(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lighterBlue)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Add New Mentoring Course",
            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = title, onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = category, onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description, onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = skillLevel, onValueChange = { skillLevel = it },
            label = { Text("Skill Level") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Location:")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = locationType == "online", onClick = { locationType = "online" })
            Text("Online")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = locationType == "meet-up", onClick = { locationType = "meet-up" })
            Text("Meet-Up")
        }
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = availability, onValueChange = { availability = it },
            label = { Text("Availability") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = contactInfo, onValueChange = { contactInfo = it },
            label = { Text("Contact Info") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                vm.addCourse(
                    AddCourseRequest(
                        title, category, description,
                        skillLevel, locationType,
                        availability, contactInfo
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit")
        }

        err?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
