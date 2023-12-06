package com.example.eegreader




import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ComponentName
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eegreader.BLEModule.*
import com.example.eegreader.RecyclerView.VisitId
import com.example.eegreader.RecyclerView.patientid
import com.example.eegreader.USB.USBClass
import com.example.eegreader.database.NoSQL.Visit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.np.lekotlin.blemodule.BLEConnectionManager
import com.np.lekotlin.blemodule.BLEDeviceManager
import java.text.SimpleDateFormat
import java.util.*


class RecordScreen : AppCompatActivity(), OnDeviceScanListener, View.OnClickListener {
    private lateinit var patiid: String
    private lateinit var visitid: String
    private lateinit var visitdate: String
    private lateinit var visittime: String
    private lateinit var FreqStart: String
    private lateinit var FreqEnd: String
    private lateinit var FreqStep: String
    private lateinit var TimeFrame: String
    private lateinit var mBtnReadConnectionChar: Button
    var s: Int = 1

    val databaseReference = FirebaseDatabase.getInstance().getReference()

    /** private lateinit var mBtnReadBatteryLevel: Button
    private lateinit var mBtnReadEmergency: Button
    private lateinit var mBtnWriteEmergency: Button
    private lateinit var mBtnWriteConnection: Button
    private lateinit var mBtnWriteBatteryLevel: Button
    private lateinit var mTvResult: TextView*/

    private lateinit var dataArray: String
    private var mDeviceAddress: String = ""
    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    private val RUNTIME_PERMISSION_REQUEST_CODE = 2
    override fun onScanCompleted(deviceDataList: BleDeviceData) {
        val bluetoothAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$".toRegex()


        if (deviceDataList.mDeviceAddress.matches(bluetoothAddressPattern)) {
            mDeviceAddress = deviceDataList.mDeviceAddress
            BLEConnectionManager.connect(deviceDataList.mDeviceAddress)
        } else {
            Log.e(TAG, "Invalid Bluetooth address: ${deviceDataList.mDeviceAddress}")
        }
    }


    /* private val scanResultAdapter: ScanResultAdapter by lazy {
         ScanResultAdapter(scanResults) { result ->
             // User tapped on a scan result
             if (isScanning) {
                 stopBleScan()
             }
             with(result.device) {
                 Log.w("ScanResultAdapter", "Connecting to $address")
                 connectGatt(context, false, gattCallback)
             }
         }
     }*/
    /* private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address


            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    // TODO: Store a reference to BluetoothGatt
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w(
                    "BluetoothGattCallback",
                    "Error $status encountered for $deviceAddress! Disconnecting..."
                )
                gatt.close()
            }
        }
    }*/
    private val REQUEST_LOCATION_PERMISSION = 2018
    private val TAG = "RecordScreen"
    private val REQUEST_ENABLE_BT = 1000
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }


    // From the previous section:


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recordscreen)

        patiid = intent.getStringExtra("patid").toString()

        visitid = intent.getStringExtra("visitid").toString()
        visitdate = intent.getStringExtra("visitdate").toString()
        visittime=intent.getStringExtra("visittime").toString()

        var commchanneditb: String = ""

        val cancelbu = findViewById<Button>(R.id.record)

        //   freqstepeditb = findViewById<EditText>(R.id.freqstepedit)
        //  timeframeeditb = findViewById<EditText>(R.id.timeframeedit)
        mBtnReadConnectionChar = findViewById<Button>(R.id.record)



        mBtnReadConnectionChar.setOnClickListener(this)




      //  dataArray = ArrayList().toString()




        var genderRadioGroup: RadioGroup? = findViewById(R.id.genderedit)
        val usbRadioButton = findViewById<RadioButton>(R.id.usb)
        val bleRadioButton = findViewById<RadioButton>(R.id.ble)
        val sendBtn = findViewById<View>(R.id.send_btn)

        genderRadioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.usb) {
                s=0
                // USB Radio button is selected
                // Handle USB selection here
            } else if (checkedId == R.id.ble) {
                s=1
                // Bluetooth Radio button is selected
                // Handle Bluetooth selection here

            }
        }

// If you want to set a default selection (e.g., USB)

