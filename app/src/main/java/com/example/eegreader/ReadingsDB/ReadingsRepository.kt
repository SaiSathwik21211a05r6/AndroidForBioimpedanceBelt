package com.example.eegreader.ReadingsDB

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//Creating the repository for the status database
class ReadingsRepository(private val db: ReadingsDatabase) {

    //Function for adding new visit
    suspend fun insertReadings(items: ReadingsList)=GlobalScope.launch{
        Log.d("DatabaseInsert", items.toString())
        db.getReadingsDao().insertReadings(items)}

    //Function for deleting a visit
    suspend fun deleteReadings(items: ReadingsList)=db.getReadingsDao().deleteReadings(items)

    //Function to get the list of visits
    fun getthelistofReadings()=db.getReadingsDao().getthelistofReadings()
    fun getMostRecentDateReadings(patid: String)=db.getReadingsDao().getMostRecentDateReadings(patid)
    fun checkIfDateExists(patid: String,visitdate:String)=db.getReadingsDao().checkIfDateExists(patid,visitdate)
    fun getJSON(patid: String?,visitdate: String?,visittime: String?): List<String> {
        val result = db.getReadingsDao().getJSON(patid,visitdate,visittime)
        Log.d("DatabaseQuery", "SQL: Select readings from Readings where Patientid=$patid$visitdate$visittime")
        Log.d("DatabaseQuery", "Result: $result")
        return result
    }
    fun getJSON1(patid: String?,visitdate: String?): List<String> {
        val result = db.getReadingsDao().getJSON1(patid,visitdate)
        Log.d("DatabaseQuery", "SQL: Select readings from Readings where Patientid=$patid$visitdate")
        Log.d("DatabaseQuery", "Result: $result")
        return result
    }
    fun updateReadings(patientId: String, visitdate:String, visittime: String, newReadings: String)= GlobalScope.launch()
        {     Log.d("DatabaseUpdate", patientId.toString()+newReadings.toString())
            Log.d("DatabaseQuery", "SQL: Update readings from Readings where Patientid=$patientId$newReadings")

            db.getReadingsDao().updateReadings(patientId, visitdate, visittime, newReadings)
        }
    }

