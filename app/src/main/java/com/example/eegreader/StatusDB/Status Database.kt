package com.example.eegreader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
//database
@Database(entities = [StatusList::class], version = 2)
abstract class StatusDatabase : RoomDatabase() {
    abstract fun getStatusDao(): PatientStatusDao

    companion object {
        @Volatile
        private var instance: StatusDatabase? = null
        private val LOCK = Any()
fun getStatusDao(): PatientStatusDao? {return  null;}
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDb(context).also {
                instance = it
            }
        }

        private fun createDb(context: Context) =
            Room.databaseBuilder(context.applicationContext, StatusDatabase::class.java,
                "StatusDatabase"
            )
                .addMigrations(MigrationFrom2To3()) // Add your migration here
                .build()
    }
    class MigrationFrom2To3 : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column "patientId" to the existing table
            database.execSQL("ALTER TABLE status ADD COLUMN patid INTEGER NOT NULL DEFAULT 0")
        }
    }

}
