package com.example.degreeplanner.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.degreeplanner.viewmodel.DegreePlannerViewModel

@Composable
fun DegreePlannerScreen(
    viewModel: DegreePlannerViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Degree Planner",
            style = MaterialTheme.typography.headlineMedium
        )

        PlanSelector(
            plans = viewModel.availablePlans,
            selectedPlan = viewModel.selectedPlan,
            isLoading = viewModel.isLoading,
            onPlanSelected = { plan -> viewModel.selectPlan(plan) },
            onRefresh = { viewModel.loadPlans() }
        )

        CourseInputCard(
            onAddCourse = { course -> viewModel.addCourse(course) }
        )

        CoursesList(
            courses = viewModel.courses,
            onRemove = { course -> viewModel.removeCourse(course) }
        )

        RequirementsList(
            requirements = viewModel.requirements,
            courses = viewModel.courses
        )
    }
}