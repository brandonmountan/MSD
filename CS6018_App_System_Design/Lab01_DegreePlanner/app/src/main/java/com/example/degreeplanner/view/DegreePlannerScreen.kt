package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.viewmodel.DegreePlannerViewModel

/**
 * MAIN COMPOSABLE: The primary screen of our app that coordinates all other components
 * This is the "parent" component that manages shared state and passes it to children
 */
@Composable
fun DegreePlannerScreen() {
    // VIEWMODEL CREATION: Create and remember the ViewModel instance
    // remember ensures the same ViewModel instance is used across recompositions
    // This prevents losing data when the UI rebuilds (e.g., during screen rotation)
    val viewModel = remember { DegreePlannerViewModel() }

    // MAIN LAYOUT: Column arranges all major sections vertically
    Column(
        // MODIFIERS: Configure the layout behavior and appearance
        modifier = Modifier
            // Take up all available screen space
            .fillMaxSize()
            // Add 16dp padding on all sides (away from screen edges)
            .padding(16.dp),
        // ARRANGEMENT: Add 16dp space between each child component
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App title
        Text(
            "Degree Planner",
            style = MaterialTheme.typography.headlineMedium
        )

        // Course input
        CourseInputCard(
            // CALLBACK: When user adds a course, this function runs
            onAddCourse = { course ->
                // Call the ViewModel's method to add the course to our data
                // The ViewModel handles duplicate prevention and state updates
                viewModel.addCourse(course)
            }
        )

        // Course list
        CoursesList(
            // PASS STATE DOWN: Give the component access to current course list
            courses = viewModel.courses,
            // CALLBACK: When user removes a course, this function runs
            onRemove = { course ->
                // Call the ViewModel's method to remove the course from our data
                // The ViewModel handles the state update and UI refresh
                viewModel.removeCourse(course)
            }
        )

        // Requirements
        RequirementsList(
            // PASS STATE DOWN: Give the component access to requirements
            requirements = viewModel.requirements,
            // PASS STATE DOWN: Give the component access to current courses
            // The component uses this to calculate which requirements are satisfied
            courses = viewModel.courses
        )
    }
}