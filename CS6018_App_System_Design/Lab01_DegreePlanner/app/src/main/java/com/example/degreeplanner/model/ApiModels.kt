// ApiModels.kt - Simple data classes for API (CORRECTED)
package com.example.degreeplanner.model

import kotlinx.serialization.Serializable

/**
 * PHASE 2 SIMPLE: API response models matching actual server structure
 */

// Main response from degreePlans.json
@Serializable
data class DegreePlansResponse(
    val plans: List<PlanInfo>  // Fixed: "plans" not "degreePlans"
)

// Individual plan info
@Serializable
data class PlanInfo(
    val name: String,    // e.g., "Computer Science"
    val path: String     // e.g., "cs.json" - Fixed: "path" not "file"
)

// Individual degree plan details
@Serializable
data class PlanDetails(
    val name: String,
    val requirements: List<SimpleRequirement>
)

// Simple requirement from API
@Serializable
data class SimpleRequirement(
    val type: String,                    // "course" or "choice"
    val description: String,             // Human description
    val department: String? = null,      // For single course
    val number: Int? = null,             // For single course
    val options: List<SimpleCourse>? = null  // For choice requirements
)

// Simple course from API
@Serializable
data class SimpleCourse(
    val department: String,
    val number: Int
)

/**
 * Convert API models to our existing domain models
 */
fun SimpleCourse.toCourse(): Course = Course(department, number)

fun SimpleRequirement.toRequirement(): Requirement? {
    return when (type) {
        "course" -> {
            if (department != null && number != null) {
                Requirement.SpecificCourse(Course(department, number), description)
            } else null
        }
        "choice" -> {
            options?.let { opts ->
                val courses = opts.map { it.toCourse() }
                Requirement.OneOf(courses, description)
            }
        }
        else -> null
    }
}