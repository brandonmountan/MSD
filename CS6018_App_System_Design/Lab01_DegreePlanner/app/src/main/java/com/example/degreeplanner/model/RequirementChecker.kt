package com.example.degreeplanner.model

object RequirementChecker {

    /**
     * Check if a requirement is satisfied by the completed courses
     */
    fun isSatisfied(requirement: Requirement, completedCourses: List<Course>): Boolean =
        when (requirement) {
            is Requirement.SpecificCourse ->
                completedCourses.contains(requirement.course)
            is Requirement.OneOf ->
                requirement.courses.any { course -> completedCourses.contains(course) }
        }

    /**
     * Check if all requirements are satisfied
     */
    fun allSatisfied(requirements: List<Requirement>, completedCourses: List<Course>): Boolean =
        requirements.all { isSatisfied(it, completedCourses) }
}