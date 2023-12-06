package com.example.eegreader.ClinicalDB1

import com.example.eegreader.ClinicalDB.ClinicalRepository
import com.example.eegreader.ClinicalDB.ClinicalViewModal


import com.example.eegreader.database.StatusRepository



//Making necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.StatusDB.StatusViewModal
//implementing lifecycle for status database
//Creating view modal factory class
class ClinicalViewModalFactory1(
    private val repository: ClinicalRepository1
):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel>
            create(modelClass: Class<T>): T {
        return ClinicalViewModal1(repository) as T
    }
}