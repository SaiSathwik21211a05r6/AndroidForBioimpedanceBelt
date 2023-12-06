package com.example.eegreader.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

//schema/structure of patient table
@Entity(tableName="grocery_items")
data class Patientlist(
    //Creating Name column
    @ColumnInfo(name = "patientId")
    var patientId: String?=null,
    @ColumnInfo(name = "patientName")
    var patientName: String,
    @ColumnInfo(name = "Gender")
    var patientGender: String,
    //Creating age column
    @ColumnInfo(name = "PatientAge")
    var patientAge: Int,

    //Creating gender and blood group column

    @ColumnInfo(name = "BloodGroup")
    val patientBg: String,
    @ColumnInfo(name = "Race")
    val race: String?=null,
    @ColumnInfo(name = "DOR")
    var dor: String?=null,
    @ColumnInfo(name = "Height")
    var height: String?=null,
    @ColumnInfo(name = "Weight")
    var weight: String?=null,
    @ColumnInfo(name = "BMI")
    var bmi: String?=null,
    @ColumnInfo(name = "Diabetes")
    var diabetes: Int?=0,
    @ColumnInfo(name = "HTN")
    var htn: Int?=0,
    @ColumnInfo(name = "Asthma")
    var asthma: Int?=0,
    @ColumnInfo(name = "Smoking")
    var smoking: Int?=0,
    @ColumnInfo(name = "Alcohol")
    var alcohol: Int?=0,
    @ColumnInfo(name = "MI")
    var mi: Int?=0,
    @ColumnInfo(name = "CVA")
    var cva: Int?=0,
    @ColumnInfo(name = "Stent")
    var stent: Int?=0,
    @ColumnInfo(name = "CABG")
    var cabg: Int?=0,



    ){

    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}