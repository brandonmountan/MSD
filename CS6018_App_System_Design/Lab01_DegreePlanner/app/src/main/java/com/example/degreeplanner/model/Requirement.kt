package com.example.degreeplanner.model

sealed class Requirement {
    abstract val description: String

    /**
     * Must take this specific course
     */
    data class SpecificCourse(
        val course: Course,
        override val description: String = "Take ${course.department} ${course.number}"
    ) : Requirement()

    /**
     * Must take one course from this list
     */
    data class OneOf(
        val courses: List<Course>,
        override val description: String = "Take one of: ${courses.joinToString(" or ")}"
    ) : Requirement()
}