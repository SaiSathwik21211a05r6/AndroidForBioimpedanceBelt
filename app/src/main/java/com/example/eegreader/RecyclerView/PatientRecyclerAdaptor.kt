package com.example.eegreader.RecyclerView

//Importing necessary views for recycler view

//Importing files for view components

//importing recycler view package
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.Journal
import com.example.eegreader.R
import com.example.eegreader.Setup
import com.example.eegreader.USB.USBClass
import com.example.eegreader.Visits
import com.example.eegreader.database.NoSQL.Visit
import com.example.eegreader.database.Patientlist
import com.example.eegreader.database.StatusList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates


public var patid by Delegates.notNull<Int>()
var patientid: Int = 0
var VisitId: String=""

//Creating an adaptor class
class PatientRecyclerAdaptor(
    var list: List<Patientlist>,
    val patientListClickInterface: PatientListClickInterface
):RecyclerView.Adapter<PatientRecyclerAdaptor.PatientViewHolder>() {
    val databaseReference = FirebaseDatabase.getInstance().getReference()
    private var currentColor: Int = Color.parseColor("#800080")
    private var currentColor1: Int = Color.parseColor("#ffffff")

    //View holder in recycler view
    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyccard: CardView = itemView.findViewById(R.id.recyccard)

        //Initialising view components on each element in the recycler view
        val patientnamea = itemView.findViewById<TextView>(R.id.patientname)
        val dora = itemView.findViewById<TextView>(R.id.dor)


        val agea = itemView.findViewById<TextView>(R.id.Age)
        val pida = itemView.findViewById<TextView>(R.id.pid_display)
        val gendera = itemView.findViewById<TextView>(R.id.Gender)
        val bloodgroupa = itemView.findViewById<TextView>(R.id.BloodGroup)
  val deletebuttona = itemView.findViewById<Button>(R.id.Deletebutton)
        val recordbuttona=itemView.findViewById<ImageButton>(R.id.Deleterecycbutton)
        val statusa = itemView.findViewById<TextView>(R.id.status)
        val journala = itemView.findViewById<ImageButton>(R.id.journalbutton)



    }



    interface PatientListClickInterface {
        fun OnItemClick(patientlist: Patientlist)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.patientlistrecycler, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        Log.d("Value of CurrentCOlor1",currentColor1.toString())
        Log.d("Value of CurrentCOlor ",currentColor.toString())

        val handler = Handler(Looper.getMainLooper())
        var backgroundColor = 0

        if (position % 2 == 0) {
            backgroundColor =  currentColor
            Log.d("color", "blue")
            Log.d("Position =",position.toString())


        }
        else {
            backgroundColor =  currentColor1
            Log.d("color","black")
            Log.d("Position =",position.toString())
        }
        Log.d("Final Background value",backgroundColor.toString())

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
        holder.itemView.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(Color.parseColor("#000000"))
            }
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(Color.TRANSPARENT)
            }
            false
        })
        holder.itemView.setBackgroundColor(backgroundColor)
        holder.patientnamea.text = list.reversed().get(position).patientName
        holder.pida.text = list.reversed().get(position).patientId
