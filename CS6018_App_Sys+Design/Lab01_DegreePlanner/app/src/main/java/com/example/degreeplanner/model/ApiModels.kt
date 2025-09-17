package com.example.degreeplanner.model

import kotlinx.serialization.Serializable


// Main response from degreePlans.json API endpoint
@Serializable // Tells Kotlin this can be converted to/from JSON automatically
data class DegreePlansResponse(
    // The "plans" field in the JSON becomes this property
    val plans: List<PlanInfo>
)

// Individual plan info from the main degree plans list
@Serializable
data class PlanInfo(
    // Human readable name oft he degree plan ("Computer Science", etc.)
    val name: String,
    // File path to get detailed requirements ("cs_bs.json", etc.)
    val path: String
)

// Detailed information about specific degree plan
// WWhat we get when we fetch individual plan file (like cs_bs.json)
@Serializable
data class PlanDetails(
    // Name of this specific plan
    val name: String,
    // List of all requirements for this degree plan
    val requirements: List<ApiRequirement>
)

// A single requirement as it comes from the API
// "raw" format from JSON, before we convert to internal Requirement classes
@Serializable
data class ApiRequirement(
    // type of requirement: "requiredCourse" or "oneOf"
    val type: String,
    // Single course (only comes with "requiredCourse type)
    val course: ApiCourse? = null,
    // Multiple course options (only comes with "oneOf" type)
    val courses: List<ApiCourse>? = null
)

// A single course as it comes from the API
@Serializable
data class ApiCourse(
    // Department code as a string ("CS", "MATH", etc.)
    val department: String,
    // Course number as a string ("101", etc.)
    // convert to int
    val number: String
)

// internal function to convert API string to int so I can use it
fun ApiCourse.toCourse(): Course {
    // Convert string number to int, handle any parsing errors
    val courseNumber = number.toIntOrNull() ?: 0
    // Create internal Course object with converted int
    return Course(department, courseNumber)
}

// Convert API requirement format to internal requirement format
fun ApiRequirement.toRequirement(): Requirement? {
    // using 'when' because of different requirement types (oneOf, requiredCourse, etc.)
    return when (type) {
        // Case 1: single required course
        "requiredCourse" -> {
            course?.let { apiCourse ->
                val domainCourse = apiCourse.toCourse()
                val description = "Complete ${domainCourse.department} ${domainCourse.number}"
                Requirement.SpecificCourse(domainCourse, description)
            }
        }
        // Case 2
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