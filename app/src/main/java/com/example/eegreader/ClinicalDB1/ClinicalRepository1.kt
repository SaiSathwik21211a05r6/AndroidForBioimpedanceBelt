package com.example.eegreader.ClinicalDB1

import com.example.eegreader.ClinicalDB.ClinicalDatabase
import com.example.eegreader.ClinicalDB.ClinicalList


import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//Creating the repository for the status database
class ClinicalRepository1(private val db: ClinicalDatabase1) {

    //Function for adding new visit
    suspend fun insertData(items: ClinicalList1)=GlobalScope.launch{
        Log.d("clinical", items.toString())
        db.getClinicalDao1().insertData(items)}

    //Function for deleting a visit
    suspend fun deleteData(items: ClinicalList1)=db.getClinicalDao1().deleteData(items)

    //Function to get the list of visits
    fun getthelistofdata()=db.getClinicalDao1().getthelistofdata()
    fun updateReadings(patientId: String, newReadings: String)=db.getClinicalDao1().updateReadings(patientId,newReadings)
    fun getJSON(patid:String): List<String> {
        val result = db.getClinicalDao1().getJSON(patid)
        Log.d("DatabaseQuery", "SQL: Select readings from Readings where Patientid=")
        Log.d("DatabaseQuery", "Result: $result")
        return result
    }


}