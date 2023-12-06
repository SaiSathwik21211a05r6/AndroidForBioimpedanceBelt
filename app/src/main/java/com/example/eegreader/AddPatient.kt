package com.example.eegreader


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.RecyclerView.PatientRecyclerAdaptor
import com.example.eegreader.RecyclerView.StatusRecyclerAdaptor
import com.example.eegreader.USB.USBClass
import com.example.eegreader.database.NoSQL.Patient
import com.example.eegreader.database.PatientDatabase
import com.example.eegreader.database.PatientRepository
import com.example.eegreader.database.PatientViewModal
import com.example.eegreader.database.PatientViewModalFactory
import com.example.eegreader.database.Patientlist
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddPatient: AppCompatActivity(), PatientRecyclerAdaptor.PatientListClickInterface {

    lateinit var itemsRV: RecyclerView
    lateinit var add: FloatingActionButton
    lateinit var list:List<Patientlist>
    lateinit var patientRecyclerAdaptor: PatientRecyclerAdaptor
    lateinit var patientViewModal: PatientViewModal
    var diabetesc: Int=0
    var htnc: Int=0
    var asthmac: Int=0
    var smokec: Int=0
    var alcoholc: Int=0
    var mic: Int=0
    var cvac: Int=0
    var stentc: Int=0
    var cabgc: Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
Log.d("launching","registration")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addpatient)
        list = ArrayList<Patientlist>()
        patientRecyclerAdaptor = PatientRecyclerAdaptor(list, this)
        val patient = Patient()

        //Cloud
        val projectId = FirebaseApp.getInstance().options.projectId
        val storageBucket = FirebaseApp.getInstance().options.storageBucket
        val projectName = getProjectNameFromStorageBucket(storageBucket!!)

        //Offline Database
        val groceryRepository = PatientRepository(PatientDatabase(this))
        val factory = PatientViewModalFactory(groceryRepository)
        patientViewModal = ViewModelProvider(this, factory).get(PatientViewModal::class.java)
        var patientgendereditb: String = ""
        val radioGroupGender = findViewById<RadioGroup>(R.id.genderedit)

            // Set up the radio group listener to capture the selected gender
        radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
                val selectedRadioButton = findViewById<RadioButton>(checkedId)
                patientgendereditb = selectedRadioButton.text.toString()
            }
            //View components on the dialog box
            val cancelbu = findViewById<Button>(R.id.add)
        val heightunit=findViewById<Spinner>(R.id.heightunitedit)
        val weightunit=findViewById<Spinner>(R.id.weightunitedit)
            val addbu = findViewById<Button>(R.id.cancelbutton)
        val patientideditb = findViewById<EditText>(R.id.patientidedit)
            val patientnameeditb = findViewById<EditText>(R.id.patientnameedit)
            val patientageeditb = findViewById<EditText>(R.id.patientageedit)
            val bloodgroupeditb =  findViewById<Spinner>(R.id.bloodgroupedit)
            if (bloodgroupeditb != null) {
                val adapter = ArrayAdapter.createFromResource(this, R.array.BloodGroupselection, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                bloodgroupeditb.adapter = adapter
                val defaultBloodGroupPosition = adapter.getPosition("Please Select")
                bloodgroupeditb.setSelection(defaultBloodGroupPosition)

                // ... (rest of the code)
            } else {

            }
        val heightPicker = findViewById<NumberPicker>(R.id.heightPicker)
        val weightPicker = findViewById<NumberPicker>(R.id.weightPicker)

// ... (rest of the code)

        if (weightunit != null) {
            val adapter = ArrayAdapter.createFromResource(this, R.array.WeightUnitSelection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            weightunit.adapter = adapter
            val defaultWeightUnitPosition = adapter.getPosition("Please Select")
            weightunit.setSelection(defaultWeightUnitPosition)

            weightunit.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                    // Set up NumberPicker based on the selected unit
                    val unit = parentView.getItemAtPosition(position).toString()
                    val minValue: Int
                    val maxValue: Int
                    if (unit == "Kgs") {
                        minValue = 40
                        maxValue = 150
                    } else {
                        // Assuming unit is "lbs"
                        minValue = 80
                        maxValue = 330
                    }

                    weightPicker.minValue = minValue
                    weightPicker.maxValue = maxValue
                    weightPicker.wrapSelectorWheel = false
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // Do nothing here
                }
            })
        } else {
            // Handle the case where weightunit is null
        }
        if (heightunit != null) {
            val adapter = ArrayAdapter.createFromResource(this, R.array.HeightUnitSelection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            heightunit.adapter = adapter
            val defaultHeightUnitPosition = adapter.getPosition("Please Select")
            heightunit.setSelection(defaultHeightUnitPosition)

            heightunit.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                    // Set up NumberPicker based on the selected unit
                    val unit = parentView.getItemAtPosition(position).toString()
                    val minValue: Int
                    val maxValue: Int
                    if (unit == "Feet") {
                        minValue = 1
                        maxValue = 10
                    } else {
                        // Assuming unit is "cm"
                        minValue = 100
                        maxValue = 300
                    }

                    heightPicker.minValue = minValue
                    heightPicker.maxValue = maxValue
                    heightPicker.wrapSelectorWheel = false
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // Do nothing here
                }
            })
        } else {
            // Handle the case where heightunit is null
        }

        if (heightunit != null) {
            val adapter = ArrayAdapter.createFromResource(this, R.array.HeightUnitSelection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            heightunit.adapter = adapter
            val defaultHeightUnitPosition = adapter.getPosition("Please Select")
            heightunit.setSelection(defaultHeightUnitPosition)

            // ... (rest of the code)
        } else {

        }
        if (weightunit != null) {
            val adapter = ArrayAdapter.createFromResource(this, R.array.WeightUnitSelection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            weightunit.adapter = adapter
            val defaultWeightUnitPosition = adapter.getPosition("Please Select")
            weightunit.setSelection(defaultWeightUnitPosition)

            // ... (rest of the code)
        } else {

        }
       // val patientheighteditb = findViewById<EditText>(R.id.patientheightedit)
       // val patientweighteditb = findViewById<EditText>(R.id.patientweightedit)
        val raceeditb =  findViewById<Spinner>(R.id.raceedit)
        if (raceeditb != null) {
            val adapter = ArrayAdapter.createFromResource(this, R.array.RaceSelection, android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            raceeditb.adapter = adapter
            val defaultRacePosition = adapter.getPosition("Please Select")
            raceeditb.setSelection(defaultRacePosition)

            // ... (rest of the code)
        } else {

        }
        val diabetesb = findViewById<RadioGroup>(R.id.diabetes)
        val diabyesb = findViewById<Button>(R.id.diabyes)
        val diabnob = findViewById<Button>(R.id.diabno)

        diabetesb.setOnCheckedChangeListener { group, checkedId ->

                when (checkedId) {
                    R.id.diabyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        diabetesc=1
                    }
                    R.id.diabno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val htnb = findViewById<RadioGroup>(R.id.htn)


        htnb.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.htnyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        htnc=1
                    }
                    R.id.htnno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val asthmab = findViewById<RadioGroup>(R.id.asthma)


        asthmab.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.asthmayes -> {
                        // Handle click for Button 1
                        // Your logic here
                        asthmac=1
                    }
                    R.id.asthmano -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val smokeb = findViewById<RadioGroup>(R.id.smoke)


        smokeb.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.smokeyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        smokec=1
                    }
                    R.id.smokeno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val alcoholb = findViewById<RadioGroup>(R.id.alcohol)


        alcoholb.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.alcoholyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        alcoholc=1
                    }
                    R.id.alcoholno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val mib = findViewById<RadioGroup>(R.id.mi)


        mib.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.miyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        mic=1
                    }
                    R.id.mino -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val cvab = findViewById<RadioGroup>(R.id.cva)


        cvab.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cvayes -> {
                        // Handle click for Button 1
                        // Your logic here
                        cvac=1
                    }
                    R.id.cvano -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val stentb = findViewById<RadioGroup>(R.id.stent)


        stentb.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.stentyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        stentc=1
                    }
                    R.id.stentno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }

        val toggleButton = findViewById<RadioGroup>(R.id.cabg)

        toggleButton.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.cabgyes -> {
                        // Handle click for Button 1
                        // Your logic here
                        cabgc=1
                    }
                    R.id.cabgno -> {
                        // Handle click for Button 2
                        // Your logic here
                    }
                    // Add more cases for other buttons if needed
                }
            }







        //Closing
        cancelbu.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
       // val bmic=calculateBMI(patientheighteditb.toString(),patientweighteditb.toString())

        //Registering Patient Details
            addbu.setOnClickListener {
                val PatientId: String = patientideditb.text.toString()
                val PatientName: String = patientnameeditb.text.toString()
                val PatientAge: String = patientageeditb.text.toString()
                val PatientHeight: String = heightPicker.toString()
                val PatientWeight: String = weightPicker.toString()
                val BMI: String = "".toString()
                val PatientGender: String = patientgendereditb
                val BloodGroup: String = bloodgroupeditb.selectedItem.toString()
                val Race: String = raceeditb.selectedItem.toString()
                patient.setPatientId(PatientName)
                patient.setAge(PatientAge)
                patient.setGender(PatientGender)
                patient.setBloodGroup(BloodGroup)

                if (PatientId.isNotEmpty()&&PatientName.isNotEmpty() && PatientAge.isNotEmpty() && PatientGender.isNotEmpty() && BloodGroup.isNotEmpty()&&PatientHeight.isNotEmpty()&&PatientWeight.isNotEmpty()) {
                    val age: Int = PatientAge.toInt()
                    val gend: String = PatientGender
                    val bg: String = BloodGroup
                    val race:String=Race
                    val calendar1 = Calendar.getInstance()
                    val dateFormat1 = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())

                    databaseReference.child("Patients").child(patient.getPatientId()).setValue(patient)
                        .addOnSuccessListener {
                            Log.d("Database Write", "Data written successfully") }

                    val calendar = Calendar.getInstance()
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val items = Patientlist(PatientId, PatientName, gend, age, bg,race,dateFormat.format(calendar.time),PatientHeight,PatientWeight,BMI,diabetesc,htnc,asthmac,smokec,alcoholc,mic,cvac,stentc,cabgc)
                    patientViewModal.insertitem(items)
                    Toast.makeText(
                        applicationContext,
                        "Patient inserted successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, USBClass::class.java)
                    intent.putExtra("patient id", PatientName)
                    intent.putExtra("visit id", "1")
                    intent.putExtra("date", dateFormat.format(calendar.time))
                    intent.putExtra("time", timeFormat.format(calendar.time))
                    Log.d("patname",PatientName)
                    startActivity(intent)

                }
                else{
                    Toast.makeText(applicationContext,"Please enter all of the details....", Toast.LENGTH_SHORT).show()
                  }}




        }
    override fun OnItemClick(grocerylist: Patientlist) {
        patientViewModal.deleteitem(grocerylist)
        patientRecyclerAdaptor.notifyDataSetChanged()

        Toast.makeText(applicationContext,"Grocery removed successfully",Toast.LENGTH_SHORT).show()
    }

    }

//Possible Usage for cloud database integration
private fun getProjectNameFromStorageBucket(storageBucket: String): String {
    // Assuming the storageBucket URL is in the format: gs://your-project-name.appspot.com
    val parts = storageBucket.split("/".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
    return if (parts.size > 1) {
        parts[1].replace(".appspot.com", "")
    } else "Unknown"
}
fun calculateBMI(weightStr: String, heightStr: String): Float {
    try {
        val weight = weightStr.toFloat()
        val height = heightStr.toFloat()

        if (height <= 0) {
            throw IllegalArgumentException("Height must be greater than zero.")
        }

        return weight / (height * height)
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("Invalid input. Please enter valid numeric values for weight and height.")
    }
}


