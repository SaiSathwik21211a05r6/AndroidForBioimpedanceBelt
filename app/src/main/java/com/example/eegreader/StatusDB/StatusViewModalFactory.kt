package com.example.eegreader.database


//Making necessary imports
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.StatusDB.StatusViewModal
//implementing lifecycle for status database
//Creating view modal factory class
class StatusViewModalFactory(
    private val repository: StatusRepository):
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel>
            create(modelClass: Class<T>): T {
        return StatusViewModal(repository) as T
    }
}