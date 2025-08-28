package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.model.Course

@Composable
fun CourseInputCard(
    onAddCourse: (Course) -> Unit
) {
    var department by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Add Course", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
                    placeholder = { Text("CS") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Number") },
                    placeholder = { Text("101") },
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        val courseNumber = number.toIntOrNull()
                        if (department.isNotBlank() && courseNumber != null) {
                            onAddCourse(Course(department.uppercase(), courseNumber))
                            department = ""
                            number = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}