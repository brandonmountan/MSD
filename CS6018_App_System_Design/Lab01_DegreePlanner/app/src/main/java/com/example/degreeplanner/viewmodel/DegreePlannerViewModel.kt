// DegreePlannerViewModel.kt - SIMPLE Phase 2 version
package com.example.degreeplanner.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.degreeplanner.model.*
import com.example.degreeplanner.repository.DegreePlanRepository
import kotlinx.coroutines.launch

/**
 * PHASE 2 SIMPLE: Enhanced ViewModel with basic API support
 */
class DegreePlannerViewModel : ViewModel() {

    private val repository = DegreePlanRepository()

    // Existing state from Phase 1
    private val _courses = mutableStateListOf<Course>()
    val courses: List<Course> = _courses

    // New state for Phase 2
    var availablePlans by mutableStateOf(listOf<PlanInfo>())
        private set

    var selectedPlan by mutableStateOf<PlanInfo?>(null)
        private set

    private val _requirements = mutableStateListOf<Requirement>()
    val requirements: List<Requirement> = _requirements

    var isLoading by mutableStateOf(false)
        private set

    init {
        // Load default requirements and fetch plans
        loadDefaultRequirements()
        loadPlans()
    }

    // Existing methods from Phase 1 (unchanged)
    fun addCourse(course: Course) {
        if (!_courses.contains(course)) {
            _courses.add(course)
        }
    }

    fun removeCourse(course: Course) {
        _courses.remove(course)
    }

    fun isComplete(): Boolean =
        RequirementChecker.allSatisfied(requirements, courses)

    fun isRequirementSatisfied(requirement: Requirement): Boolean =
        RequirementChecker.isSatisfied(requirement, courses)

    // New methods for Phase 2
    private fun loadDefaultRequirements() {
        _requirements.clear()
        _requirements.addAll(
            listOf(
                Requirement.SpecificCourse(Course("CS", 101)),
                Requirement.OneOf(
                    listOf(Course("PHIL", 101), Course("SOC", 101)),
                    "Complete one of: Philosophy or Sociology"
                )
            )
        )
    }

    fun loadPlans() {
        viewModelScope.launch {
            isLoading = true
            availablePlans = repository.getPlans()
            isLoading = false
        }
    }

    fun selectPlan(plan: PlanInfo) {
        selectedPlan = plan
        viewModelScope.launch {
            isLoading = true
            val newRequirements = repository.getRequirements(plan.path)
            _requirements.clear()
            _requirements.addAll(newRequirements)
            isLoading = false
        }
    }
}