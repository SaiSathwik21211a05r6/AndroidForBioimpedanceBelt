package com.example.eegreader.ClinicalDB

import android.util.Log


//Creating the repository for the status database
class ClinicalRepository(private val db: ClinicalDatabase) {

    //Function for adding new visit
    suspend fun insertData(items: ClinicalList)={
        Log.d("clinical", items.toString())
        db.getClinicalDao().insertData(items)}

    //Function for deleting a visit
    suspend fun deleteData(items: ClinicalList)=db.getClinicalDao().deleteData(items)

    //Function to get the list of visits
    fun getthelistofdata()=db.getClinicalDao().getthelistofdata()
   fun updateReadings(patientId: String, newReadings: String)=db.getClinicalDao().updateReadings(patientId,newReadings)
}