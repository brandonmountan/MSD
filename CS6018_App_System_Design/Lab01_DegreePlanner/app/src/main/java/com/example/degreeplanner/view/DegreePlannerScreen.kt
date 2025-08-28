package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.viewmodel.DegreePlannerViewModel

@Composable
fun DegreePlannerScreen() {
    val viewModel = remember { DegreePlannerViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App title
        Text(
            "Degree Planner",
            style = MaterialTheme.typography.headlineMedium
        )

        // Course input
        CourseInputCard(
            onAddCourse = { course ->
                viewModel.addCourse(course)
            }
        )

        // Course list
        CoursesList(
            courses = viewModel.courses,
            onRemove = { course ->
                viewModel.removeCourse(course)
            }
        )

        // Requirements
        RequirementsList(
            requirements = viewModel.requirements,
            courses = viewModel.courses
        )
    }
}