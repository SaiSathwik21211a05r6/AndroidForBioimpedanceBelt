package com.example.eegreader

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.BLEModule.BLEConstants
import com.example.eegreader.BLEModule.BLEConstants.Companion.ACTION_DATA_AVAILABLE
import com.example.eegreader.BLEModule.BLEService
import com.example.eegreader.Graph.CustomDataPoint
import com.example.eegreader.Graph.YourXAxisFormatter
import com.example.eegreader.RecyclerView.DataAdapter
import com.example.eegreader.RecyclerView.ReadingsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

var sendText: TextView? = null
var sendText1: TextView? = null
var sendText2: TextView? = null
var sendText3: TextView? = null
private lateinit var stopButton: Button
private lateinit var saveButton: Button
private var linechart1: LineChart? = null

//If the device requires ByteArrays as input
private const val frequency11 = 1000.00
private const val frequency21 = 2000.00
private const val frequencystep1 = 1.00

private var p1 = 0
private var p2 = 0

private const val frequency = "10000.00"
private const val ndp = "11"

//graph updation time
private const val PROCESSING_INTERVAL = (15 * 1000 // 60 seconds in milliseconds
        ).toLong()

//time counter
private var lastProcessingTime: Long = 0
private val startTime = System.currentTimeMillis()

//Data points collection for graph plotting
val rzMagValues: ArrayList<Double> = ArrayList()
val rzPhaseValues: ArrayList<Double> = ArrayList()

//for optimization of processing
var numbers = java.lang.StringBuilder()
private val dataBuffer = java.lang.StringBuilder()

//connection checker
private enum class Connected {
    False, Pending, True
}

//Possible Textview useful for the future
private val receiveText: TextView? = null


// Declare dataPoints as a list of your custom data points for plotting real time graph
var dataPoints: ArrayList<CustomDataPoint> = ArrayList()
private val recyclerView: RecyclerView? = null
private val hexWatcher: com.example.eegreader.TextUtil.HexWatcher? = null
var frequencyEntries: List<Entry> = ArrayList()
var phaseEntries: List<Entry> = ArrayList()
private val receivedData = StringBuilder()
private var linechart: LineChart? = null
private const val hexEnabled = false
private var pendingNewline = false
private val newline = com.example.eegreader.TextUtil.newline_crlf
private val dataList: ArrayList<String> = ArrayList() // Your data source
val adapter = DataAdapter(dataList)
var databaseReference = FirebaseDatabase.getInstance().reference


class Readings : AppCompatActivity(), BLEService.DataCallback {
    private var dataLiveData = MutableLiveData<MutableList<String>>().apply {
        value = mutableListOf()
    }

    private lateinit var viewModel: ReadingsViewModel
    private val dataList = mutableListOf<String>() // Your data source
    private val adapter = DataAdapter(dataList)

