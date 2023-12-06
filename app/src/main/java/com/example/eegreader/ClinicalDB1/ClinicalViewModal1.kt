package com.example.eegreader.ClinicalDB1

import com.example.eegreader.ClinicalDB.ClinicalList



//Imports necessary for the view model class
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//interacting with status database
//Creating a view model class
class ClinicalViewModal1(private val repository: ClinicalRepository1):ViewModel() {

    //Inserted groceries
    fun insertData(items: ClinicalList1?)=GlobalScope.launch{
        Log.d("DatabaseInsert", items.toString())
        if (items != null) {
            repository.insertData(items)
        }
    }

    //Deleted groceries
    fun deleteData(items:ClinicalList1)=GlobalScope.launch{
        repository.deleteData(items)
    }

    //List of groceries
    fun getthelistofdata()=repository.getthelistofdata()
    // Call getLastVisitNumber from your ViewModel or any other relevant part of your code
    fun updateReadings(patientId: String, newReadings: String) =GlobalScope.launch()
    {
        Log.d("DatabaseInsertforclinical", newReadings.toString())
        repository.updateReadings(patientId, newReadings)
    }
    fun getJSON(patid: String) =
        repository.getJSON(patid)

    companion object {
        fun insertData(items: ClinicalList) {

        }
    }


}