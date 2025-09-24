package com.example.maraca.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Tell Room this handles database operations
@Dao
interface ShakeDao {
    // Get the 20 most recent shakes, newest first
    @Query("SELECT * FROM shakes ORDER BY timestamp DESC LIMIT 20")
    fun getRecentShakes(): Flow<List<Shake>>

    // Add a new shake to the database
    @Insert
    suspend fun insertShake(shake: Shake)

    // Delete shakes older than a certain time
    @Query("DELETE FROM shakes WHERE timestamp < :threshold")
    suspend fun deleteOldShakes(threshold: Long)

    // Delete all shakes from the database
    @Query("DELETE FROM shakes")
    suspend fun deleteAllShakes()
}