package com.example.eegreader

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.eegreader.ReadingsDB.ReadingsDatabase.Companion.invoke
import com.example.eegreader.ReadingsDB.ReadingsRepository
import com.example.eegreader.ReadingsDB.ReadingsViewModal
import com.example.eegreader.ReadingsDB.ReadingsViewModalFactory
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.collection.LLRBNode
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

val frequencyEntry = mutableListOf<Entry>()
val phaseEntry = mutableListOf<Entry>()
private var pid: TextView? = null
private var vid: TextView? = null
private var vtime: TextView? = null
private var vdate: TextView? = null
var number32:List<String>?=null
var number31:List<String>?=null
var lineChart:LineChart?= null
var lineChart1:LineChart?= null
class Acknowledgement: AppCompatActivity() {
    lateinit var readingsViewModal:ReadingsViewModal
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("launching", "registration")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acknowledgment)
        val freqstart=intent.getStringExtra("freqstart")
        val freqend=intent.getStringExtra("freqend")
        val patid=intent.getStringExtra("patid")
        var visitdate=intent.getStringExtra("date")
         lineChart = findViewById<LineChart>(R.id.lineChart1)
        lineChart?.getDescription()?.setText("Timed Data Chart")
        lineChart1 = findViewById<LineChart>(R.id.lineChart2)
        val lstreading=findViewById<TextView>(R.id.lstreading)
        val lstday=findViewById<TextView>(R.id.lstday)
        val lstmnth=findViewById<TextView>(R.id.lastmnth)
        val avg=findViewById<TextView>(R.id.avg)
        lstday.setText("NA")
        lstmnth.setText("NA")
        val frequencyeditb =  findViewById<Spinner>(R.id.frequencyedit)
        if (frequencyeditb != null) {
            val startFrequency = freqstart?.toInt()
            val endFrequency = freqend?.toInt()

            // Generate a list of consecutive values between startFrequency and endFrequency
            val frequencyList = (endFrequency?.let { startFrequency?.rangeTo(it) })?.map { it.toString() }

            val adapter = ArrayAdapter.createFromResource(this, R.array.frequencyselection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            frequencyeditb.adapter = adapter
            val defaultFrequencyPosition = adapter.getPosition("Please Select")
            frequencyeditb.setSelection(defaultFrequencyPosition)

            // ... (rest of the code)
        } else {

        }
        val visitid = intent.getStringExtra("visit id")
        //  dbHelper = new DatabaseHelper(this);
        //  dbHelper = new DatabaseHelper(this);
        val visitdate1 = intent.getStringExtra("date")
        var visittime = intent.getStringExtra("time")
        pid = findViewById(R.id.patid)
        vid = findViewById(R.id.vid)
        vtime = findViewById(R.id.vtime)
        vdate = findViewById(R.id.vdate)
        pid?.setText(patid)
        vid?.setText(visitid)
        vtime?.setText(visittime)
        vdate?.setText(visitdate1)

        val dateFormat1 = SimpleDateFormat("hh:mm", Locale.getDefault())
var visittime1:StringBuilder= StringBuilder(visittime)
      visittime1.insert(2,":")
        val readingsDatabase = invoke(this)
        val readingsRepository = ReadingsRepository(readingsDatabase)
        val factory = ReadingsViewModalFactory(readingsRepository)
        readingsViewModal = ViewModelProvider(this, factory as ViewModelProvider.Factory).get<ReadingsViewModal>(
            ReadingsViewModal::class.java
        )
        readingsViewModal.getthelistofReadings().observeForever {
           readingsList ->
            // Update your UI with the new data
            if (readingsList != null) {
                for (reading in readingsList) {
                    Log.d("Readings", reading.toString())
                }
            } else {
                Log.d("Readings", "List is null")
            }
        }

        val t = Thread {
            Log.d("patid", patid.toString())
            Log.d("readings1 are", readingsViewModal.getthelistofReadings().toString()
            )
            runOnUiThread {
            readingsViewModal.getthelistofReadings().observe(this, { readingsList ->
                // Update your UI or log the contents of readingsList
                if (readingsList != null) {
                    for (reading in readingsList) {
                        Log.d("Readings", reading.toString())
                    }
                } else {
                    Log.d("Readings", "List is null")
                }
            })}
            //Log.d("readings are", readingsViewModal.getJSON("Sathwik").toString())
            val jsonObjectStringList = readingsViewModal.getJSON(patid,visitdate,visittime1.toString())

            // Ensure the list is not empty
            if (jsonObjectStringList.isNotEmpty()) {
                var sum = 0.0
                var count = 0

                for (jsonObjectString in jsonObjectStringList) {
                    try {
                        // Parse the string into a JSONObject
                        val jsonObject = JSONObject(jsonObjectString)

                        // Iterate over keys and log both keys and values
                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = jsonObject.get(key)

                            // Log key
                            Log.d("Key", key)

                            if (value is JSONArray) {
                                // If the value is a JSON array, calculate the average of values at index 2
                                for (i in 0 until value.length()) {
                                    val element = value.getString(i)

for(j in 0 until element.length)
                                    // Check if the element is a number and at index 2
{
    if (j == 2 && element.toDoubleOrNull() != null) {
                                        sum += element.toDouble()
                                        count++
                                    }}

                                    // Log element
                                    Log.d("Value", element)
                                }
                            } else {
                                val number3=value.toString().split(",")
                                number31=number3[2].split("\"")
                                val number311=number3[2].split("\"")
                                Log.d("number31", number31!![1].toString())
                                count++;
                               sum=sum + number31!![1].toString().toDouble()!!
                                runOnUiThread {
                                    updateChartWithData1(number311!![1].toString().toFloat())
                                }
                                // Log value if it's not an array
                                Log.d("Value", value.toString())
                            }
                        }
                    } catch (e: Exception) {
                        // Handle JSON parsing exception
                        Log.e("JSON Parsing", "Error parsing JSON object", e)
                    }
                }

                if (count > 0) {
                    val average = sum / count
                    Log.d("Average at Index 2", average.toString())
                    runOnUiThread {    lstreading.setText(average.toString())
                    avg.setText(average.toString())}
                } else {
                    Log.d("Average at Index 2", "No valid values found")
                }
            } else {
                Log.d("Readings", "JSON object list is empty")
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(visitdate)

            // Create a Calendar instance and set it to the parsed date
            val calendar = Calendar.getInstance()
            calendar.time = date

            // Subtract one day
            calendar.add(Calendar.DAY_OF_YEAR, -2)
            calendar.add(Calendar.DAY_OF_YEAR, -1) // Start with the day before the current date

            var previousDate: String? = null

            while (previousDate == null) {
                // Format the date to match your database format
                val formattedDate = dateFormat.format(calendar.time)

                // Check if the formatted date exists in the database
                val dateExists = patid?.let { readingsViewModal.checkIfDateExists(it, formattedDate) } ?: false

                if (dateExists) {
                    // If the date exists in the database, set it as the previousDate and exit the loop
                    previousDate = formattedDate
                } else {
                    // If the date doesn't exist, move to the previous day
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                }
            }

            Log.d("visitdate fir orev", previousDate)
            // Format the result back to a string
         //   val previousDate = dateFormat.format(calendar.time)
         //   val previousDate= patid?.let { readingsViewModal.getMostRecentDateReadings(it) }
            Log.d("visitdate fir orev",previousDate.toString().removeSurrounding("[", "]"))
            val jsonObjectStringList1 = readingsViewModal.getJSON1(patid,previousDate.toString().removeSurrounding("[", "]"))

            // Ensure the list is not empty
            if (jsonObjectStringList1.isNotEmpty()) {
                var sum = 0.0
                var count = 0

                for (jsonObjectString in jsonObjectStringList1) {
                    try {
                        // Parse the string into a JSONObject
                        val jsonObject = JSONObject(jsonObjectString)

                        // Iterate over keys and log both keys and values
                        val keys = jsonObject.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val value = jsonObject.get(key)

                            // Log key
                            Log.d("Key", key)

                            if (value is JSONArray) {
                                // If the value is a JSON array, calculate the average of values at index 2
                                for (i in 0 until value.length()) {
                                    val element = value.getString(i)

                                    for(j in 0 until element.length)
                                    // Check if the element is a number and at index 2
                                    {
                                        if (j == 2 && element.toDoubleOrNull() != null) {
                                            sum += element.toDouble()
                                            count++
                                        }}

                                    // Log element
                                    Log.d("Value", element)
                                }
                            } else {
                                val number3=value.toString().split(",")
                                number32=number3[2].split("\"")
                                Log.d("number32", number32!![1].toString())
                                count++;

                                sum=(((number32!![1].toDouble()- number31!![1].toDouble())/ number31!![1].toDouble())*1000.0)?.toString()?.toDouble()!!
                              Log.d("sum", sum.toString())
                                Log.d("sumno31", number31.toString())
                                Log.d("sumno32", number32.toString())
                                runOnUiThread {
                                   updateChartWithData(number32!![1]?.toString()+","+number31!![1]?.toString())
                                }
                                // Log value if it's not an array
                                Log.d("Value", value.toString())
                            }
                        }
                    } catch (e: Exception) {
                        // Handle JSON parsing exception
                        Log.e("JSON Parsing", "Error parsing JSON object", e)
                    }
                }

                if (count > 0) {
                    val average = sum
                    Log.d("Average at Index 2", average.toString())

                    runOnUiThread {
                        if(average<0.0)
                        {
                            lstday.setText(Math.abs(average.roundToInt()).toString()+"%")
                           lstday.setTextColor(Color.RED)
                        }
                        else
                        {
                            lstday.setText(Math.abs(average.roundToInt()).toString()+"%")
                            lstday.setTextColor(Color.GREEN)
                        }
                        }
                } else {
                    Log.d("Average at Index 2", "No valid values found")
                }
            } else {
                Log.d("Readings", "JSON object list is empty")
            }
        }

        t.start()



    }
    fun convertStringArray(stringArray: String): Array<String>? {
        return try {
            // Assuming the stringArray is in the format [Ljava.lang.String;@123456
            if (stringArray.startsWith("[Ljava.lang.String;@")) {
                val hashcode = stringArray.substring("[Ljava.lang.String;@".length)
                // Simulate the hashcode to get an array of strings
                val simulatedArray = arrayOf("frequency11", "number3", "number4", "p1", "p2")
                Log.d("Simulated Array", simulatedArray.joinToString())
                return simulatedArray
            } else {
                // If the format is unexpected, log an error and return null
                Log.e("Conversion Error", "Unexpected format: $stringArray")
                null
            }
        } catch (e: Exception) {
            Log.e("Conversion Error", "Error converting string array", e)
            null
        }
    }
    var count=0
    private var lineData:LineData?=null
    private var lineData1:LineData?=null
    fun updateChartWithData(rowData: String) {
        var split=rowData?.split(",")
    var yValueFrequency: Float = split!![0].toFloat()  // Assuming frequency data is at index 2
        var yValueFrequency1: Float = split!![1].toFloat()
        // Assuming phase data is at index 3

    android.util.Log.d("line", "iterate")
if(yValueFrequency<90.0) {
    frequencyEntry.add(Entry(count.toFloat(), yValueFrequency))
    frequencyEntry.add(Entry((count+1.0).toFloat(), yValueFrequency1))
}
        else
            phaseEntry.add(Entry(count.toFloat(),yValueFrequency))
 //   phaseEntry.add(com.github.mikephil.charting.data .Entry(count.toFloat(), yValuePhase))
    count = count + 2






//    YourXAxisFormatter.updateValues(frequency11, p1, p2);
// linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());

//    YourXAxisFormatter.updateValues(frequency11, p1, p2);
// linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());
var xFormatter: ValueFormatter = object : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        // Format the Y-axis labels as needed
        return String.format("%.1f", value)
    }
}

        lineChart1?.getXAxis()?.setLabelCount(3, true)
        lineChart1?.getXAxis()?.setPosition(XAxis.XAxisPosition.BOTTOM)
