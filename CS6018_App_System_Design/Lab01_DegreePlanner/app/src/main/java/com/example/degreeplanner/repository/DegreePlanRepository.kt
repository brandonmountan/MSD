// DegreePlanRepository.kt - SIMPLE version
package com.example.degreeplanner.repository

import com.example.degreeplanner.model.*
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * PHASE 2 SIMPLE: Basic repository for API calls
 */
class DegreePlanRepository {

    // Simple HTTP client
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val baseUrl = "https://msd2025.github.io/degreePlans/"

    /**
     * Get list of available plans
     */
    suspend fun getPlans(): List<PlanInfo> {
        return try {
            val response = client.get("${baseUrl}degreePlans.json")
            val data: DegreePlansResponse = response.body()
            data.plans
        } catch (e: Exception) {
            emptyList() // Simple fallback
        }
    }

    /**
     * Get requirements for a specific plan
     */
    suspend fun getRequirements(path: String): List<Requirement> {
        return try {
            val response = client.get("$baseUrl$path")
            val data: PlanDetails = response.body()
            data.requirements.mapNotNull { it.toRequirement() }
        } catch (e: Exception) {
            getDefaultRequirements() // Fallback to default
        }
    }

    /**
     * Default requirements (same as Phase 1)
     */
    private fun getDefaultRequirements(): List<Requirement> {
        return listOf(
            Requirement.SpecificCourse(Course("CS", 101)),
            Requirement.OneOf(
                listOf(Course("PHIL", 101), Course("SOC", 101)),
                "Complete one of: Philosophy or Sociology"
            )
        )
    }
}