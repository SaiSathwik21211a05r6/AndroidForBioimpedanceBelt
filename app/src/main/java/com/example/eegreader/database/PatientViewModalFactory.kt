package com.example.eegreader.database


//Making necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
//creating lifecycle for patient database
//Creating view modal factory class
class PatientViewModalFactory(
    private val repository: PatientRepository):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel>
            create(modelClass: Class<T>): T {
        return PatientViewModal(repository) as T
    }
}