package com.example.eegreader

//Importing Necessary files

//For the dialog window to add new grocery in the app

//Layout Components

//Presenting the list of groceries
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.Graph.Archives
import com.example.eegreader.RecyclerView.StatusRecyclerAdaptor
import com.example.eegreader.StatusDB.StatusViewModal
import com.example.eegreader.database.StatusDatabase
import com.example.eegreader.database.StatusList
import com.example.eegreader.database.StatusRepository
import com.example.eegreader.database.StatusViewModalFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

//Add grocery sign

class Visits: AppCompatActivity(), StatusRecyclerAdaptor.StatusListClickInterface{
    //Variables involving other files of the project
    lateinit var itemsRV:RecyclerView
    /**lateinit var add:FloatingActionButton*/
    lateinit var list:List<StatusList>
    lateinit var statusRecyclerAdaptor: StatusRecyclerAdaptor
    lateinit var patientViewModal: StatusViewModal
    val visitCountMap: MutableMap<String, Int> = mutableMapOf()
    var visitNumber:Int=0
    var visitid: String =""
    var visittime: String =""
    var file:File=File("","")
    lateinit var pid:TextView
    lateinit var vtime:TextView
    lateinit var vdate:TextView
    private var job: Job? = null
    var visitdate: String =""
lateinit var patid1:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statuslist)

        visitid= intent.getStringExtra("visit id").toString()
        patid1= intent.getStringExtra("patient id").toString()
        visittime= intent.getStringExtra("time").toString()
        visitdate= intent.getStringExtra("date").toString()
        val calendar = Calendar.getInstance()
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

// Before loading your RecyclerView data (e.g., in onCreate or before making the file search operation)

        val downloadDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        pid = findViewById<TextView>(R.id.patid)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        vtime = findViewById<TextView>(R.id.vtime)
        vdate = findViewById<TextView>(R.id.vdate)
        pid.setText(patid1)
        progressBar.visibility = View.VISIBLE
        vtime.setText( timeFormat.format(calendar.time))
        vdate.setText( dateFormat.format(calendar.time))


//        insertStatusRecord()
        //Views on the app UI
        itemsRV = findViewById(R.id.recview1)
        itemsRV.layoutManager = LinearLayoutManager(this)
        list = ArrayList<StatusList>()

       /** val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setDateTextAppearance(android.R.style.TextAppearance_Medium)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Show the TimePickerDialog when a date is selected
            showTimePickerDialog(year, month, dayOfMonth)
        }*/

                // Create a loop to iterate over 30-minute intervals

            /**    while (calendar.timeInMillis <= endTime) {
                    val timeSlot = SimpleDateFormat("HHmm", Locale.US).format(calendar.time)
                    // String dateSlot = new SimpleDateFormat("yyyyMMdd", Locale.US).format(calendar.getTime());
                    // String dateSlot = new SimpleDateFormat("yyyyMMdd", Locale.US).format(calendar.getTime());
                    val formattedDate = visitdate.replace("-", "")
                    // Construct the file name based on the time slot
                    // Construct the file name based on the time slot
                    val fileName = "readings_" + formattedDate + "_" + timeSlot + ".csv"
                    Log.d("searching for", fileName)

                    val file = File(downloadDirectory, fileName)

                    if (file.exists()) {
                        val uri = FileProvider.getUriForFile(
                            this,
                            applicationContext.packageName + ".provider",
                            file
                        )

                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.setDataAndType(uri, "text/csv")
                        try {
                            Log.d("File not", "downloaded1");

                            var csvData = CsvParser.readCsvFile(file.getAbsolutePath());
                            if (csvData.size > 2) {
                                Log.d("csvdata", csvData[1].contentToString())

                                var sumFrequency = 0f
                                var sumPhase = 0f

                                for (rowData in csvData) {
                                    // Skip the first row
                                    try {
                                        val yValueFrequency = rowData[2].toFloat()
                                        val yValuePhase = rowData[3].toFloat()

                                        Log.d("line", "iterate")


                                        // Accumulate values for average calculation
                                        sumFrequency += yValueFrequency
                                        sumPhase += yValuePhase
                                    } catch (e: Exception) {
                                        // Handle the exception as needed
                                    }
                                }

                                // Calculate averages
                                val avgFrequency = sumFrequency / csvData.size
                                val avgPhase = sumPhase / csvData.size

                                Log.d("Average Frequency", avgFrequency.toString())
                                Log.d("Average Phase", avgPhase.toString())
                            }
                            else{}
                        } catch (e: java.lang.Exception) {

                        }
                    }
                    else{}
                    calendar.add(Calendar.MINUTE, 1)}*/
                        statusRecyclerAdaptor = StatusRecyclerAdaptor(list, this, patid1)
                        itemsRV.adapter = statusRecyclerAdaptor
     //   val loadingProgressBar = findViewById<ProgressBar>(R.id.loadingProgressBar)
//        loadingProgressBar.visibility = View.GONE
        intent.getStringExtra("time")
                        intent.getStringExtra("date")
        val noDataTextView: TextView = findViewById(R.id.noDataTextView)
val visitTimet=findViewById<TextView>(R.id.visitdate)
        val record=findViewById<TextView>(R.id.record)
        val report=findViewById<TextView>(R.id.report)
        val visitDatet=findViewById<TextView>(R.id.visittime)

                        visitNumber = visitCountMap[patid1] ?: 0 // Default to 0 if not found

//Log.d("last visit",getLastVisitNumber())
                        val newVisitNumber = visitNumber + 1
                        visitCountMap[patid1] = newVisitNumber
                        Log.d("patid", visitCountMap.toString())

                        //Initialising databases required for storing grocery lists
                        val groceryRepository = StatusRepository(StatusDatabase(this))
                        val factory = StatusViewModalFactory(groceryRepository)
                        patientViewModal =
                            ViewModelProvider(this, factory).get(StatusViewModal::class.java)
                        Log.d("hello", list.toString())
                        val filteredList = list.filter {
                            it.patid.toString() == patid1
                        }

