package com.example.eegreader

import android.Manifest
import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.eegreader.BLEModule.*

import com.example.eegreader.BLEModule.OnDeviceScanListener
import com.example.eegreader.RecyclerView.StatusRecyclerAdaptor
import com.example.eegreader.database.PatientViewModal
import com.example.eegreader.database.Patientlist
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {

    lateinit var itemsRV: RecyclerView
    lateinit var add: FloatingActionButton
    private val PERMISSION_REQUEST_CODE = 123
    private val BLUETOOTH_CONNECT_PERMISSION_CODE = 1
    lateinit var list:List<Patientlist>
    lateinit var patientRecyclerAdaptor: StatusRecyclerAdaptor
    lateinit var patientViewModal: PatientViewModal
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            FirebaseApp.initializeApp(this)


                requestPermissions()

        if (!isBluetoothEnabled() || !isLocationEnabled()) {
            showEnableBluetoothAndLocationDialog()

        }


        var patientlist = findViewById<Button>(R.id.viewpatientlist)
            var patientregis = findViewById<Button>(R.id.patientregistration)

            patientlist.setOnClickListener{
                val intent = Intent(this, PatientList::class.java)
                startActivity(intent)
            }
            patientregis.setOnClickListener{

                // Ensure permissions are granted before opening the dialog
                if (arePermissionsGranted()) {

                    val intent = Intent(this, AddPatient::class.java)
                    startActivity(intent)
                } else {
                    Log.d("hi","opening")
                    // Request permissions before opening the dialog
                    requestPermissions()
                }
            }





    }
    //required settings
    private fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun showEnableBluetoothAndLocationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Please enable Bluetooth and Location services to use this app.")
        builder.setPositiveButton("Enable") { _, _ ->
            // Open Bluetooth and Location settings to allow the user to enable them
            val enableBluetoothIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            val enableLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            try {
                startActivity(enableBluetoothIntent)
                startActivity(enableLocationIntent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where the settings activity is not found
                // You can choose to handle this differently based on your app's requirements
            }
        }
        builder.setNegativeButton("Exit") { _, _ ->
            // Close the app if the user declines to enable Bluetooth and Location
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }

//permissions
    private val STORAGE_PERMISSION_CODE = 101

    private fun checkStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
            // You have storage permission; you can access, read, and modify files here.
            // Example: val file = File(Environment.getExternalStorageDirectory(), "example.txt")
        } else {
            // You don't have storage permission; request it.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            return false;
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted; you can access, read, and modify files here.
            } else {
                // Storage permission denied; handle this as needed, e.g., show a message to the user.
            }
        }
    }

    private fun arePermissionsGranted(): Boolean {
        // Check if Bluetooth and Location permissions are granted
        val bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
        val locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val bluetoothConnectPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

        return bluetoothPermission  //locationPermission && bluetoothConnectPermission
    }

    private fun requestPermissions() {
        // Request Bluetooth, Location, and Bluetooth Connect permissions
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
          //  Manifest.permission.READ_EXTERNAL_STORAGE,
          //  Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), PERMISSION_REQUEST_CODE)
    }
    private fun showEnableStoragePermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Please enable storage permissions to use this app.")
        builder.setPositiveButton("Enable") { _, _ ->
            // Open app settings to allow the user to enable storage permissions
            val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            appSettingsIntent.data = uri
            try {
                startActivity(appSettingsIntent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where the settings activity is not found
                // You can choose to handle this differently based on your app's requirements
            }
        }
        builder.setNegativeButton("Exit") { _, _ ->
            // Close the app if the user declines to enable storage permissions
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }


    }


    /**
     * Check the Location Permission before calling the BLE API's
     */



