package com.example.eegreader.ReadingsDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//database
@Database(entities = [ReadingsList::class], version = 8)
abstract class ReadingsDatabase : RoomDatabase() {
    abstract fun getReadingsDao(): ReadingsDao

    companion object {
        @Volatile
        private var instance: ReadingsDatabase? = null
        private val LOCK = Any()
        fun getReadingsDao(): ReadingsDao? {return  null;}
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDb(context).also {
                instance = it
            }
        }

        private fun createDb(context: Context) =
            Room.databaseBuilder(context.applicationContext, ReadingsDatabase::class.java,
                "ReadingsDatabase"
            )
        .fallbackToDestructiveMigration()
                .addMigrations(MigrationFrom2To3())
                .build()
    }

    class MigrationFrom2To3 : Migration(2, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column "patientId" to the existing table
            database.execSQL("ALTER TABLE READINGS ADD COLUMN VISITTIME")
        }
    }
}
