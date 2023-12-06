package com.example.eegreader.ClinicalDB



import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Schema of the table in the status database
@Entity(tableName="ClinicalData")
data class ClinicalList(
    //Creating patient id and visit number column



    @ColumnInfo(name = "PatientId")
    var patid: String?=null,
    @ColumnInfo(name = "Clinical")
    var clinical: String?=null,





    ){

    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}