// If you want to set a default selection (e.g., USB)
        usbRadioButton.isChecked =
            true // You can set Bluetooth as the default selection if you prefer


        checkLocationPermission()

    }
    private lateinit var bleService: BLEService
    private var isBound = false
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            val binder = service as BLEService.LocalBinder
            bleService = binder.getService()
            isBound = true
          //  bleService.registerDataCallback(this@RecordScreen)
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
    fun addVisitForPatient(patientId: String) {
        // Check if the patient already exists in the database
        val patientRef = databaseReference.child("Patients").child(patientId)

        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The patient exists, now get the number of existing visits
                    val visitsRef = patientRef.child("Visits")
                    val visitCount = dataSnapshot.child("Visits").childrenCount.toInt()
                    val newVisitNumber = visitCount + 1

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

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    //searching for devices
    private val scanCallback = object : ScanCallback() {


        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            if (result.device.name?.trim() == "=BLEMED" && isValidBluetoothAddress(result.device.address)) {
                // Initiate connection

                stopBLEScan()
                connectDevice(result.device.address)

            }
            else{

            }
        }


        override fun onScanFailed(errorCode: Int) {

            when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> {
                   stopBLEScan()
                    startBLEScan()
                }

                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> {

                }

            }
        }
    }

    private fun isValidBluetoothAddress(address: String?): Boolean {
        if (address == null) {
            return false
        }

        val addressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"
        val reversedAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"
        return address.matches(Regex(addressPattern)) || address.matches(
            Regex(
                reversedAddressPattern
            )
        )
    }

    private val MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN = 1

    @SuppressLint("MissingPermission")
    private fun startBLEScan() {
        if (!hasRequiredRuntimePermissions()) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN)
           // requestRelevantRuntimePermissions()
        } }else {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
            bleScanner.startScan(null, scanSettings, scanCallback)


            // You can optionally add a toast message to indicate that scanning has started
            Toast.makeText(this, "Scanning started", Toast.LENGTH_SHORT).show()

        }
    }

    @SuppressLint("MissingPermission")
    private fun stopBLEScan() {
        try {
            bleScanner.stopScan(scanCallback)
            Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle the exception (e.g., log an error)

        }
        Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show()
    }

    /**
     * Check the Location Permission before calling the BLE API's
     */
    private fun checkLocationPermission() {
        if (isAboveMarshmallow()) {
            when {
                isLocationPermissionEnabled() -> initBLEModule()
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> displayRationale()

                else -> requestLocationPermission()
            }
        } else {
            initBLEModule()
        }
    }


    /**
     * Request Location API
     * If the request go to Android system and the System will throw a dialog message
     * user can accept or decline the permission from there
     */


    /**
     * If the user decline the Permission request and tick the never ask again message
     * Then the application can't proceed further steps
     * In such situation- App need to prompt the user to do the change form Settings Manually
     */
    private fun displayRationale() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.location_permission_disabled))
            .setPositiveButton(
                getString(R.string.ok)
            ) { _, _ -> requestLocationPermission() }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }
            .show()
    }


    /**
     * If the user either accept or reject the Permission- The requested App will get a callback
     * Form the call back we can filter the user response with the help of request key
     * If the user accept the same- We can proceed further steps
     */
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (permissions.size != 1 || grantResults.size != 1) {
                    throw RuntimeException("Error on requesting location permission.")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBLEModule()
                } else {
                    Toast.makeText(
                        this, R.string.location_permission_not_granted,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    /**
     * Check with the system- If the permission already enabled or not
     */
    private fun isLocationPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    /**
     * The location permission is incorporated in Marshmallow and Above
     */
    private fun isAboveMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    /**
     *After receive the Location Permission, the Application need to initialize the
     * BLE Module and BLE Service
     */
    @SuppressLint("MissingPermission")
    private fun initBLEModule() {
        // BLE initialization
        if (!BLEDeviceManager.init(this)) {
            Toast.makeText(this, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show()
            return
        }
        registerServiceReceiver()
        BLEDeviceManager.setListener(this)


        if (!BLEDeviceManager.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }


        BLEConnectionManager.initBLEService(this)
    }

    //potentially useful
    fun addVisitForPatient(patientId: Int) {
        // Check if the patient already exists in the database
        val patientRef = databaseReference.child("Patients").child(patientId.toString())
        patientid =patientId

        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The patient exists, now get the number of existing visits
                    val visitsRef = patientRef.child("Visits")
                    val visitCount = dataSnapshot.child("Visits").childrenCount.toInt()
                    val newVisitNumber = visitCount + 1

                    VisitId = newVisitNumber.toString()

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

    /**
     * Register GATT update receiver
     */
    private fun registerServiceReceiver() {
        //      this.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }


    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BLEConstants.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)


        return intentFilter
    }


    /**
     * Unregister GATT update receiver
     */
    private fun unRegisterServiceReceiver() {
        try {
            // this.unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            //May get an exception while user denies the permission and user exists the app

        }


    }


    override fun onDestroy() {
        super.onDestroy()
        BLEConnectionManager.disconnect()
        unRegisterServiceReceiver()
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.record -> {
                if (!BLEDeviceManager.isEnabled()) {
                // Bluetooth is not enabled, prompt the user to enable it
               val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
               startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                checkLocationPermission() }
               else {
addVisitForPatient(patiid)
               /* dataArray.add(freqStartValue)
                dataArray.add(freqEndValue)
                dataArray.add(freqStepValue)
                dataArray.add(timeFrameValue)*/

                // Bluetooth is already enabled, you can proceed with BLE device scanning

                dataArray=(   (sendText?.text.toString() + "," + sendText1?.getText()
                    .toString() + "," + sendText2?.getText().toString() + "," + sendText3?.getText()
                    .toString()))


if(s!=0){
                    startBLEScan()}
                else
{

    val intent = Intent(this, USBClass::class.java)
    intent.putExtra("patient id",patiid)
    intent.putExtra("visit id",visitid)
    intent.putExtra("visitdate",visitdate)
    intent.putExtra("visittime",visittime)
    startActivity(intent)
}





            }
            }

        }
    }


    /**
     * Connect the application with BLE device with selected device address.
     */
    private fun connectDevice(deviceAddress: String) {
        Handler().postDelayed({
            BLEConnectionManager.initBLEService(this)
            if (BLEConnectionManager.connect(deviceAddress)) {
                Toast.makeText(this, "DEVICE CONNECTED", Toast.LENGTH_SHORT).show()
                 // Create your data array

            val characteristicUuid: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
                sendArrayToBLEService(dataArray, characteristicUuid,deviceAddress)
            } else {
                Toast.makeText(this, "DEVICE CONNECTION FAILED", Toast.LENGTH_SHORT).show()
            }
        }, 100)


    }

    private fun sendArrayToBLEService(
        dataArray: String, characteristicUuid: UUID,
        deviceAddress:String) {



        val bleServiceIntent = Intent(this, BLEService::class.java)
        bleServiceIntent.putExtra("characteristicUuid", characteristicUuid)

        bleServiceIntent.putExtra("dataArray", dataArray)
        bleServiceIntent.putExtra("deviceAddress", deviceAddress)
        startService(bleServiceIntent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }



    // Callback for BluetoothGattCallback
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Loop through services
                for (service in gatt?.services.orEmpty()) {
                    // Loop through characteristics within the service
                    for (characteristic in service.characteristics) {
                        // Handle the characteristic as needed
                        // For example, send data to the characteristic
                        sendDataToCharacteristic(characteristic)
                    }
                }
            }
        }
    }

    private fun sendDataToCharacteristic(characteristic: BluetoothGattCharacteristic) {
        val dataArray: ArrayList<Double> = ArrayList()
        // Create your data array
        val bleServiceIntent = Intent(this, BLEService::class.java)
        bleServiceIntent.putExtra("characteristicUuid", characteristic.uuid.toString())
        bleServiceIntent.putExtra("dataArray", dataArray)
        startService(bleServiceIntent)
    }



    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }



    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }
    private fun Activity.requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) { return }
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {

                requestLocationPermission()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {

                requestLocationPermission()
            }
        }
    }
    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }
    fun Context.hasRequiredRuntimePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }




    private fun requestLocationPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val rationale = "Starting from Android 12, the app needs location access to scan for BLE devices."
        val PERMISSION_REQUEST_CODE = 123 // Replace 123 with any unique integer value

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, no need to show the dialog
            // You can proceed with your logic here

            startBLEScan()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // Show an explanation dialog if needed
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    setTitle("Location permission required")
                    setMessage(rationale)
                    setCancelable(false)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        // Request the permission using the appropriate API
                        ActivityCompat.requestPermissions(this@RecordScreen, arrayOf(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), PERMISSION_REQUEST_CODE)
                    }
                    create().show()
                }
            } else {
                // Request the permission directly, without showing an explanation
                ActivityCompat.requestPermissions(
                    this, arrayOf(permission),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun requestBluetoothPermissions() {
        runOnUiThread {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.apply {
                setTitle("Location permission required")
                setMessage("Starting from Android 12, the app needs location access to scan for BLE devices.")
                setCancelable(false)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    requestLocationPermission()
                }
                create().show()
            }
        }
    }




}