//
    //    val fileName = "readings_" + date + "_" + formattedDate + ".csv"
      //  Log.d("searching for", fileName)

//        val file = File(downloadDirectory, fileName)
                        patientViewModal.getthelistofstatus().observe(
                            this,
                            {
                                val backgroundTask: Runnable = object : Runnable {
                                    override fun run() {
var formattedDate:String
var fileName:String=""
                                statusRecyclerAdaptor.list = it.filter {
                                    val date = it.visitdate
                                    val time = it.visittime
                                    Log.d("time", it.visittime)

                                    if (isDateFormatValid(date)) {
                                        formattedDate = SimpleDateFormat(
                                            "HHmm",
                                            Locale.getDefault()
                                        ).format(
                                            SimpleDateFormat(
                                                "HH:mm:ss",
                                                Locale.getDefault()
                                            ).parse(time)
                                        )
                                        fileName =
                                            "readings_" + date + "_" + formattedDate + ".csv"
                                    } else {
                                        formattedDate = SimpleDateFormat(
                                            "HHmm",
                                            Locale.getDefault()
                                        ).format(
                                            SimpleDateFormat(
                                                "HHmm",
                                                Locale.getDefault()
                                            ).parse(date)
                                        )
                                        fileName =
                                            "readings_" + time + "_" + formattedDate + ".csv"
                                    }


                                    Log.d("searching for", fileName)








                                    file = File(downloadDirectory, fileName)

runOnUiThread {
                                    statusRecyclerAdaptor.notifyDataSetChanged()

   }

                                    it.patid.toString() == patid1&&file.exists()
                                   }
if(statusRecyclerAdaptor.list.isEmpty()){
    noDataTextView.visibility= VISIBLE
    noDataTextView.setText("No data")}
                                         progressBar.visibility=View.GONE
                                    }





                        //  patientViewModal.getthelistofstatus()

                    }
                                Thread(backgroundTask).start()

    })}
    fun isDateFormatValid(dateString: String): Boolean {
        val regex = Regex("\\d{4}-\\d{2}-\\d{2}")

        return regex.matches(dateString)
    }
  /**  fun showEndTimePickerDialog(calendar: Calendar) {
        // End Time Picker Dialog
        val endTimePickerDialog = TimePickerDialog(
            this,
            { _, endHourOfDay, endMinute ->
                // Set the end time in the calendar
                calendar.set(Calendar.HOUR_OF_DAY, endHourOfDay)
                calendar.set(Calendar.MINUTE, endMinute)

                // Now 'calendar' contains both start and end times selected by the user
                // You can use this 'calendar' object as needed
                // For example, display the selected start and end times in a TextView
                val selectedStartDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)

                // Update your UI or perform other actions with the selected start and end times
                updateSelectedDateTime(selectedStartDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )

        endTimePickerDialog.setTitle("Select End Time")
        endTimePickerDialog.show()
    }
    fun showTimePickerDialog(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        // Time Picker Dialog
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                // Now 'calendar' contains both date and time selected by the user
                // You can use this 'calendar' object as needed
                // For example, display the selected date and time in a TextView
                val selectedDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(calendar.time)
                // Update your UI or do other actions with the selected date and time
                updateSelectedDateTime(selectedDateTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24-hour format
        )

        timePickerDialog.show()
    }

    // Add this function to update your UI with the selected date and time
    private fun updateSelectedDateTime(selectedDateTime: String) {
        // Parse the selected date and time
        val selectedDate = selectedDateTime.split(" ")[0]
        val selectedTime = selectedDateTime.split(" ")[1]

        // Update your RecyclerView adapter with the selected date and time range
        statusRecyclerAdaptor.updateSelectedTimeRange(selectedTime, selectedTime)
    }*/



    //Using the add button
    /**  add.setOnClickListener {
    openDialog()
    }*/


    private fun insertStatusRecord() {
        val groceryRepository = StatusRepository(StatusDatabase(this))
        val factory = StatusViewModalFactory(groceryRepository)
        patientViewModal = ViewModelProvider(this, factory).get(StatusViewModal::class.java)

        val currentTimeMillis = System.currentTimeMillis()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd")
        val sdfTime = SimpleDateFormat("HH:mm:ss")
        val currentDate = sdfDate.format(currentTimeMillis)
        val currentTime = sdfTime.format(currentTimeMillis)

        val statusDao = StatusDatabase.getStatusDao()

        val lastVisitNumber = statusDao?.getLastVisitNumber()
        Log.d("lastVisitNumber",statusDao.toString())
        val newVisitNumber = lastVisitNumber?.let { it + 1 } ?: 1
if(visitid==null) {
    Toast.makeText(
        this,
        "The app currently doesn't support multi-device access. Kindly use the another device",
        Toast.LENGTH_LONG
    )
    val intent= Intent(this,Visits::class.java)
    startActivity(intent)
}
        val newStatusRecord = StatusList(patid1,visitid.toInt(), currentTime, currentDate)

        patientViewModal.insertstatus(newStatusRecord)

        Toast.makeText(applicationContext, "New visit recorded successfully", Toast.LENGTH_SHORT).show()
    }
    override fun OnItemClick(statuslist: StatusList) {
        patientViewModal.deleteitem(statuslist)
        statusRecyclerAdaptor.notifyDataSetChanged()

        Toast.makeText(applicationContext,"Patient data removed successfully",Toast.LENGTH_SHORT).show()
    }


}
