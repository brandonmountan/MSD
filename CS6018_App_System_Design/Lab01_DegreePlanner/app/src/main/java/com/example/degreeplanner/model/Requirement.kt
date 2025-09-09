package com.example.degreeplanner.model

sealed class Requirement {
    abstract val description: String

    data class SpecificCourse(
        val course: Course,
        override val description: String = "Take ${course.department} ${course.number}"
    ) : Requirement()

    data class OneOf(
        val courses: List<Course>,
        override val description: String = "Take one of: ${courses.joinToString(" or ")}"
    ) : Requirement()
}