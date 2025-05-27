package com.project.skill_hunt.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.skill_hunt.data.model.AddCourseRequest
import com.project.skill_hunt.data.model.CourseResponse

@Composable
fun AddCourseScreen(
    vm: CourseViewModel,
    onSuccess: (CourseResponse) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var locationType by remember { mutableStateOf("online") }  // default
    var availability by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    val err = vm.errorMessage
    val success = vm.successResponse

    LaunchedEffect(success) {
        success?.let { onSuccess(it) }
    }

    Column(Modifier.padding(16.dp)) {
        Text("Add New Mentoring Course", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(title, { title = it }, label = { Text("Title") })
        OutlinedTextField(category, { category = it }, label = { Text("Category") })
        OutlinedTextField(description, { description = it }, label = { Text("Description") })
        OutlinedTextField(skillLevel, { skillLevel = it }, label = { Text("Skill Level") })

        // Location toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Location:")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = locationType=="online", onClick={locationType="online"})
            Text("Online")
            Spacer(Modifier.width(8.dp))
            RadioButton(selected = locationType=="meet-up", onClick={locationType="meet-up"})
            Text("Meet-Up")
        }

        OutlinedTextField(availability, { availability = it }, label = { Text("Availability") })
        OutlinedTextField(contactInfo, { contactInfo = it }, label = { Text("Contact Info") })

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            vm.addCourse(
                AddCourseRequest(
                title, category, description,
                skillLevel, locationType,
                availability, contactInfo
            )
            )
        }, Modifier.fillMaxWidth()) {
            Text("Submit")
        }

        err?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
