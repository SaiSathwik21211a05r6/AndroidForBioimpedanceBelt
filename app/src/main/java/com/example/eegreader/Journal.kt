package com.example.eegreader

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.ClinicalDB1.ClinicalDatabase1
import com.example.eegreader.ClinicalDB1.ClinicalRepository1
import com.example.eegreader.ClinicalDB1.ClinicalViewModal1
import com.example.eegreader.ClinicalDB1.ClinicalViewModalFactory1
import com.example.eegreader.RecyclerView.patid
import org.json.JSONArray
import org.json.JSONObject

class Journal: AppCompatActivity() {
    lateinit var clinicalViewModal: ClinicalViewModal1
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("launching", "registration")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clinical)
        val clinicalDatabase = ClinicalDatabase1.invoke(this)
        val clinicalRepository = ClinicalRepository1(clinicalDatabase)
        val factory = ClinicalViewModalFactory1(clinicalRepository)
        clinicalViewModal =
            ViewModelProvider(this, factory as ViewModelProvider.Factory).get<ClinicalViewModal1>(
                ClinicalViewModal1::class.java
            )
val pida=findViewById<TextView>(R.id.patid)
        val datea=findViewById<TextView>(R.id.vdate)
        val timea=findViewById<TextView>(R.id.vtime)
        val date=intent.getStringExtra("date")
        val time=intent.getStringExtra("time")

        datea.setText(date)
        timea.setText(time)
        val height = findViewById<TextView>(R.id.height)
        val weight = findViewById<TextView>(R.id.weight)
        val bmi = findViewById<TextView>(R.id.bmi)
        val diabetes = findViewById<TextView>(R.id.diabetes)
        val htn = findViewById<TextView>(R.id.htn)
        val asthma = findViewById<TextView>(R.id.asthma)
        val smoking = findViewById<TextView>(R.id.smoking)
        val alcohol = findViewById<TextView>(R.id.alcohol)
        val mi = findViewById<TextView>(R.id.mi)
        val cva = findViewById<TextView>(R.id.cva)
        val stent = findViewById<TextView>(R.id.stent)
        val cabg = findViewById<TextView>(R.id.cabg)

        val patid = intent.getStringExtra("patient id")
        pida.setText(patid)
        clinicalViewModal.getthelistofdata().observe (this,{
                readingsList ->
            // Update your UI with the new data
            if (readingsList != null) {
                for (reading in readingsList) {
                    Log.d("Readings", reading.toString())
                }
            } else {
                Log.d("Readings", "List is null")
            }
        })

        val t = Thread {
            val jsonObjectStringList = clinicalViewModal.getJSON(patid.toString())
Log.d("clinical",clinicalViewModal.getthelistofdata().toString())
            runOnUiThread {
                Log.d("lifecylce","vd")
                clinicalViewModal.getthelistofdata().observe(this, { readingsList ->
                    // Update your UI or log the contents of readingsList
                    Log.d("readingslist", readingsList.toString())
                    if (readingsList != null) {
                        for (reading in readingsList) {
                            Log.d("Readings", reading.toString())
                        }
                    } else {
                        Log.d("Readings", "List is null")
                    }
                })}
            // Ensure the list is not empty
            if (jsonObjectStringList.isNotEmpty()) {


                for (jsonObjectString in jsonObjectStringList) {
                    try {
                        // Parse the string into a JSONObject
                        val jsonObject = JSONObject(jsonObjectString)

                        // Iterate over keys and log both keys and values
                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = jsonObject.get(key)
var j=0
                            // Log key
                            Log.d("Key", key)

                            if (value is JSONArray) {
                                // If the value is a JSON array, calculate the average of values at index 2
                                for (i in 0 until value.length()) {
                                    val element = value.getString(i)
                                    Log.d("elements",element)
                                runOnUiThread {
                                        if(i==0) height.setText(element+"cm")
                                        Log.d("elements",element)
                                        if (i == 1) weight.setText(element+"kg")
                                        if (i == 2) bmi.setText(element)
                                        if (i == 3) {if(element=="1")
                                            diabetes.setText("yes")
                                        else
                                            diabetes.setText("no")}
                                    if (i == 4) {if(element=="1")
                                        htn.setText("yes")
                                    else
                                        htn.setText("no")}
                                    if (i == 5) {if(element=="1")
                                        asthma.setText("yes")
                                    else
                                        asthma.setText("no")}
                                    if (i == 6) {if(element=="1")
                                        smoking.setText("yes")
                                    else
                                        smoking.setText("no")}
                                    if (i == 7) {if(element=="1")
                                        alcohol.setText("yes")
                                    else
                                        alcohol.setText("no")}
                                    if (i == 8) {if(element=="1")
                                        mi.setText("yes")
                                    else
                                        mi.setText("no")}
                                    if (i == 9) {if(element=="1")
                                        cva.setText("yes")
                                    else
                                        cva.setText("no")}
                                    if (i == 10) {if(element=="1")
                                        stent.setText("yes")
                                    else
                                        stent.setText("no")}
                                    if (i ==11) {if(element=="1")
                                        cabg.setText("yes")
                                    else
                                        cabg.setText("no")}
                                }


                                }
                                j+=1

                            }
                        }
                    } catch (e: Exception) {
                        // Handle JSON parsing exception
                        Log.e("JSON Parsing", "Error parsing JSON object", e)
                    }
                }
            }
        }
        t.start()
    }
}