//  lineChart1.getXAxis().setLabelCount(3,true);
//  lineChart1.getXAxis().setLabelCount(3,true);
        lineChart1?.getXAxis()?.setValueFormatter(
            xFormatter)
//  lineChart1.getXAxis().setLabelCount(3,true);
//  lineChart1.getXAxis().setLabelCount(3,true);

//   linechart1.getXAxis().setValueFormatter(
//xFormatter);
//linechart.getXAxis().set// Implement your X-axis formatting
// linechart.getAxisLeft().setAxisMinimum(150000f);
// linechart.getAxisLeft().setAxisMimum(150000f);
//  linechart.getAxisLeft().setAxisMinimum(400f);
//   linechart1.getXAxis().setValueFormatter(
//xFormatter);
//linechart.getXAxis().set// Implement your X-axis formatting
// linechart.getAxisLeft().setAxisMinimum(150000f);
// linechart.getAxisLeft().setAxisMimum(150000f);
//  linechart.getAxisLeft().setAxisMinimum(400f);

        lineChart1?.getAxisRight()?.setEnabled(false)
        lineChart1?.getXAxis()?.setDrawGridLines(false)
//  lineChart1.setData(lineData1);
// linechart1.getDescription().setText("Timed Phase Chart");


//   linechart1.getAxisLeft().setAxisMinimum(-80f);

