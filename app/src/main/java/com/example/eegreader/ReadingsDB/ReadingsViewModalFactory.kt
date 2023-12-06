package com.example.eegreader.ReadingsDB


//Making necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.ReadingsDB.ReadingsRepository
import com.example.eegreader.ReadingsDB.ReadingsViewModal
import com.example.eegreader.StatusDB.StatusViewModal
//implementing lifecycle for status database
//Creating view modal factory class
class ReadingsViewModalFactory(
    private val repository: ReadingsRepository
):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel>
            create(modelClass: Class<T>): T {
        return ReadingsViewModal(repository) as T
    }
}