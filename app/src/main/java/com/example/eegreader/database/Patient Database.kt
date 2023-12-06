package com.example.eegreader.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//Using database to store the list of groceries added by the user
@Database(entities = [Patientlist::class], version = 18, exportSchema = false)


//Creating an abstract class
abstract class PatientDatabase :RoomDatabase(){
    abstract fun getEEGPatientDao():EEGPatientDao

    //Using implicit intent
    companion object{
        @Volatile
        private var instance: PatientDatabase?=null
        private val LOCK=Any()

        operator fun invoke(context:Context)= instance?: synchronized(LOCK){
            instance?: createDb(context).also {
                instance=it
            }
        }

        //Building database
        private fun createDb(context: Context)=
            Room.databaseBuilder(context.applicationContext,PatientDatabase::class.java,"Grocery.db")
                .addMigrations(PatientDatabase.MigrationFrom2To3())
                .build()
    }
    class MigrationFrom2To3 : Migration(13, 18) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column "patientId" to the existing table
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Weight TEXT")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Height TEXT")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Race TEXT")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN BMI TEXT")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Diabetes INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN HTN INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Asthma INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Smoking INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Alcohol INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN MI INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN CVA INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN Stent INTEGER")
            database.execSQL("ALTER TABLE grocery_items ADD COLUMN CABG INTEGER")
        }
    }
}