package com.example.eegreader.ClinicalDB1

import com.example.eegreader.ClinicalDB.ClinicalDao
import com.example.eegreader.ClinicalDB.ClinicalList


import com.example.eegreader.database.PatientStatusDao
import com.example.eegreader.database.StatusList


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.eegreader.ReadingsDB.ReadingsDatabase

//database
@Database(entities = [ClinicalList1::class], version = 1)
abstract class ClinicalDatabase1 : RoomDatabase() {
    abstract fun getClinicalDao1(): ClinicalDao1

    companion object {
        @Volatile
        private var instance: ClinicalDatabase1? = null
        private val LOCK = Any()
        fun getClinicalDao1(): ClinicalDao1? {return  null;}
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDb(context).also {
                instance = it
            }
        }

        private fun createDb(context: Context) =
            Room.databaseBuilder(context.applicationContext, ClinicalDatabase1::class.java,
                "ClinicalDatabase1"
            )
                // Add your migration here

                .build()

    }
    class MigrationFrom2To3 : Migration(1, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column "patientId" to the existing table


            // Drop the old table
            //  database.execSQL("DROP TABLE ClinicalData")
            database.execSQL("Drop TABLE ClinicalData")
            database.execSQL("Create TABLE ClinicalData(Clinical TEXT,PatientId TEXT,Id INTEGER )")
            // Rename the new table to the original table name
            //  database.execSQL("CREATE TABLE ClinicalData (PatientId TEXT,Clinical TEXT, Id INTEGER)")

            //  database.execSQL("ALTER TABLE ClinicalData RENAME Clinical1 to Clinical")

        }
    }

}
