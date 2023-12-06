package com.example.eegreader.StatusDB

//Imports necessary for the view model class
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eegreader.database.StatusList
import com.example.eegreader.database.StatusRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//interacting with status database
//Creating a view model class
class StatusViewModal(private val repository: StatusRepository):ViewModel() {

    //Inserted groceries
    fun insertstatus(items: StatusList)=GlobalScope.launch{
        Log.d("DatabaseInsert", items.toString())
        repository.insertStatus(items)
    }

    //Deleted groceries
    fun deleteitem(items:StatusList)=GlobalScope.launch{
        repository.deleteitem(items)
    }

    //List of groceries
    fun getthelistofstatus()=repository.getthelistofstatus()
    // Call getLastVisitNumber from your ViewModel or any other relevant part of your code


    companion object {
        fun insertitems(items: StatusList) {

        }
    }


}