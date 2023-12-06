package com.example.eegreader.ClinicalDB


//Imports necessary for the view model class
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eegreader.database.StatusList
import com.example.eegreader.database.StatusRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//interacting with status database
//Creating a view model class
class ClinicalViewModal(private val repository: ClinicalRepository):ViewModel() {

    //Inserted groceries
    fun insertData(items: ClinicalList)=GlobalScope.launch{
        Log.d("DatabaseInsert", items.toString())
        repository.insertData(items)
    }

    //Deleted groceries
    fun deleteData(items:ClinicalList)=GlobalScope.launch{
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

    companion object {
        fun insertData(items: ClinicalList) {

        }
    }


}