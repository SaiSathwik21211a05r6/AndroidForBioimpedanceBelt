package com.example.eegreader.RecyclerView

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//Possible use for terminal screen in advanced versions of the app
class ReadingsViewModel : ViewModel() {
    val dataLiveData = MutableLiveData<MutableList<String>>()

    // Method to set new data in the LiveData
    fun setData(newDataList: MutableList<String>) {
        dataLiveData.value = newDataList
    }

    // Method to get the current data from the LiveData
    fun getData(): MutableList<String>? {
        return dataLiveData.value
    }

    // Method to add a single data item to the current data
    fun addDataItem(newDataItem: String) {
        val currentData = dataLiveData.value ?: mutableListOf()
        currentData.add(newDataItem)
        dataLiveData.value = currentData
    }

    // Method to clear the data in the LiveData
    fun clearData() {
        dataLiveData.value = mutableListOf()
    }
}
