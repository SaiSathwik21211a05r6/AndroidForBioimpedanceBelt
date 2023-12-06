package com.example.eegreader

//Importing Necessary files
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//For the dialog window to add new grocery in the app
import android.app.Dialog
import android.content.Context
import android.util.Log

//Layout Components
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider

//Presenting the list of groceries
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.RecyclerView.PatientRecyclerAdaptor
import com.example.eegreader.RecyclerView.StatusRecyclerAdaptor
import com.example.eegreader.RecyclerView.patientid
import com.example.eegreader.database.*
import java.lang.Exception


class PatientList: AppCompatActivity(), PatientRecyclerAdaptor.PatientListClickInterface{
    //Variables involving other files of the project
    lateinit var itemsRV:RecyclerView

    lateinit var list:List<Patientlist>
    lateinit var patientRecyclerAdaptor: PatientRecyclerAdaptor
    lateinit var patientViewModal: PatientViewModal


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patientlist)
        //Views on the app UI
        itemsRV = findViewById(R.id.recview)
        itemsRV.layoutManager = LinearLayoutManager(this)
        list = ArrayList<Patientlist>()
        patientRecyclerAdaptor = PatientRecyclerAdaptor(list, this)
        itemsRV.adapter = patientRecyclerAdaptor





    patientRecyclerAdaptor.notifyDataSetChanged()
        //Initialising databases required for storing grocery lists
        val groceryRepository = PatientRepository(PatientDatabase(this))
        val factory = PatientViewModalFactory(groceryRepository)
        patientViewModal = ViewModelProvider(this, factory).get(PatientViewModal::class.java)
        patientViewModal.getthelistofpatients().observe(
            this,
            {

                patientRecyclerAdaptor.list = it.filter {

try{Log.d("number", it.dor?.substring(5,7).toString())
                    (!isPatientIdNumeric(it.patientName)|| it.patientName=="12454")&& it.dor?.substring(5,7)?.toInt()!! <12
                }
catch (e:Exception)
{   if(it.patientName=="12454")
    true
    else
        false

}
                }
                patientRecyclerAdaptor.notifyDataSetChanged()
            }

        )

    }

    private fun isPatientIdNumeric(patientId: String?): Boolean {
        return try {
            patientId?.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    //Code for dialog box to add new grocery
 /**   fun openDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.addpatient)

        //View components on the dialog box
        val cancelbu = dialog.findViewById<Button>(R.id.cancelbutton)
        val addbu = dialog.findViewById<Button>(R.id.add)

        val patientnameeditb = dialog.findViewById<EditText>(R.id.patientnameedit)
        val patientageeditb = dialog.findViewById<EditText>(R.id.patientageedit)
        val patientgendereditb = dialog.findViewById<EditText>(R.id.genderedit)
        val bloodgroupeditb = dialog.findViewById<EditText>(R.id.bloodgroupedit)

        //Initialising cancel buttons
        cancelbu.setOnClickListener {
            dialog.dismiss()

        }

        //Initialising add button
        addbu.setOnClickListener {
            val PatientName: String = patientnameeditb.text.toString()
            val PatientAge: String = patientageeditb.text.toString()
            val PatientGender: String = patientgendereditb.text.toString()
            val BloodGroup: String = bloodgroupeditb.text.toString()
            val age: Int = PatientAge.toInt()
            val gend: String = PatientGender
            val bg: String = BloodGroup
            if (PatientName.isNotEmpty() && PatientAge.isNotEmpty() && PatientGender.isNotEmpty() && BloodGroup.isNotEmpty()) {
                val items = Patientlist(PatientId,PatientName, age, gend, bg)
                patientViewModal.insertitem(items)
                Toast.makeText(
                    applicationContext,
                    "Patient inserted successfully...",
                    Toast.LENGTH_SHORT
                ).show()
                patientRecyclerAdaptor.notifyDataSetChanged()
                dialog.dismiss()
            }
            else{
                Toast.makeText(applicationContext,"Please enter all of the details....",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()

    }
*/

    override fun OnItemClick(patientlist: Patientlist) {
        patientViewModal.deleteitem(patientlist)
        patientRecyclerAdaptor.notifyDataSetChanged()

        Toast.makeText(applicationContext,"Patient data removed successfully",Toast.LENGTH_SHORT).show()
    }


}
