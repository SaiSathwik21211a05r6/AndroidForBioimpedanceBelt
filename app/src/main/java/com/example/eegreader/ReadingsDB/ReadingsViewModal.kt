package com.example.eegreader.ReadingsDB

//Imports necessary for the view model class
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//interacting with status database
//Creating a view model class
class ReadingsViewModal(private val repository: ReadingsRepository):ViewModel() {

    //Inserted groceries
    fun insertReadings(items: ReadingsList) = GlobalScope.launch {
        Log.d("DatabaseInsert", items.toString())
        repository.insertReadings(items)
    }

    //Deleted groceries
    fun deleteitem(items: ReadingsList) = GlobalScope.launch {
        repository.deleteReadings(items)
    }

    //List of groceries
    fun getthelistofReadings() = repository.getthelistofReadings()
    fun getMostRecentDateReadings(patid:String) = repository.getMostRecentDateReadings(patid)
    fun checkIfDateExists(patid: String, visitDate: String): Boolean {
        val count = repository.checkIfDateExists(patid, visitDate)
        return count > 0
    }
    // Call getLastVisitNumber from your ViewModel or any other relevant part of your code
    fun getJSON(patid: String?,visitdate: String?,visittime: String?) =
            repository.getJSON(patid, visitdate, visittime)
    fun getJSON1(patid: String?,visitdate: String?) =
        repository.getJSON1(patid, visitdate)
 fun updateReadings(patientId: String, visitdate:String, visittime:String, newReadings: String) =GlobalScope.launch()
     {
         Log.d("DatabaseInsertviewmodal", patientId.toString()+newReadings.toString())
         repository.updateReadings(patientId, visitdate, visittime, newReadings)
     }
    companion object {
        fun insertReadings(items: ReadingsList) {

        }
    }


}