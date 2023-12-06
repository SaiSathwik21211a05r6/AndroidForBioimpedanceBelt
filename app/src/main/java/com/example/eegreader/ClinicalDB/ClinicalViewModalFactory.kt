package com.example.eegreader.ClinicalDB

import com.example.eegreader.database.StatusRepository



//Making necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.StatusDB.StatusViewModal
//implementing lifecycle for status database
//Creating view modal factory class
class ClinicalViewModalFactory(
    private val repository: ClinicalRepository
):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel>
            create(modelClass: Class<T>): T {
        return ClinicalViewModal(repository) as T
    }
}