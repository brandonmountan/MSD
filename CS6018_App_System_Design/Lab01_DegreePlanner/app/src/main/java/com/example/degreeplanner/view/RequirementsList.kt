package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement
import com.example.degreeplanner.model.RequirementChecker

@Composable
fun RequirementsList(
    // All the requirements for the degree (comes from ViewModel)
    requirements: List<Requirement>,

    // All the courses the student has added (comes from ViewModel)
    courses: List<Course>
) {
    // This runs every time the component recomposes (when courses or requirements change)
    val allComplete = RequirementChecker.allSatisfied(requirements, courses)

    // CARD CONTAINER: Elevated container for the requirements section
    Card(modifier = Modifier.fillMaxWidth()) {
        // COLUMN LAYOUT: Stack all children vertically
        Column(modifier = Modifier.padding(16.dp)) {
            // HEADER
            Text("Degree Requirements", style = MaterialTheme.typography.titleMedium)

            // SPACING: Vertical space between header and requirements
            Spacer(modifier = Modifier.height(8.dp))

            // REQUIREMENTS LOOP: Show each requirement with its status
            // forEach loops through each requirement and creates UI for it
            requirements.forEach { requirement ->
                // REQUIREMENT STATUS: Check if this specific requirement is satisfied
                val satisfied = RequirementChecker.isSatisfied(requirement, courses)

                // REQUIREMENT ROW: Display one requirement with status indicator
                Row(
                    // Fill the full width and add vertical padding
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    // Center items vertically within the row
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // STATUS ICON: Visual indicator of requirement status
                    Text(
                        // AI tools help me add fancy icons
                        text = if (satisfied) "✅" else "❌",
                        // Add space to the right of the icon
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // REQUIREMENT TEXT: Description of what's required
                    Text(
                        // The description comes from the requirement itself
                        // e.g., "Take CS 101" or "Take one of: PHIL 101 or SOC 101"
                        text = requirement.description,
                        // Change color based on status: green if satisfied, red if not
                        color = if (satisfied) Color.Green else Color.Red
                    )
                }
            }

            // CELEBRATION SECTION: Show success message when everything is complete
            if (allComplete) {
                // SPACING: Add space before celebration message
                Spacer(modifier = Modifier.height(16.dp))

                // CELEBRATION CARD: Special highlighted card for completion
                Card(
                    // Use primary color scheme for celebration
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    // CELEBRATION ROW: Icon and message side by side
                    Row(
                        // Add padding inside the celebration card
                        modifier = Modifier.padding(16.dp),
                        // Center items vertically
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // SUCCESS ICON: Checkmark circle icon
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            // Space between icon and text
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        // SUCCESS MESSAGE: Congratulations text with emoji
                        Text("🎉 Degree requirements completed!")
                    }
                }
            }
        }
    }
}