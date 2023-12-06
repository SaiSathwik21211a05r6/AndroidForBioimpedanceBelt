package com.example.eegreader.ReadingsDB


//Making necessary imports
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.json.JSONObject

//Creating data access object for status database
@Dao
interface ReadingsDao {

    //Inserting new status into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReadings(item: ReadingsList)

    //Removing status from the database
    @Delete
    suspend fun deleteReadings(item: ReadingsList)

    //Creating a query
    @Query("Select * FROM Readings")
    fun getthelistofReadings():LiveData<List<ReadingsList>>

    @Query("UPDATE Readings SET readings = :newReadings WHERE Patientid = :patientId and Visitdate= :visitdate and Visittime= :visittime")
     fun updateReadings(patientId: String, visitdate: String, visittime:String, newReadings: String)
    @Query("Select readings from Readings where Patientid=:patid and Visitdate=:visitdate and Visittime=:visittime")
    fun getJSON(patid: String?,visitdate: String?,visittime: String?):List<String>
    @Query("Select readings from Readings where Patientid=:patid and Visitdate=:visitdate")
    fun getJSON1(patid: String?,visitdate: String?):List<String>
   /** @Query("SELECT MAX(`VisitTime`) FROM Readings")*/
    //fun getLastReading(): Int?
 /**  @Query("SELECT avg(Bioimpedance) as avgBioimp FROM Readings WHERE Patientid = :patientId AND VisitDate = :visitDate AND VisitTime = :visitTime")
    fun getTimestampAndBioimpedance(patientId: String, visitDate: String, visitTime: String): List<TimestampAndBioimpedance>
    @Query("SELECT avg(Phase) as avgBiophase FROM Readings WHERE Patientid = :patientId AND VisitDate = :visitDate AND VisitTime = :visitTime")
    fun getTimestampAndPhase(patientId: String, visitDate: String, visitTime: String): List<PhaseAndBioimpedance>

    // Create a data class to hold the result
   data class TimestampAndBioimpedance(
        @ColumnInfo(name = "avgBioimp")
        val bioimp: String
    )
    data class PhaseAndBioimpedance(
        @ColumnInfo(name = "avgBiophase")
        val phase: String
    )*/
 // In ReadingsDao
 @Query("SELECT Visitdate FROM readings WHERE Patientid = :patid ORDER BY Visitdate DESC LIMIT 1")
 fun getMostRecentDateReadings(patid: String): List<String>
    @Query("SELECT COUNT(*) FROM readings WHERE Patientid = :patid AND Visitdate = :visitDate")
    fun checkIfDateExists(patid: String, visitDate: String): Int

}