//      linechart1.getAxisRight().setEnabled(false);
// Create LineDataSet with the entry lists
//  lineChart1.setData(lineData1);
// linechart1.getDescription().setText("Timed Phase Chart");


//   linechart1.getAxisLeft().setAxisMinimum(-80f);

//      linechart1.getAxisRight().setEnabled(false);
// Create LineDataSet with the entry lists
var dataSetFrequency = LineDataSet(frequencyEntry, "Frequency Data")
dataSetFrequency.setColor(android.graphics.Color.BLUE) // Set line color


var dataSetPhase = LineDataSet(phaseEntry, "Phase Data")
dataSetPhase.setColor(android.graphics.Color.RED) // Set line color


// Refresh the chart
// Refresh the chart

// Create LineData object with our datasets
// Create LineData object with our datasets

        lineData1 = LineData(dataSetPhase)
android.util.Log.d("linedata", lineData?.getDataSets().toString())
// Set data to the chart
// Set data to the chart

        lineChart1?.setData(lineData1)
        lineChart1?.notifyDataSetChanged()
// Refresh the chart
// Refresh the chart
        lineChart1?.invalidate()
}
    fun updateChartWithData1(rowData: Float) {

        var yValueFrequency: Float = rowData.toFloat() // Assuming frequency data is at index 2

        // Assuming phase data is at index 3

        android.util.Log.d("line", "iterate")
        if(yValueFrequency<90.0)
            frequencyEntry.add(Entry(count.toFloat(), yValueFrequency))
        else
            phaseEntry.add(Entry(count.toFloat(),yValueFrequency))
        //   phaseEntry.add(com.github.mikephil.charting.data .Entry(count.toFloat(), yValuePhase))
        count = count + 1






//    YourXAxisFormatter.updateValues(frequency11, p1, p2);
// linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());

//    YourXAxisFormatter.updateValues(frequency11, p1, p2);
// linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());
        var xFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                // Format the Y-axis labels as needed
                return String.format("%.1f", value)
            }
        }
        lineChart?.getXAxis()?.setLabelCount(3, true)
        lineChart?.getXAxis()?.setPosition(XAxis.XAxisPosition.BOTTOM)
