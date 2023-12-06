package com.example.eegreader.database

//Creating the repository for the patient database
class PatientRepository(private val db:PatientDatabase) {

    //Function for adding new patient
    suspend fun insertitem(items:Patientlist)=db.getEEGPatientDao().insertitem(items)

    //Function for deleting a patient
    suspend fun deleteitem(items:Patientlist)=db.getEEGPatientDao().deleteitem(items)

    //Function to get the list of patients
    fun getthelistofpatients()=db.getEEGPatientDao().getthelistofgrocery()
}