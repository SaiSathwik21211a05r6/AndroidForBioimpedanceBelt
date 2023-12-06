package com.example.eegreader.ClinicalDB

import com.example.eegreader.database.StatusList



//Making necessary imports
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//Creating data access object for status database
@Dao
interface ClinicalDao {

    //Inserting new status into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(item: ClinicalList)

    //Removing status from the database
    @Delete
    suspend fun deleteData(item: ClinicalList)

    //Creating a query
    @Query("Select * FROM clinicaldata")
    fun getthelistofdata():LiveData<List<ClinicalList>>
    @Query("UPDATE clinicaldata SET Clinical = :newReadings WHERE Patientid = :patientId")
    fun updateReadings(patientId: String, newReadings: String)

}