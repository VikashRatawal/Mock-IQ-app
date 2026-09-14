package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MockTestEntity::class,
        QuestionEntity::class,
        CustomReasonEntity::class,
        CustomTagEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MockTrackerDatabase : RoomDatabase() {

    abstract fun dao(): MockTrackerDao

    companion object {
        @Volatile
        private var INSTANCE: MockTrackerDatabase? = null

        fun getDatabase(context: Context): MockTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MockTrackerDatabase::class.java,
                    "mock_tracker_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
