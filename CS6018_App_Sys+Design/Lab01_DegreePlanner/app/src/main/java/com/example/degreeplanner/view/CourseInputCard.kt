package com.example.degreeplanner.view

// Import layout and styling tools from Compose
import androidx.compose.foundation.layout.*
// Import Material Design icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
// Import Material Design 3 components (buttons, text fields, etc.)
import androidx.compose.material3.*
// Import Compose state and effect management
import androidx.compose.runtime.*
// Import alignment tools for positioning UI elements
import androidx.compose.ui.Alignment
// Import modifier for styling and behavior
import androidx.compose.ui.Modifier
// Import measurement units (density-independent pixels)
import androidx.compose.ui.unit.dp
// Import our Course data class
import com.example.degreeplanner.model.Course

/**
 * COMPOSABLE FUNCTION: Creates the UI for adding new courses
 * @param onAddCourse Callback function - when user adds a course, this function gets called
 * This is an example of "state hoisting" - the data lives in parent component
 */
@Composable
fun CourseInputCard(
    // This parameter is a function that takes a Course and returns nothing (Unit)
    // When user successfully adds a course, we call this function to notify parent
    onAddCourse: (Course) -> Unit
) {
    // LOCAL STATE: These variables belong only to this component
    // 'remember' keeps the variable alive across recompositions (UI updates)
    // 'mutableStateOf' creates a variable that triggers UI updates when changed

    // Stores what the user types in the department field
    var department by remember { mutableStateOf("") }
    // Stores what the user types in the number field
    var number by remember { mutableStateOf("") }

    // CARD CONTAINER: Creates a elevated rectangular container with rounded corners
    Card(modifier = Modifier.fillMaxWidth()) {
        // COLUMN LAYOUT: Arranges children vertically (top to bottom)
        Column(modifier = Modifier.padding(16.dp)) {
            // TITLE: Shows "Add Course" at the top
            Text("Add Course", style = MaterialTheme.typography.titleMedium)

            // SPACING: Adds 8dp of vertical space between title and inputs
            Spacer(modifier = Modifier.height(8.dp))

            // ROW LAYOUT: Arranges children horizontally (left to right)
            Row(
                // Put 8dp of space between each child in the row
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                // Center all children vertically within the row
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DEPARTMENT INPUT FIELD: Text field for course department
                OutlinedTextField(
                    // Current value shown in the text field
                    value = department,
                    // Called every time user types a character
                    onValueChange = { department = it },
                    // Shows "Department" as a floating label
                    label = { Text("Department") },
                    // Shows gray hint text when field is empty
                    placeholder = { Text("CS") },
                    // Takes up equal space with the number field (both get 50% width)
                    modifier = Modifier.weight(1f)
                )

                // COURSE NUMBER INPUT FIELD: Text field for course number
                OutlinedTextField(
                    // Current value shown in the text field
                    value = number,
                    // Called every time user types a character
                    onValueChange = { number = it },
                    // Shows "Number" as a floating label
                    label = { Text("Number") },
                    // Shows gray hint text when field is empty
                    placeholder = { Text("101") },
                    // Takes up equal space with the department field (both get 50% width)
                    modifier = Modifier.weight(1f)
                )

                // ADD BUTTON: Button to add the course to the list
                Button(
                    // This code runs when user clicks the button
                    onClick = {
                        // Try to convert the number string to an actual integer
                        val courseNumber = number.toIntOrNull()
                        // Check if input is valid:
                        // - department is not blank (has some text)
                        // - courseNumber converted successfully (not null)
                        // - courseNumber is positive (greater than 0)
                        if (department.isNotBlank() && courseNumber != null) {
                            // Create a new Course object with the user's input
                            // .uppercase() converts "cs" to "CS" for consistency
                            onAddCourse(Course(department.uppercase(), courseNumber))
                            // Clear the input fields after successful add
                            department = ""
                            number = ""
                        }
                        // If validation fails, do nothing (could add error handling later)
                    }
                ) {
                    // BUTTON CONTENT: Shows a "+" icon inside the button
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}