package com.example.eegreader.database


//Making necessary imports
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//Creating data access object for status database
@Dao
interface PatientStatusDao {

    //Inserting new status into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertstatus(item: StatusList)

    //Removing status from the database
    @Delete
    suspend fun deletestatus(item: StatusList)

    //Creating a query
    @Query("Select * FROM status")
    fun getthelistofstatus():LiveData<List<StatusList>>
    @Query("SELECT MAX(visit) FROM status")
    fun getLastVisitNumber(): Int?
}