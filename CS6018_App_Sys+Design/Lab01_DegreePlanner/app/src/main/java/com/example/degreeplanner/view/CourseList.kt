package com.example.degreeplanner.view

// Import layout tools for arranging UI elements
import androidx.compose.foundation.layout.*
// Import LazyColumn for efficiently displaying large lists
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
// Import Material Design icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
// Import Material Design 3 components
import androidx.compose.material3.*
// Import Compose runtime for state management
import androidx.compose.runtime.Composable
// Import alignment tools
import androidx.compose.ui.Alignment
// Import modifier for styling
import androidx.compose.ui.Modifier
// Import measurement units
import androidx.compose.ui.unit.dp
// Import our Course data class
import com.example.degreeplanner.model.Course

/**
 * COMPOSABLE FUNCTION: Displays a list of courses with ability to remove them
 * @param courses List of Course objects to display
 * @param onRemove Callback function called when user wants to remove a course
 */
@Composable
fun CoursesList(
    // The list of courses to show (comes from parent/ViewModel)
    courses: List<Course>,
    // Function to call when user clicks delete button on a course
    onRemove: (Course) -> Unit
) {
    // CARD CONTAINER: Creates elevated container for the entire course list
    Card(modifier = Modifier.fillMaxWidth()) {
        // COLUMN LAYOUT: Stack children vertically
        Column(modifier = Modifier.padding(16.dp)) {
            // HEADER: Shows title and count of courses
            Text("Your Courses (${courses.size})", style = MaterialTheme.typography.titleMedium)

            // SPACING: Add vertical space between header and content
            Spacer(modifier = Modifier.height(8.dp))

            // CONDITIONAL DISPLAY: Show different content based on whether list is empty
            if (courses.isEmpty()) {
                // EMPTY STATE: Show this when no courses are added yet
                Text("No courses added yet", style = MaterialTheme.typography.bodyMedium)
            } else {
                // COURSE LIST: Show this when there are courses to display
                // LazyColumn is like RecyclerView - only renders visible items for performance
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // items() creates one UI element for each course in the list
                    items(courses) { course ->
                        // Create a card for each individual course
                        Card(
                            // Set background color to distinguish from main card
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            // ROW LAYOUT: Arrange course info and delete button horizontally
                            Row(
                                // Fill the entire width of the card
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                // Push course name to left and delete button to right
                                horizontalArrangement = Arrangement.SpaceBetween,
                                // Center both items vertically within the row
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // COURSE NAME: Display the course (e.g., "CS 101")
                                // toString() is called automatically, returns "CS 101" format
                                Text(course.toString())

                                // DELETE BUTTON: Clickable icon to remove this course
                                IconButton(onClick = { onRemove(course) }) {
                                    // TRASH ICON: Shows delete icon inside the button
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}