package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExcuseEntity::class], version = 1, exportSchema = false)
abstract class ExcuseDatabase : RoomDatabase() {
    abstract fun excuseDao(): ExcuseDao

    companion object {
        @Volatile
        private var INSTANCE: ExcuseDatabase? = null

        fun getInstance(context: Context): ExcuseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExcuseDatabase::class.java,
                    "excuse_finder.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
