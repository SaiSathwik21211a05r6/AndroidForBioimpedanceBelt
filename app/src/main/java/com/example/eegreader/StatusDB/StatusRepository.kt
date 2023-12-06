package com.example.eegreader.database

//Creating the repository for the status database
class StatusRepository(private val db:StatusDatabase) {

    //Function for adding new visit
    suspend fun insertStatus(items: StatusList)=db.getStatusDao().insertstatus(items)

    //Function for deleting a visit
    suspend fun deleteitem(items: StatusList)=db.getStatusDao().deletestatus(items)

    //Function to get the list of visits
    fun getthelistofstatus()=db.getStatusDao().getthelistofstatus()

}