    //Receiving updates for incoming data via BLE
    private val dataReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {

            val data = intent?.getStringExtra(com.example.eegreader.BLEModule.BLEService.EXTRA_DATA)?.toByteArray()
            val uuId = intent?.getStringExtra(BLEConstants.EXTRA_UUID)
            val spn = SpannableStringBuilder()

//data processing
            if (data != null) {
                Log.d("processing", "here")

                if (String(data) == "^@") {
                    Toast.makeText(this@Readings, "Expecting non-null values", Toast.LENGTH_SHORT)
                        .show()
                }
                if (hexEnabled) {
                    spn.append(TextUtil.toHexString(data)).append('\n')
                } else {
                    val msg = String(data)

                    // adapter.updateData(msg)
                    if (newline == TextUtil.newline_crlf && msg.isNotEmpty()) {
                        // Don't show CR as ^M if directly before LF
                        msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf)

                        if (pendingNewline && msg.isNotEmpty() && msg[0] == '\n') {
                            if (spn.length >= 2) {
                                spn.delete(spn.length - 2, spn.length)
                            } else {
                                val edt = receiveText?.editableText
                                if (edt != null && edt.length >= 2) {
                                    edt.delete(edt.length - 2, edt.length)
                                }
                            }
                        }
                        pendingNewline = msg.isNotEmpty() && msg[msg.length - 1] == '\r'
                    }
                    spn.append(TextUtil.toCaretString(msg, newline.length != 0))
                    if (!hexEnabled) {
                        val msg1 = msg
                        receivedData.append(msg1)

                    }
                }
//Number extraction from received string
                if (containsNumber(receivedData?.toString().toString()) || containsDecimal(receivedData?.toString().toString())) {
                    dataBuffer.append(receiveText?.text)

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {
                        processAndDisplayData(receivedData?.toString().toString())
                        lastProcessingTime = currentTime

                        // Clear the buffer after processing
                        dataBuffer.setLength(0)
                    }
                } else {
                    // Handle non-numeric data here if needed
                }

            }

        }
    }
    var number3 = 0.0
    var number4 = 0.0
    fun numberExtract(finalMsg: String) {
        val pattern = Pattern.compile("-?\\d+(\\.\\d+)")

// The input string containing numbers

// Create a matcher for the input string
        val matcher = pattern.matcher(finalMsg)

// Find and print all matched numbers
        while (matcher.find()) {
            val number = matcher.group()
            if (number.toDouble() > 1500000.00) number3 = number.toDouble() else number4 =
                number.toDouble()


            //number2.add(Double.valueOf(number));
        }
    }
    var csvstring = java.lang.StringBuilder()

    var number2 = java.util.ArrayList<Double>()
    var data = arrayOf<String?>(null)
    private fun resetActivityState() {
        // Reset your activity state to its initial state here

        // For example, you can clear text fields, reset variables, or perform any other required actions
        sendText!!.text = "" // Clear the text in an EditText
        sendText1!!.text = ""
        sendText2!!.text = ""
        sendText3!!.text = ""
        receiveText!!.text = "" // Clear a TextView
        // Reset any other views or variables
        data = arrayOf(null)
        rzMagValues.clear()
        rzPhaseValues.clear()
        number2.clear()
        p1 = 0
        p2 = 0
        if (receivedData.length != 0) receivedData.delete(0, receivedData.length - 1)

        // You can also refresh your chart or clear the X-axis labels if needed
        linechart!!.xAxis.valueFormatter =
            YourXAxisFormatter(lastProcessingTime - startTime, frequency11, p1, p2)
        linechart!!.notifyDataSetChanged()
        linechart!!.invalidate()
        linechart!!.clear()
        linechart1?.getXAxis()?.setValueFormatter(
            YourXAxisFormatter(
                lastProcessingTime - startTime,
                frequency11,
                p1,
                p2
            )
        )
        linechart1?.notifyDataSetChanged()
        linechart1?.invalidate()
        linechart1?.clear()
        adapter.notifyDataSetChanged()
    }
    //processing and updating chart
    open fun processAndDisplayData(finalMsg: String) {
        val backgroundTask = Runnable {
            val pattern = Pattern.compile("-?\\d+(\\.\\d+)")

            // The input string containing numbers

            // Create a matcher for the input string
            val matcher = pattern.matcher(finalMsg)

            // Find and print all matched numbers
            while (matcher.find()) {
                val number = matcher.group()
                number2.add(java.lang.Double.valueOf(number))
                // readings.add(Double.valueOf(number));
                // Log.d("numbers are ", String.valueOf(readings.peek()));
                Log.d("numbers2 are", number2.toString())
                number.replace(number, "")
                if (rzMagValues.size < 3) {
                    if (java.lang.Double.valueOf(number) > 150000.00) {
                        rzMagValues.add(java.lang.Double.valueOf(number))
                    }
                } else if (rzMagValues.size >= 3 && java.lang.Double.valueOf(number) > 150000.00) {
                    rzMagValues.clear()
                    rzMagValues.add(java.lang.Double.valueOf(number))
                }
                if (rzPhaseValues.size < 3 && java.lang.Double.valueOf(number) < 0 && java.lang.Double.valueOf(
                        number
                    ) < 100.00
                ) {
                    rzPhaseValues.add(java.lang.Double.valueOf(number.trim { it <= ' ' }))
                } else if (rzPhaseValues.size >= 3 && java.lang.Double.valueOf(number) < 0 && java.lang.Double.valueOf(
                        number
                    ) < 100.00
                ) {
                    rzPhaseValues.clear()
                    rzPhaseValues.add(java.lang.Double.valueOf(number.trim { it <= ' ' }))
                }
            }

            //number2.clear();
            for (i in rzPhaseValues.indices) {
                // double normalizedRzPhaseValue = normalizeValue(rzPhaseValues.get(i), -100, 150000);
                //   rzPhaseValues.set(i, normalizedRzPhaseValue);
            }
            finalMsg.replace(finalMsg, "")
            try {
                val currentTime = System.currentTimeMillis()
                val timeElapsed = currentTime - startTime

                // Create a DataPoint object with the parsed values
                val dataPoint = CustomDataPoint(timeElapsed, rzMagValues, rzPhaseValues)
                dataPoints.add(dataPoint)

                // Update your data sets


                // Update the chart with the new data
                runOnUiThread {
                    if (rzMagValues.size != 0 && rzPhaseValues.size != 0)
                        data =
                            arrayOf(rzMagValues[0].toString() + "," + rzPhaseValues[0].toString() + "," + frequency + "," + p1 + "," + p2)

                    //  Log.d("data passed", data[0]);
                    // UI-related code to update the chart goes here
                    updateChartWithData(dataPoints)
                    val parts = finalMsg.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    // Update your UI here
                    adapter.updateData(data.get(0))
                    adapter.setRecyclerView(recyclerView!!)
                }
            } catch (e: NumberFormatException) {
                // Handle parsing errors
                // Log.e("Data Parsing", "Error parsing data: " + msg);
            }
        }
        Thread(backgroundTask).start()
    }

    //real time graph plotting
    private fun updateChartWithData(dataPoint: List<CustomDataPoint>) {

        // Create new LineData and LineDataSet
        val frequencyDataSet = LineDataSet(frequencyEntries, "Frequency")
        frequencyDataSet.color = Color.BLUE
        frequencyDataSet.valueTextColor = Color.BLACK
        frequencyDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        val phaseDataSet = LineDataSet(phaseEntries, "Phase")
        phaseDataSet.color = Color.RED
        phaseDataSet.valueTextColor = Color.BLACK
        phaseDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        for (i in 0 until rzMagValues.size) {

                frequencyDataSet.addEntry(Entry((lastProcessingTime - startTime).toFloat(), rzMagValues[i].toFloat()))

break
        }

        // Add rzPhaseValues to the phaseEntries
        for (i in 0 until rzPhaseValues.size) {
            phaseDataSet.addEntry(Entry((lastProcessingTime - startTime).toFloat(), rzPhaseValues[i].toFloat()))
            break
        }

        val lineData = LineData(frequencyDataSet,phaseDataSet)


        // Update the chart
        linechart?.data = lineData
        linechart?.description?.text = "Timed Data Chart"
        linechart?.xAxis?.valueFormatter = YourXAxisFormatter(
lastProcessingTime- startTime,11.0,1,2

        ) // Implement your X-axis formatting
        linechart?.axisLeft?.axisMinimum = 0f // Set Y-axis minimum value
        linechart?.axisRight?.isEnabled = false

        // Refresh the chart
        linechart?.notifyDataSetChanged()
        linechart?.invalidate()
    }

    //format for the file name is: readings_date_time.csv and saves in the downloads folder of the mobile
    private fun saveReadingsToCSV() {
        if (receivedData.length < 100) {
            Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
            val currentDateTime = dateFormat.format(Date())
            val fileName = "readings_$currentDateTime.csv"
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(directory, fileName)
            val writer = FileWriter(file)
            writer.append("Frequency,Bioimpedance,Phase,Electrode Combination 1, Electrode Combination2\n")
            // Write the received data to the CSV file.
            if (csvstring != null) writer.append(csvstring.toString())

            // Close the FileWriter.
            writer.close()

            // Clear the received data.
            receivedData.setLength(0)
            Toast.makeText(this, "Data saved to Downloads/$fileName", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show()
        }
    }
    //number extraction methods
    private fun isDecimal(str: String): Boolean {
        return str.matches("-?\\d*\\.?\\d+".toRegex())
    }

    fun containsNumber(input: String): Boolean {
        // Define a regular expression pattern to match any digit
        val regex = ".*\\d+.*".toRegex()

        // Check if the input contains a digit
        return regex.matches(input)
    }

    fun containsDecimal(str: String): Boolean {
        // Define a regular expression pattern for a decimal number
        // This pattern allows for an optional minus sign, digits before and after the decimal point.
        val decimalPattern = "-?\\d*\\.?\\d+".toRegex()

        // Use the pattern to check if the input string contains a decimal
        return decimalPattern.containsMatchIn(str)
    }

    private fun registerServiceReceiver() {val filter = IntentFilter(com.example.eegreader.BLEModule.BLEConstants.ACTION_DATA_AVAILABLE)

        registerReceiver(dataReceiver, filter)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.outdata)

        dataLiveData.value = mutableListOf()

        //four inputs
        sendText = findViewById<TextView>(R.id.input1)
        sendText1 = findViewById<TextView>(R.id.input2)
        sendText2 = findViewById<TextView>(R.id.input3)
        sendText3 = findViewById<TextView>(R.id.input4)

        //tabular form for display
        val recyclerView: RecyclerView = findViewById(R.id.recview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        viewModel = ViewModelProvider(this).get(ReadingsViewModel::class.java)

        stopButton = findViewById(R.id.stopbutton)
        saveButton = findViewById(R.id.add)
        // Observe LiveData from the ViewModel
       runOnUiThread {

            dataLiveData.observe(this, { dataList ->
              //   This block should be executed on the main thread when data changes
               adapter.updateData(dataList.toString())
            })
        }

        //Communication from app
        val sendBtn = findViewById<View>(R.id.send_btn)
        sendBtn.setOnClickListener(View.OnClickListener { v: View? ->
            bleService.writeCharacteristic1(
                (sendText?.text.toString() + "," + sendText1?.getText()
                    .toString() + "," + sendText2?.getText().toString() + "," + sendText3?.getText()
                    .toString())
            )
        })

        //Real time graph plotting initialization
        linechart = findViewById<LineChart>(R.id.lineChart1)
        linechart?.getXAxis()?.isEnabled = true
        linechart?.getAxisLeft()?.isEnabled = false
        linechart?.getAxisRight()?.isEnabled = false
        linechart?.getDescription()?.isEnabled = false
        linechart1 = findViewById<LineChart>(R.id.lineChart2)
        linechart1?.getXAxis()?.setEnabled(false)
        linechart1?.getXAxis()?.setEnabled(true)
        linechart1?.getXAxis()?.setDrawGridLines(false)
        linechart1?.getAnimation()
        linechart1?.getDescription()?.setEnabled(false)
        linechart1?.getAxisLeft()?.setDrawGridLines(true)
        linechart1?.getAxisLeft()?.setLabelCount(6, true) // Number of labels (10 steps + 1)

        //  linechart1.getAxisLeft().setAxisMaximum(-95f); // Minimum value for Y-axis
        //  linechart.getAxisLeft().setAxisMinimum(-85f); // Maximum value for Y-axis
        //  linechart1.getAxisLeft().setAxisMaximum(-95f); // Minimum value for Y-axis
        //  linechart.getAxisLeft().setAxisMinimum(-85f); // Maximum value for Y-axis
        val stepValue1 = 1f
        // xAxis.setValueFormatter(customFormatter);


// Set any other chart configurations (labels, axis scaling, etc.)

// Display the chart
        val yFormatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                // Format the Y-axis labels as needed
                return String.format("%.1f", value)
            }
        }
        linechart1?.getAxisLeft()?.setGranularity(stepValue1)
        linechart?.setTouchEnabled(true)
        linechart?.setDragEnabled(true)
        linechart?.setScaleEnabled(true)
        linechart1?.getAxisLeft()?.setValueFormatter(yFormatter)
        linechart1?.setTouchEnabled(true)
        linechart1?.setDragEnabled(true)
        linechart1?.setScaleEnabled(true)


        //stop button disconnects the device
        stopButton.setOnClickListener {
            // Handle the click event for the Stop button
if(bleService!=null)
    bleService.disconnect()
        }

        //save button saves the readings as a csv file
        saveButton.setOnClickListener {
            // Handle the click event for the Save button
            // You can add your logic here
saveReadingsToCSV()
            // Implment the drop-down behavior if needed
            // For example, show a drop-down menu or perform an action when the Save button is clicked
        }

    }
    private lateinit var bleService: BLEService
    private var isBound = false



    //Actions after exiting the screen
    override fun onDestroy() {
        super.onDestroy()

        bleService?.disconnect()
        // Unregister the BroadcastReceiver when the activity is destroyed
       unregisterReceiver(dataReceiver)
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            val binder = service as BLEService.LocalBinder
            bleService = binder.getService()
            isBound = true
            bleService.registerDataCallback(this@Readings)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }

        /**  override fun onServiceDisconnected(p0: ComponentName?,service: IBinder) {
            val binder = service as BLEService.LocalBinder
            bleService = binder.getService()
            isBound = true
            bleService.registerDataCallback(this@Readings)
        }*/


    }

    //Actions for opening the screen
    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, BLEService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)



    }


    //Actions for going back to the previous screen
    override fun onStop() {

        super.onStop()

        if (isBound) {
            unbindService(connection)
            isBound = false
            bleService.unregisterDataCallback()
            bleService?.disconnect()
        }}


    override fun onDataReceived(data: MutableList<ByteArray>) {
        // Create a new list to hold the updated data
        val newDataList = mutableListOf<String>()
        val newDataLiveData = MutableLiveData<MutableList<String>>().apply {
            value = mutableListOf()
        }
        // Iterate through the received data list
        for (element in data) {
            // Convert the ByteArray to a string using UTF-8 encoding
            val strData = String(element, Charsets.UTF_8)

            // Add the string to the new data list
            newDataList.add(strData)

        }

        runOnUiThread {

            viewModel.setData(newDataList)
            newDataLiveData.value = newDataList
            dataLiveData=newDataLiveData

        }


    }

    //Actions after closing and reopening the screen
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ACTION_DATA_AVAILABLE)

        registerReceiver(dataReceiver, filter)
    }

  //Action for temporarily putting the activity on hold
override fun onPause()
{super.onPause()

    bleService?.disconnect()
}
}



