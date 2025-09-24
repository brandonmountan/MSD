package com.example.maraca.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

// Tell Room this is a database with Shake table, version 1
@Database(entities = [Shake::class], version = 1)
abstract class ShakeDatabase : RoomDatabase() {
    // Get the DAO for database operations
    abstract fun shakeDao(): ShakeDao

    companion object {
        // Make sure only one database instance exists
        @Volatile
        private var INSTANCE: ShakeDatabase? = null

        // Get or create the database instance
        fun getDatabase(context: Context): ShakeDatabase {
            // If database doesn't exist, create it
            return INSTANCE ?: synchronized(this) {
                // Build the database with name "shake_db"
                Room.databaseBuilder(context, ShakeDatabase::class.java, "shake_db").build()
                    .also { INSTANCE = it }
            }
        }
    }
}