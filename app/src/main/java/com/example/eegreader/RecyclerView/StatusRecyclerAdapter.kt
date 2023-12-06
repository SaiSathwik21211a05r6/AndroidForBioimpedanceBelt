package com.example.eegreader.RecyclerView


//Importing necessary views for recycler view
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

//Importing files for view components
import android.widget.TextView

//importing recycler view package
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.R
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Button
import com.example.eegreader.RecordScreen
import java.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.eegreader.Graph.Archives
import com.example.eegreader.Setup
import com.example.eegreader.USB.USBClass
import com.example.eegreader.adapter
import com.example.eegreader.database.Patientlist
import com.example.eegreader.database.StatusList
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.Locale


//Creating an adaptor class
class StatusRecyclerAdaptor(
    var list: List<StatusList>,
    val statusListClickInterface: StatusListClickInterface,
    val patid1: String
):RecyclerView.Adapter<StatusRecyclerAdaptor.StatusViewHolder>() {
    val visitCountMap: MutableMap<String, Int> = mutableMapOf()
    var visitNumber: Int = 0
    private var holder: StatusViewHolder? = null

    //View holder in recycler view
    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Initialising view components on each element in the recycler view
        val visita = itemView.findViewById<TextView>(R.id.visit)
        val patida = itemView.findViewById<TextView>(R.id.patid)
        val visittimea = itemView.findViewById<TextView>(R.id.visitdate)
        val visitdatea = itemView.findViewById<TextView>(R.id.visittime)
        val avgfreq = itemView.findViewById<TextView>(R.id.avgfreq)
        val avgbioimp = itemView.findViewById<TextView>(R.id.avgbioimp)
        val avgphase = itemView.findViewById<TextView>(R.id.avgphase)


        val deletebuttona = itemView.findViewById<Button>(R.id.Deleterecycbutton)
        val recordbuttona = itemView.findViewById<Button>(R.id.Deleterecycbutton)
        val statusa = itemView.findViewById<TextView>(R.id.status)
        val reporta = itemView.findViewById<TextView>(R.id.report)
        val recorda = itemView.findViewById<TextView>(R.id.record)
        val recyccard = itemView.findViewById<CardView>(R.id.recyccard)
        val secondLinearLayout = itemView.findViewById<LinearLayout>(R.id.clindetails)

    }


    interface StatusListClickInterface {
        fun OnItemClick(statuslist: StatusList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
// Filter the list to include only items with matching patient IDs
Log.d("inflating","here")

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.statuslistrecycler, parent, false)
        return StatusViewHolder(view)
    }

    val grayColor = Color.parseColor("#808080")
    private var expandedPosition: Int = -1
    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        this.holder = holder

        val reversedList = list.reversed()
        val visitItem = reversedList[position]
        val isExpanded = position == expandedPosition

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled)
        )

        val colors = intArrayOf(

            Color.parseColor("#f5f5f5"),
            Color.parseColor("#f5f5f5")
        )
        val colors1 = intArrayOf(

            Color.WHITE,
            Color.WHITE
        )

        val myList = ColorStateList(states, colors)
        val myList1 = ColorStateList(states, colors1)
        if(position%2==0)
            holder.recyccard.backgroundTintList=myList
        else
            holder.recyccard.backgroundTintList=myList1
        // Set the visibility of the second LinearLayout based on the expanded state
      /**  holder.secondLinearLayout.visibility = if (isExpanded) View.VISIBLE else View.GONE

        // Set click listener on the entire item to toggle the expansion
        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            val backgroundColor = if (position % 2 == 0) {
                Color.parseColor("#800080")
                Log.d("color","blue")} else { Color.parseColor("#800080")
                Log.d("color","black")}

            holder.itemView.setBackgroundResource(R.color.black)
            notifyDataSetChanged()
        }
*/
        // Check if there are existing visits
        if (list.size >= 1) {
            // Existing visits found, increment the visit number


        } else {
            // No existing visits, set visit to 1

            holder.visita.text = "1"

            // Get the current date and time
            val currentDateTime = Calendar.getInstance()
            val formattedDate =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDateTime.time)
            val formattedTime =
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentDateTime.time)
            val reversedList = list.reversed()
            val visitItem = reversedList[position]

            // Calculate visit number for each patient


            // Increment the visit count for this patient
            val newVisitNumber = visitNumber + 1
            visitCountMap[patid1] = newVisitNumber

            holder.visita.text = visitItem.visit.toString()

            // Update visit date and time
            visitItem.visitdate = formattedDate.toString()
            visitItem.visittime = formattedTime.toString()
        }

            val originalFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val targetFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val formattedDate = visitItem.visitdate ?: ""
                val formattedTime = visitItem.visittime?.replace("-", "") ?: ""
                val date: Date = originalFormat.parse(formattedTime)
                val fileName = "readings_${targetFormat.format(date)}_$formattedDate.csv"

                Log.d("filename", fileName)
                // Read the CSV file and compute averages
                val averages = calculateAveragesFromFile(holder.itemView.context, fileName)
                if (averages.size == 3)
                // Update TextViews with computed averages
                {
                    Log.d("filename1", fileName)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        holder.avgfreq.text = Html.fromHtml(
                            "<b>Avg Bioimpedance:</b><font color='$grayColor'>" + averages[2].toString()+"</font>",
                            Html.FROM_HTML_MODE_LEGACY
                        )

                        holder.avgbioimp.text = Html.fromHtml(
                            "<b>Avg Frequency:</b><font color='$grayColor'>" + averages[1].toString()+"</font>",
                            Html.FROM_HTML_MODE_LEGACY
                        )
                        holder.avgphase.text = Html.fromHtml(
                            "<b>Avg Phase:</b><font color='$grayColor'>" + averages[0].toString()+"</font>",
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    }  }
            } catch (e: Exception) {
            }
            // Increment the visit count for this patient
            val newVisitNumber = visitNumber + 1
            visitCountMap[visitItem.patid] = newVisitNumber
            holder.visita.text = newVisitNumber.toString()
            holder.patida.text = patid1


            holder.visita.text = "Visit " + visitItem.visit.toString()
            holder.visitdatea.text = "Time: " + visitItem.visitdate.toString()
            holder.visittimea.text = "Date: " + visitItem.visittime.toString()
            holder.deletebuttona.setOnClickListener {
                showDeleteConfirmationDialog(holder.itemView.context, list[position])
                //  patientListClickInterface.OnItemClick(list.get(position))

            }


            holder.recorda.setOnClickListener {
                if (visitItem.visitdate != null && visitItem.visittime != null) {
                    val timeParts = visitItem.visittime.split(":")
                    Log.d("timeparts", timeParts.toString())


                    // Generate the CSV file here with the 'fileName'

                    if (isOldVisit(visitItem) && Math.abs(
                            (currentDateTimeInSeconds() - getSecondsFromVisittime(
                                visitItem.visittime
                            ))
                        ) > 30
                    ) {
                        Log.d("time", "passed")
                        val intent = Intent(holder.itemView.context, Archives::class.java)
                        intent.putExtra("patid", patid1)
                        intent.putExtra("visitid", visitItem.visit.toString())
                        intent.putExtra("visitdate", visitItem.visitdate.toString())
                        intent.putExtra("visittime", visitItem.visittime.toString())
                        holder.itemView.context.startActivity(intent)

                }

            }
            holder.statusa.setOnClickListener {
                Log.d("time", "passed")
                val intent = Intent(holder.itemView.context, Setup::class.java)
                intent.putExtra("patient id", patid1)
                intent.putExtra("visit id", visitItem.visit.toString())
                intent.putExtra("date", visitItem.visitdate.toString())
                intent.putExtra("time", visitItem.visittime.toString())
                holder.itemView.context.startActivity(intent)
            }
        }
            // No existing visits, set the text to show a message

            // You may want to handle other views or visibility based on your layout
        }






  /**  fun updateSelectedTimeRange(startTime: String, endTime: String) {

        val filteredList = list.filter {
            // Assuming visittime is in HH:mm format
            it.visittime?.let { time ->
                Log.d("time",time)
                time >= startTime && time <= endTime
            } ?: false
        }
        updateList(filteredList)
    }*/
  var isFirstRow:Boolean=true
    private fun calculateAveragesFromFile(context: Context, fileName: String): List<Double> {
        val averages = mutableListOf<Double>()

        try {
            val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDirectory, fileName)

            if (file.exists()) {

                // Read the CSV file and calculate averages
                val lines = file.readLines()

                if (lines.isNotEmpty()) {
                    // Assuming CSV format: freq, bioimp, phase
                    val columns = lines[0].split(",")

                    if (columns.size >= 3) {
            //        alculate averages for each column
                        val freqValues = mutableListOf<Double>()
                        val bioimpValues = mutableListOf<Double>()
                        val phaseValues = mutableListOf<Double>()

                        for (line in lines.subList(1, lines.size)) {

                            // C
                            val values = line.split(",")

                            if (values.size >= 3) {
                                Log.d("filename2","filename")
                                freqValues.add(values[0].toDouble())
                                bioimpValues.add(values[1].toDouble())
                                phaseValues.add(values[2].toDouble())
                            }
                        }

                        // Calculate averages
                        val avgFreq = freqValues.average()
                        val avgBioimp = bioimpValues.average()
                        val avgPhase = phaseValues.average()

                        averages.addAll(listOf(avgFreq, avgBioimp, avgPhase))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", "Error reading CSV file: ${e.message}")
        }
Log.d("averages", averages.toString())
        return averages
    }
    private fun isOldVisit(visitItem: StatusList): Boolean {
        return visitItem.visitdate != null && visitItem.visittime != null
    }

    private fun currentDateTimeInSeconds(): Int {
        val currentDateTime = Calendar.getInstance()
        return currentDateTime.get(Calendar.HOUR_OF_DAY) * 3600 + currentDateTime.get(Calendar.MINUTE) * 60 + currentDateTime.get(Calendar.SECOND)
    }
    private fun showDeleteConfirmationDialog(context: Context, status: StatusList) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.deleteconfirmation, null)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        val confirmButton = dialogView.findViewById<Button>(R.id.btn_confirm)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        val messageText = dialogView.findViewById<TextView>(R.id.dialog_message)

        messageText.text = "Are you sure you want to delete ${status.visit}'s details?"

        // Set a click listener for the Confirm button
        confirmButton.setOnClickListener {
            // Handle the confirmation logic here (e.g., delete the patient)
            statusListClickInterface.OnItemClick(status)
            adapter.notifyDataSetChanged()
            alertDialog.dismiss()
        }

        // Set a click listener for the Cancel button
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
    private fun getSecondsFromVisittime(visittime: String): Int {
        val timeParts = visittime.split(":")
        if (timeParts.size == 3) {
            Log.d("checking","time")
            return timeParts[0].toInt() * 3600 + timeParts[1].toInt() * 60 + timeParts[2].toInt()
        }
        return 0
    }
    private fun downloadFileForVisit(visitItem: StatusList) {
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val visitTime = visitItem.visittime

        if (visitTime != null) {
            Log.d("file","downloading")
            // Parse the visit time to a Date object
            val sdf = SimpleDateFormat("HH:mm", Locale.US)
            val visitTimeDate = sdf.parse(visitTime)

            if (visitTimeDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = visitTimeDate

                // Create a loop to iterate over 30-minute intervals
                val endTime = calendar.timeInMillis + 30 * 60 * 1000  // 30 minutes in milliseconds

                while (calendar.timeInMillis <= endTime) {
                    val timeSlot = SimpleDateFormat("HHmm", Locale.US).format(calendar.time)
                  //  val dateSlot = SimpleDateFormat("yyyymmdd", Locale.US).format(calendar.)
                    val formattedDate = visitItem.visitdate.replace("-", "")
                    // Construct the file name based on the time slot
                    val fileName = "readings_${formattedDate}_$timeSlot.csv"
                    Log.d("searching for",fileName)

                    val file = File(downloadDirectory, fileName)

                    if (file.exists()) {
                        Log.d("File not","downloaded3")
                        // The file exists, so open it using an appropriate application
                        val uri = FileProvider.getUriForFile(
                            holder?.itemView?.context ?: return,
                            "${holder?.itemView?.context?.applicationContext?.packageName}.provider",
                            file
                        )
                        Log.d("File not","downloaded2")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        intent.setDataAndType(uri, "text/csv")

                        try {
                            Log.d("File not","downloaded1")
                            holder?.itemView?.context?.startActivity(intent)
                            return  // Open the first available file and exit the loop
                        } catch (e: ActivityNotFoundException) {
                            // Handle the case where there is no app to open CSV files
                            // You can show a message or provide an alternative action here
                            Log.d("File not","downloaded")
                        }
                    }
                    Log.d("File not","downloaded5")
                    // Move to the next 30-minute interval
                    calendar.add(Calendar.MINUTE, 1)

                 }


                // Handle the case where no file is found within the 30-minute intervals
                // You can show a message or provide an alternative action here
            } else {
                Toast.makeText(holder?.itemView?.context,"No file downloaded for this recording",Toast.LENGTH_SHORT)
            }
        }
    }

    /**private fun downloadFileForVisit(visitItem: StatusList) {
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "readings_${visitItem.visitdate}_${visitItem.visittime}.csv"
        val file = File(downloadDirectory, fileName)

        if (file.exists()) {
            // The file exists, so open it using an appropriate application
            val uri = holder?.itemView?.context?.let {
                FileProvider.getUriForFile(
                    it,
                    holder?.itemView?.context?.applicationContext?.packageName + ".provider",
                    file
                )
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.setDataAndType(uri, "text/csv")

            try {
                holder?.itemView?.context?.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where there is no app to open CSV files
                // You can show a message or provide an alternative action here
            }
        } else {
            // The file does not exist
            // You can handle this case accordingly, e.g., show a message

        }
    }*/

    fun updateList(filteredItems: List<StatusList>) {
        list = filteredItems
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {

        return list.size
    }

}