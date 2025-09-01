// DegreePlannerViewModel.kt - Simple state management
package com.example.degreeplanner.viewmodel

import androidx.compose.runtime.*
import com.example.degreeplanner.model.Course
import com.example.degreeplanner.model.Requirement
import com.example.degreeplanner.model.RequirementChecker

class DegreePlannerViewModel {

    // List of courses the student has added
    private val _courses = mutableStateListOf<Course>()
    val courses: List<Course> = _courses

    val requirements = listOf(
        Requirement.SpecificCourse(Course("CS", 101)),
        Requirement.OneOf(listOf(Course("PHIL", 101), Course("SOC", 101)))
    )

    // Functions
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
}