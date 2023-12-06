package com.example.eegreader.ReadingsDB


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Schema of the table in the status database
@Entity(tableName="Readings")
data class ReadingsList(
    //Creating patient id and visit number column
    @ColumnInfo(name = "Patientid")
    var patid: String?=null,
    @ColumnInfo(name = "Visitdate")
    var visitdate: String?=null,
    @ColumnInfo(name = "Visittime")
    var visittime: String?=null,

    //Creating date column
   /** @ColumnInfo(name = "VisitDate")
    var visitdate: String,
    @ColumnInfo(name = "VisitTime")
    var visittime: String,*/
    @ColumnInfo(name = "readings")
    var readings: String? =null,



    //Creating time column



    ){

    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}