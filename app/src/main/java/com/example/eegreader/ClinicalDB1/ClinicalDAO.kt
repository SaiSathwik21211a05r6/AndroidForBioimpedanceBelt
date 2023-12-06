package com.example.eegreader.ClinicalDB1

import com.example.eegreader.ClinicalDB.ClinicalList


import com.example.eegreader.database.StatusList



//Making necessary imports
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//Creating data access object for status database
@Dao
interface ClinicalDao1 {

    //Inserting new status into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(item: ClinicalList1)

    //Removing status from the database
    @Delete
    suspend fun deleteData(item: ClinicalList1)

    //Creating a query
    @Query("Select * FROM clinicaldata1")
    fun getthelistofdata():LiveData<List<ClinicalList1>>
    @Query("UPDATE ClinicalData1 SET Clinical = :newReadings WHERE Patientid = :patientId")
    fun updateReadings(patientId: String, newReadings: String)
    @Query("Select clinical from ClinicalData1 where PatientId=:patid")
    fun getJSON(patid:String):List<String>
}