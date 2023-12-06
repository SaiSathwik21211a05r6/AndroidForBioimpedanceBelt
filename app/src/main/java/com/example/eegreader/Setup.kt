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
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.ClinicalDB.ClinicalList
import com.example.eegreader.ClinicalDB1.ClinicalDatabase1
import com.example.eegreader.ClinicalDB1.ClinicalList1
import com.example.eegreader.ClinicalDB1.ClinicalRepository1
import com.example.eegreader.ClinicalDB1.ClinicalViewModal1
import com.example.eegreader.ClinicalDB1.ClinicalViewModalFactory1
import com.example.eegreader.RecyclerView.PatientRecyclerAdaptor
import com.example.eegreader.USB.USBClass
import com.example.eegreader.database.NoSQL.Patient
import com.example.eegreader.database.Patientlist
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Setup: AppCompatActivity() {

    lateinit var itemsRV: RecyclerView
    lateinit var add: FloatingActionButton
    lateinit var list:List<Patientlist>
    lateinit var patientRecyclerAdaptor: PatientRecyclerAdaptor
    lateinit var clinicalViewModal: ClinicalViewModal1
    private var pid: TextView? = null
    private var vid: TextView? = null
    private var vtime: TextView? = null
    private var vdate: TextView? = null
    var readingsjson: JSONObject?=null
    var patiiid: String?=null
    var visitid:String?=null
    var visitdate:String?=null
    var visittime:String?=null
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
        setContentView(R.layout.setup)
        list = ArrayList<Patientlist>()

        val patient = Patient()

        //Cloud
        val projectId = FirebaseApp.getInstance().options.projectId
        val storageBucket = FirebaseApp.getInstance().options.storageBucket
        val projectName = getProjectNameFromStorageBucket(storageBucket!!)
        patiiid = intent.getStringExtra("patient id")
        visitid = intent.getStringExtra("visit id")
        Log.d("gotten",visitid.toString())
// dbHelper = DatabaseHelper(this)
        visitdate = intent.getStringExtra("date")
        visittime = intent.getStringExtra("time")

        //Offline Database
        val groceryRepository = ClinicalRepository1(ClinicalDatabase1(this))
        val factory = ClinicalViewModalFactory1(groceryRepository)
        clinicalViewModal = ViewModelProvider(this, factory).get(ClinicalViewModal1::class.java)


        //View components on the dialog box

        val addbu = findViewById<Button>(R.id.cancelbutton)

            /** val patientheighteditb = findViewById<EditText>(R.id.patientheightedit)
        val patientweighteditb = findViewById<EditText>(R.id.patientweightedit)*/

        val diabetesb = findViewById<RadioGroup>(R.id.diabetes)
        val diabyesb = findViewById<Button>(R.id.diabyes)
        val diabnob = findViewById<Button>(R.id.diabno)
        pid = findViewById<TextView>(R.id.patid)
        vid = findViewById<TextView>(R.id.vid)
        vtime = findViewById<TextView>(R.id.vtime)
        vdate = findViewById<TextView>(R.id.vdate)
        pid?.setText(patiiid)
        vid?.setText(visitid)
        vtime?.setText(visittime)
        vdate?.setText(visitdate)
        val heightPicker = findViewById<NumberPicker>(R.id.heightPicker)
        val weightPicker = findViewById<NumberPicker>(R.id.weightPicker)
        val heightunit=findViewById<Spinner>(R.id.heightunitedit)
        val weightunit=findViewById<Spinner>(R.id.weightunitedit)
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






        val readingsjson = JSONObject()
        //Closing

        // val bmic=calculateBMI(patientheighteditb.toString(),patientweighteditb.toString())

        //Registering Patient Details
        addbu.setOnClickListener {

            val PatientHeight: String = heightPicker.value.toString()
            val PatientWeight: String = weightPicker.value.toString()
            val BMI: String = "".toString()


                if (PatientHeight.isNotEmpty()&&PatientWeight.isNotEmpty()) {

                    val calendar1 = Calendar.getInstance()
                    val dateFormat1 = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())


                    val calendar = Calendar.getInstance()
                    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
var readingsjson1=JSONArray()
                    readingsjson1.put(PatientHeight)
                    readingsjson1.put(PatientWeight)
                    readingsjson1.put(BMI)
                    readingsjson1.put(diabetesc)
                    readingsjson1.put(htnc)
                    readingsjson1.put(asthmac)
                    readingsjson1.put(smokec)
                    readingsjson1.put(alcoholc)
                    readingsjson1.put(mic)
                    readingsjson1.put(cvac)
                    readingsjson1.put(stentc)
                    readingsjson1.put(cabgc)

                    readingsjson?.put(visitdate + visittime.toString(), readingsjson1)
                    Log.d("readingsjson is", readingsjson.toString())

                    val items = patiiid?.let { it1 ->
                        visittime?.let { it2 ->
                            visitdate?.let { it3 ->
                                ClinicalList1(
                                    it1,
                                    readingsjson.toString()
                                )
                            }
                        }
                    }
                    if (items != null) {
                        val thread = Thread {
                            Log.d("database", clinicalViewModal.toString())
                            patiiid?.let { it1 -> clinicalViewModal.insertData(items) }
                        }

                        thread.start()

                    }
                    Toast.makeText(
                        applicationContext,
                        "Patient inserted successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    showChestSizeDialog()
                }


               }



    }
    interface ChestSizeListener {
        fun onChestSizeEntered(chestSize: String)
    }

    private fun showChestSizeDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.chestsize, null)

        val editTextChestSize: EditText = dialogView.findViewById(R.id.chestsizeedit)

        builder.setView(dialogView)
            .setTitle("Enter Chest Size")
            .setPositiveButton("OK") { dialog, _ ->
                val chestSize = editTextChestSize.text.toString()
                // Pass the chestSize value to your USBClass or handle it as needed
                // For example, you can start a new activity and pass the value as an extra
                // val intent = Intent(this, YourUsbClassActivity::class.java)
                // intent.putExtra("chestSize", chestSize)
                // startActivity(intent)
                val intent = Intent(this, USBClass::class.java)
                //   intent.putExtra("patient id", PatientName)
                intent.putExtra("patient id", patiiid)
                intent.putExtra("visit id", visitid.toString())
                intent.putExtra("date", visitdate.toString())
                intent.putExtra("time", visittime.toString())

                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun OnItemClick(grocerylist: ClinicalList1) {
        clinicalViewModal.deleteData(grocerylist)
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
fun setBMI(weightStr: String, heightStr: String): Float {
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


