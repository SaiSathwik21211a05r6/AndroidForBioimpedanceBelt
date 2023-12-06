package com.example.eegreader.database

//Imports necessary for the view model class
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//interacting with patient database
//Creating a view model class
class PatientViewModal(private val repository: PatientRepository):ViewModel() {

    //Inserted patients
    fun insertitem(items:Patientlist)=GlobalScope.launch{
        repository.insertitem(items)
    }

    //Deleted patients
    fun deleteitem(items:Patientlist)=GlobalScope.launch{
        repository.deleteitem(items)
    }

    //List of patients
    fun getthelistofpatients()=repository.getthelistofpatients()
}