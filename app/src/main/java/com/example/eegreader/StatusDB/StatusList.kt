package com.example.eegreader.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
//Schema of the table in the status database
@Entity(tableName="status")
data class StatusList(
    //Creating patient id and visit number column
    @ColumnInfo(name = "Patient Id")
    var patid: String,
    @ColumnInfo(name = "Visit")
    var visit: Int,

    //Creating time column
    @ColumnInfo(name = "Visit Time")
    var visittime: String,

    //Creating date column
    @ColumnInfo(name = "Visit Date")
    var visitdate: String,


    ){

    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}