//  lineChart1.getXAxis().setLabelCount(3,true);
//  lineChart1.getXAxis().setLabelCount(3,true);
        lineChart?.getXAxis()?.setValueFormatter(
            xFormatter)
      /**  lineChart1?.getXAxis()?.setLabelCount(3, true)
        lineChart1?.getXAxis()?.setPosition(XAxis.XAxisPosition.BOTTOM)*/
//  lineChart1.getXAxis().setLabelCount(3,true);
//  lineChart1.getXAxis().setLabelCount(3,true);
 /**       lineChart1?.getXAxis()?.setValueFormatter(
            xFormatter)*/
//  lineChart1.getXAxis().setLabelCount(3,true);
//  lineChart1.getXAxis().setLabelCount(3,true);

//   linechart1.getXAxis().setValueFormatter(
//xFormatter);
//linechart.getXAxis().set// Implement your X-axis formatting
// linechart.getAxisLeft().setAxisMinimum(150000f);
// linechart.getAxisLeft().setAxisMimum(150000f);
//  linechart.getAxisLeft().setAxisMinimum(400f);
//   linechart1.getXAxis().setValueFormatter(
//xFormatter);
//linechart.getXAxis().set// Implement your X-axis formatting
// linechart.getAxisLeft().setAxisMinimum(150000f);
// linechart.getAxisLeft().setAxisMimum(150000f);
//  linechart.getAxisLeft().setAxisMinimum(400f);
        lineChart?.getAxisRight()?.setEnabled(false)
        lineChart?.getXAxis()?.setDrawGridLines(false)
      /**  lineChart1?.getAxisRight()?.setEnabled(false)
        lineChart1?.getXAxis()?.setDrawGridLines(false)*/
//  lineChart1.setData(lineData1);
// linechart1.getDescription().setText("Timed Phase Chart");


//   linechart1.getAxisLeft().setAxisMinimum(-80f);

//      linechart1.getAxisRight().setEnabled(false);
// Create LineDataSet with the entry lists
//  lineChart1.setData(lineData1);
// linechart1.getDescription().setText("Timed Phase Chart");


//   linechart1.getAxisLeft().setAxisMinimum(-80f);

//      linechart1.getAxisRight().setEnabled(false);
// Create LineDataSet with the entry lists
        var dataSetFrequency = LineDataSet(frequencyEntry, "Frequency Data")
        dataSetFrequency.setColor(android.graphics.Color.BLUE) // Set line color


        var dataSetPhase = LineDataSet(phaseEntry, "Phase Data")
        dataSetPhase.setColor(android.graphics.Color.RED) // Set line color


// Refresh the chart
// Refresh the chart

// Create LineData object with our datasets
// Create LineData object with our datasets
        lineData = LineData(dataSetFrequency)
       /** lineData1 = LineData(dataSetPhase)*/
        android.util.Log.d("linedata", lineData?.getDataSets().toString())
// Set data to the chart
// Set data to the chart
        lineChart?.setData(lineData)
        lineChart?.notifyDataSetChanged()
// Refresh the chart
// Refresh the chart
        lineChart?.invalidate()
    /**    lineChart1?.setData(lineData1)
        lineChart1?.notifyDataSetChanged()
// Refresh the chart
// Refresh the chart
        lineChart1?.invalidate()*/
    }
}
