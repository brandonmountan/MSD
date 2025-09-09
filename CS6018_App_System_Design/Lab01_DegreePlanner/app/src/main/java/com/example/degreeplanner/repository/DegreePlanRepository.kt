package com.example.degreeplanner.repository

import com.example.degreeplanner.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class DegreePlanRepository {

    // HTTP client configured for JSON parsing
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true      // Ignore extra fields in JSON
                coerceInputValues = true      // Handle type mismatches gracefully
            })
        }
    }

    private val baseUrl = "https://msd2025.github.io/degreePlans/"

    suspend fun getPlans(): List<PlanInfo> {
        return try {
            val response = client.get("${baseUrl}degreePlans.json")
            val data: DegreePlansResponse = response.body()
            data.plans
        } catch (e: Exception) {
            // Return empty list if API fails - graceful fallback
            println("Failed to load degree plans: ${e.message}")
            emptyList()
        }
    }

    suspend fun getRequirements(path: String): List<Requirement> {
        return try {
            val response = client.get("$baseUrl$path")
            val data: PlanDetails = response.body()

            // Convert API requirements to domain requirements
            data.toRequirements()
        } catch (e: Exception) {
            // Fallback to default requirements if API fails
            println("Failed to load requirements for $path: ${e.message}")
            getDefaultRequirements()
        }
    }

    fun getDefaultRequirements(): List<Requirement> {
        return listOf(
            Requirement.SpecificCourse(Course("CS", 101), "Complete CS 101"),
            Requirement.OneOf(
                listOf(Course("PHIL", 101), Course("SOC", 101)),
                "Complete one of: PHIL 101 or SOC 101"
            )
        )
    }

    fun close() {
        client.close()
    }
}