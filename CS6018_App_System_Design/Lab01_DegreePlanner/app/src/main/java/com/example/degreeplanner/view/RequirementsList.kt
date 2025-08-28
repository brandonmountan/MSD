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
    requirements: List<Requirement>,
    courses: List<Course>
) {
    val allComplete = RequirementChecker.allSatisfied(requirements, courses)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Degree Requirements", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Show each requirement
            requirements.forEach { requirement ->
                val satisfied = RequirementChecker.isSatisfied(requirement, courses)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status indicator
                    Text(
                        text = if (satisfied) "✅" else "❌",
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Requirement description
                    Text(
                        text = requirement.description,
                        color = if (satisfied) Color.Green else Color.Red
                    )
                }
            }

            // Completion message
            if (allComplete) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("🎉 Degree requirements completed!")
                    }
                }
            }
        }
    }
}