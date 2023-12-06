package com.example.eegreader.database


//Making necessary imports
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

//Creating data access object for patient database
@Dao
interface EEGPatientDao {

    //Inserting new patient into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertitem(item: com.example.eegreader.database.Patientlist)

    //Removing patient from the database
    @Delete
    suspend fun deleteitem(item: com.example.eegreader.database.Patientlist)

    //Creating a query
    @Query("Select * FROM grocery_items")
    fun getthelistofgrocery():LiveData<List<Patientlist>>
}