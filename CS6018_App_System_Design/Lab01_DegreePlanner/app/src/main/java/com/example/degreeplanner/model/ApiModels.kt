package com.example.degreeplanner.model

import kotlinx.serialization.Serializable


// Main response from degreePlans.json
@Serializable
data class DegreePlansResponse(
    val plans: List<PlanInfo>
)

// Individual plan info from the main list
@Serializable
data class PlanInfo(
    val name: String,
    val path: String
)

@Serializable
data class PlanDetails(
    val name: String,
    val requirements: List<ApiRequirement>
)

@Serializable
data class ApiRequirement(
    val type: String,
    val course: ApiCourse? = null,
    val courses: List<ApiCourse>? = null
)

@Serializable
data class ApiCourse(
    val department: String,
    val number: String
)

fun ApiCourse.toCourse(): Course {
    // Convert string number to int, handle any parsing errors
    val courseNumber = number.toIntOrNull() ?: 0
    return Course(department, courseNumber)
}

fun ApiRequirement.toRequirement(): Requirement? {
    return when (type) {
        "requiredCourse" -> {
            course?.let { apiCourse ->
                val domainCourse = apiCourse.toCourse()
                val description = "Complete ${domainCourse.department} ${domainCourse.number}"
                Requirement.SpecificCourse(domainCourse, description)
            }
        }
        "oneOf" -> {
            courses?.let { apiCourses ->
                val domainCourses = apiCourses.map { it.toCourse() }
                val description = "Complete one of: ${domainCourses.joinToString(" or ")}"
                Requirement.OneOf(domainCourses, description)
            }
        }
        else -> {
            null
        }
    }
}

fun PlanDetails.toRequirements(): List<Requirement> {
    return requirements.mapNotNull { it.toRequirement() }
}