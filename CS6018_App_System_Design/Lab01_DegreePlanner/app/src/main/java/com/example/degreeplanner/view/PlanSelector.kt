package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.degreeplanner.model.PlanInfo

@Composable
fun PlanSelector(
    plans: List<PlanInfo>,
    selectedPlan: PlanInfo?,
    isLoading: Boolean,
    onPlanSelected: (PlanInfo) -> Unit,
    onRefresh: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Degree Plans", style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loading or plans
            if (isLoading) {
                Text("Loading...")
            } else if (plans.isEmpty()) {
                Text("No plans available")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(plans) { plan ->
                        Card(
                            onClick = { onPlanSelected(plan) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (plan == selectedPlan) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                        ) {
                            Text(
                                text = plan.name,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}