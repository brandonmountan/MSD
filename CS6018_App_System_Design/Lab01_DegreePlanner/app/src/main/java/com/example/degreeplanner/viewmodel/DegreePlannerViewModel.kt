// DegreePlannerViewModel.kt - Simple state management
package com.example.degreeplanner.viewmodel

import androidx.compose.runtime.*
import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement
import com.example.degreeplanner.model.RequirementChecker

/**
 * Simple ViewModel for managing degree planning state
 */
class DegreePlannerViewModel {

    // List of courses the student has added
    private val _courses = mutableStateListOf<Course>()
    val courses: List<Course> = _courses

    // Simple degree requirements: CS 101 + (PHIL 101 OR SOC 101)
    val requirements = listOf(
        Requirement.SpecificCourse(Course("CS", 101)),
        Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
    )

    /**
     * Add a course to the list
     */
    fun addCourse(course: Course) {
        if (!_courses.contains(course)) {
            _courses.add(course)
        }
    }

    /**
     * Remove a course from the list
     */
    fun removeCourse(course: Course) {
        _courses.remove(course)
    }

    /**
     * Check if all requirements are satisfied
     */
    fun isComplete(): Boolean =
        RequirementChecker.allSatisfied(requirements, courses)

    /**
     * Check if a specific requirement is satisfied
     */
    fun isRequirementSatisfied(requirement: Requirement): Boolean =
        RequirementChecker.isSatisfied(requirement, courses)
}