//        patid= (holder.patientnamea.text as String).toInt()
        holder.agea.text = list.reversed().get(position).patientAge.toString()
        holder.gendera.text =  list.reversed().get(position).patientGender.toString()
        holder.bloodgroupa.text = list.reversed().get(position).patientBg.toString()
        holder.dora.text = list.reversed().get(position).dor.toString()

        holder.deletebuttona.setOnClickListener {
            try{
            addVisitForPatient(holder.patientnamea.text as String)}
            catch (e:Exception)
            {
                addVisitForPatient((holder.pida.text as String))
            }

            val currentDateTime = Calendar.getInstance()
            val intent=Intent(holder.itemView.context, Visits::class.java)
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDateTime.time)
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentDateTime.time)

            intent.putExtra("time",formattedTime)
            intent.putExtra("date",formattedDate)
            intent.putExtra("visit id", VisitId)
            intent.putExtra("patient id",holder.patientnamea.text)
            intent.putExtra("patient name",holder.pida.text)
            holder.itemView.context.startActivity(intent)


          //  patientListClickInterface.OnItemClick(list.get(position))

        }
        holder.journala.setOnClickListener {
            val currentDateTime = Calendar.getInstance()
            val intent=Intent(holder.itemView.context, Journal::class.java)
            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDateTime.time)
            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentDateTime.time)

            intent.putExtra("time",formattedTime)
            intent.putExtra("date",formattedDate)
            intent.putExtra("visit id", VisitId)
            intent.putExtra("patient id",holder.patientnamea.text)
            intent.putExtra("patient name",holder.pida.text)
            holder.itemView.context.startActivity(intent)
        }
        //going to record screen for taking readings
        holder.recordbuttona.setOnClickListener {
            Toast.makeText(holder.itemView.context,"Please wait...",Toast.LENGTH_SHORT);
            if(holder.pida.text.length!=0){
                Log.d("paTIENT NAME", holder.pida.text as String)
addVisitForPatient(holder.patientnamea.text as String)}
            else
            {
                addVisitForPatient((holder.patientnamea.text as String))
            }

            val currentDateTime = Calendar.getInstance()
            // Filter the list to include only items with matching patient IDs




                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDateTime.time)
                val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentDateTime.time)

                val intent = Intent(holder.itemView.context, Setup::class.java)

                intent.putExtra("time",formattedTime)
                intent.putExtra("date",formattedDate)
                intent.putExtra("patient name",holder.pida.text)
            Log.d("sent",VisitId.toString())
            intent.putExtra("visit id", VisitId)
            intent.putExtra("patient id",holder.patientnamea.text)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setBackgroundColor(Color.BLACK)
    }

    private fun isOldVisit(visitItem: StatusList): Boolean {
        return visitItem.visitdate != null && visitItem.visittime != null
    }

    private fun currentDateTimeInSeconds(): Int {
        val currentDateTime = Calendar.getInstance()
        return currentDateTime.get(Calendar.HOUR_OF_DAY) * 3600 + currentDateTime.get(Calendar.MINUTE) * 60 + currentDateTime.get(Calendar.SECOND)
    }
    // Inside your PatientRecyclerAdaptor class
    private fun showDeleteConfirmationDialog(context: Context, patient: Patientlist) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.deleteconfirmation, null)

        builder.setView(dialogView)
        val alertDialog = builder.create()

        val confirmButton = dialogView.findViewById<Button>(R.id.btn_confirm)
        val cancelButton = dialogView.findViewById<Button>(R.id.btn_cancel)
        val messageText = dialogView.findViewById<TextView>(R.id.dialog_message)

        messageText.text = "Are you sure you want to delete ${patient.patientName}'s details?"

        // Set a click listener for the Confirm button
        confirmButton.setOnClickListener {
            // Handle the confirmation logic here (e.g., delete the patient)
            patientListClickInterface.OnItemClick(patient)
            alertDialog.dismiss()
        }

        // Set a click listener for the Cancel button
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

//cloud
    fun addVisitForPatient(patientId: String) {
        // Check if the patient already exists in the database
        val patientRef = databaseReference.child("Patients").child(patientId.toString())

Log.d("visit recorded","here")
    patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The patient exists, now get the number of existing visits
                    val visitsRef = patientRef.child("Visits")
                    val visitCount = dataSnapshot.child("Visits").childrenCount.toInt()
                    val newVisitNumber = visitCount + 1

VisitId= newVisitNumber.toString()
                    // Get the current date and time
                    val currentTimeMillis = System.currentTimeMillis()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val currentDateAndTime = sdf.format(currentTimeMillis)

                    // Create a new visit object
                    val newVisit = Visit()
                    newVisit.setVisitNumber(newVisitNumber.toString())
                    newVisit.setVisitDate(currentDateAndTime)

                    // Store the new visit in the database
                    visitsRef.child("Visit$newVisitNumber").setValue(newVisit)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    override fun getItemCount(): Int {

        return list.size
    }

}
