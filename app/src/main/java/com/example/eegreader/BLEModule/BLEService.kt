package com.example.eegreader.BLEModule

import android.annotation.SuppressLint
import android.app.Service

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.eegreader.Readings
import com.example.eegreader.RecyclerView.ReadingsViewModel
import com.np.lekotlin.blemodule.BLEConnectionManager
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.UUID


class BLEService: Service() {

private lateinit var intent1:Intent
    private val TAG = "BLE"
    private lateinit var byteArray1:ByteArray
  private lateinit var byteArray:ByteArray
    private var servicesDiscovered = false
    private var readingsActivity: Readings? = null

    private val mBinder = LocalBinder()//Binder for Activity that binds to this Service
    private var mBluetoothManager: BluetoothManager? =
        null//BluetoothManager used to get the BluetoothAdapter
    private var mBluetoothAdapter: BluetoothAdapter? =
        null//The BluetoothAdapter controls the BLE radio in the phone/tablet
    private var mBluetoothGatt: BluetoothGatt? =
        null//BluetoothGatt controls the Bluetooth communication link
    private var mBluetoothDeviceAddress: String? = null//Address of the connected BLE device
    private val mCompleResponseByte = ByteArray(100)
    private var activityStarted = false
    private val mHandler = Handler()
    private val binder = LocalBinder()
    private lateinit var viewModel: ReadingsViewModel

    companion object {
        const val ACTION_DATA_AVAILABLE = "BLEConstants.ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "com.example.eegreader.EXTRA_DATA"
    }

    interface DataCallback {

        fun onDataReceived(data: MutableList<ByteArray>)
    }
    private var dataCallback: DataCallback? = null
    fun registerDataCallback(callback: Readings) {
        dataCallback = callback
    }

    fun unregisterDataCallback() {
        dataCallback = null
    }
    //val myDataCallback = DataCallback
    private fun updateDataInViewModel(newDataList: MutableList<String>) {
        // Use the ViewModel method to set the new data
        viewModel.setData(newDataList)

        // If you want to add a single data item, use the addDataItem method
        // viewModel.addDataItem(newDataItem)

        // If you want to clear the data, use the clearData method
        // viewModel.clearData()
    }

    // Example method to receive data and update LiveData
    fun onDataReceivedFromBluetooth(data: MutableList<ByteArray>) {
        // Create a new list to hold the updated data
        val newDataList = mutableListOf<String>()

        // Iterate through the received data list
        for (element in data) {
            // Convert the ByteArray to a string using UTF-8 encoding
            val strData = String(element, Charsets.UTF_8)

            // Add the string to the new data list
            newDataList.add(strData)
        }

        // Update the LiveData with the new data
        updateDataInViewModel(newDataList)

        // ... (other onDataReceivedFromBluetooth code)
    }

    //Bluetooth communication settings
    private val mGattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {//Change in connection state

            if (newState == BluetoothProfile.STATE_CONNECTED) {//See if we are connected

                broadcastUpdate(BLEConstants.ACTION_GATT_CONNECTED)//Go broadcast an intent to say we are connected
Log.d("disc","serv")
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.d("status", newState.toString())
                mBluetoothGatt?.discoverServices()},2000)//Discover services on connected BLE device
            }

            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//See if we are not connectedLog.i(TAG, "**ACTION_SERVICE_DISCONNECTED**" + status);
Log.d("state","disconn")
                broadcastUpdate(BLEConstants.ACTION_GATT_DISCONNECTED)
             //   refreshDeviceCache(deviceGatt);//Go broadcast an intent to say we are disconnected
            }
        }

        //For information only. This application uses Indication to receive updated characteristic data, not Read
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) { //A request to Read has completed

            val data = characteristic.value
            val values = characteristic.value
            val clearValue = byteArrayOf(255.toByte())
            var value = 0
            var value1 = (values[0] ); // Parse byte to integer
            var value2 = (values[1] );
            var value3 = (values[2] );
            if (null != values) {
                Log.i(TAG, "ACTION_DATA_READ VALUE: " + values.size)


                Log.i(TAG, "ACTION_DATA_READ VALUE 1: " + value1);
                Log.i(TAG, "ACTION_DATA_READ VALUE 2: " + value2);
                Log.i(TAG, "ACTION_DATA_READ VALUE 3: " + value3);








            }

            BLEConnectionManager.writeEmergencyGatt(clearValue)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //See if the read was successful

                broadcastUpdate(
                    BLEConstants.ACTION_DATA_AVAILABLE,
                    characteristic
                )                 //Go broadcast an intent with the characteristic data
            } else {

            }

        }


        //For information only. This application sends small packets infrequently and does not need to know what the previous write completed
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) { //A request to Write has completed

            if (status == BluetoothGatt.GATT_SUCCESS) {                                 //See if the write was successful


Log.d("writing","success")
            }
            Log.d("failed",status.toString())



        }

        val receivedData = mutableListOf<ByteArray>()

     //Response from the device
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic?
        ) {



            if (characteristic != null && characteristic.properties == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {

            }
            if (characteristic != null) {

                val receivedData = String(characteristic.value, )
                Charset.forName("UTF-8")

            }

            val responseData = characteristic?.value

            // Process the received data as needed
            responseData?.let { receivedData.add(it) }

Log.d("response data",responseData.toString())
                // All data has been received, display or process it

            if (characteristic != null) {
                broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic)
            }


            //Go broadcast an intent with the characteristic data
        }

        // Write to a given characteristic. The completion of the write is reported asynchronously through the
        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("Descriptor write","was successful")

            } else {
                // Descriptor write failed

            }
        }


//Discovering device functionalities
@SuppressLint("Missing Permission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            var characteristic: BluetoothGattCharacteristic? = null
    Log.d("serv","here")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("serv", gatt?.services.toString())
                gatt?.services?.forEach { gattService ->
                    gattService.characteristics.forEach { mCharacteristic ->
if(mCharacteristic?.uuid==knownCharacteristicUUID)
                        mCharacteristic?.let { setCharacteristicIndication(it, true) }
                        characteristic = mCharacteristic
Log.d("characteristics are",mCharacteristic.uuid.toString())

                        Log.d("characteristics1 are",knownCharacteristicUUID.toString())          }

                    servicesDiscovered = true

                    if (characteristic?.uuid == knownCharacteristicUUID) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            // Find the desired characteristic by UUID

                            val desiredCharacteristic =
                                findCharacteristicByUUID(gatt, knownCharacteristicUUID)
Log.d("characteristic", byteArray.toString())
                            byteArray1="1".toByteArray()
                            desiredCharacteristic?.value = byteArray1
                            if (desiredCharacteristic !=
                                null) {
                                writeCharacteristic(desiredCharacteristic)
                            } else {

                            }
                        }, 3000)
                    }
                }








            } else {
                Log.e("MyBluetoothGattCallback", "onServicesDiscovered failed with status: $status")
            }
        }
    }
    private fun findCharacteristicByUUID(gatt: BluetoothGatt?, uuid: UUID): BluetoothGattCharacteristic? {
        // Find and return the characteristic with the specified UUID.
        return gatt?.services?.flatMap { it.characteristics }?.find { it.uuid == uuid }
    }
        // An activity has bound to this service
    override fun onBind(intent: Intent): IBinder? {

        return mBinder                                                                 //Return LocalBinder when an Activity binds to this Service
    }

    // An activity has unbound from this service
    @SuppressLint("MissingPermission")
    override fun onUnbind(intent: Intent): Boolean {

        if (mBluetoothGatt != null) {                                                   //Check for existing BluetoothGatt connection
            mBluetoothGatt!!.close()                                                     //Close BluetoothGatt coonection for proper cleanup
            mBluetoothGatt =
                null                                                      //No longer have a BluetoothGatt connection
        }

        return super.onUnbind(intent)
    }

    //connecting
    @SuppressLint("MissingPermission")
    fun connect(address: String?): Boolean {
        try {

            if (mBluetoothAdapter == null || address == null) {                             //Check that we have a Bluetooth adappter and device address
             //Log a warning that something went wrong
                return false                                                               //Failed to connect
            }

            // Previously connected device.  Try to reconnect.
            if (mBluetoothDeviceAddress != null && address == mBluetoothDeviceAddress && mBluetoothGatt != null) { //See if there was previous connection to the device

                //See if we can connect with the existing BluetoothGatt to connect
                //Success
                //Were not able to connect
                return mBluetoothGatt!!.connect()
            }

            val device = mBluetoothAdapter!!.getRemoteDevice(address)
                ?: //Check whether a device was returned
                return false                                                               //Failed to find the device
            //No previous device so get the Bluetooth device by referencing its address

            mBluetoothGatt = device.connectGatt(
                this,
                false,
                mGattCallback
            )                //Directly connect to the device so autoConnect is false
            mBluetoothDeviceAddress =
                address                                              //Record the address in case we need to reconnect with the existing BluetoothGatt

            return true
        } catch (e: Exception) {

        }





























        return false
    }

    // Broadcast an intent with a string representing an action
    private fun broadcastUpdate(action: String) {
        val intent =
            Intent(action)
                                                              //Broadcast the intent
    }
     var readingsActivityLaunched = false

    // Broadcast an intent with a string representing an action an extra string with the data
    // Modify this code for data that is not in a string format
    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {




        val data = characteristic.value
        Log.d("ble received", data.toString())
          val dataValue = String(data, Charsets.UTF_8) // Convert to a string
Log.d("dataValue",dataValue)
          // Create an Intent with the specified action
          val intent = Intent(ACTION_DATA_AVAILABLE)

          // Put the data as an extra in the intent
          intent.putExtra(EXTRA_DATA, dataValue)

          // Broadcast the intent
          sendBroadcast(intent)
      }


        // Broadcast the intent



    inner class LocalBinder : Binder() {
        internal fun getService(): BLEService {
            return this@BLEService//Return this instance of BluetoothLeService so clients can call its public methods
        }
    }
    val knownCharacteristicUUID = UUID.fromString("00002a57-0000-1000-8000-00805f9b34fb")
    val X_ACCEL_CHARACTERISTICS_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    // BLE Descriptors
    val X_ACCEL_DESCRIPTOR_UUID = UUID.fromString("3f542902-1ee0-4245-a7ef-35885ccae141")
    // Enable indication on a characteristic

   // Enable indication on a characteristic
   @SuppressLint("MissingPermission")
   fun setCharacteristicIndication(characteristic: BluetoothGattCharacteristic, enabled: Boolean) {
       try {
           if (mBluetoothAdapter == null || mBluetoothGatt == null) { //Check that we have a GATT connection

               return
           }

Log.d("Indicing char", X_ACCEL_CHARACTERISTICS_UUID.toString())
           Log.d("Indicing Descriptor", X_ACCEL_DESCRIPTOR_UUID.toString())
           mBluetoothGatt!!.setCharacteristicNotification(characteristic, true)//Enable notification and indication for the characteristic
           val descriptor =
               characteristic.getDescriptor(X_ACCEL_CHARACTERISTICS_UUID)


           descriptor.value =
               if (enabled) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else{
                   byteArrayOf(
                   0x00,
                   0x00
               )}

           if(mBluetoothGatt!!.writeDescriptor(descriptor))
             //Write the descriptor
           readingsActivityLaunched = false

       } catch (e: Exception) {

       }


   }

   //oothGattCallback onCharacteristic Wire callback method.

    // Write to a given characteristic. The completion of the write is reported asynchronously through the
    // BluetoothGattCallback onCharacteristic Wire callback method.
    @SuppressLint("MissingPermission")
    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic) {
        try {
Log.d("writing","vals")
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                Log.d("writing","valsf")
                return
            }
            val test =
                characteristic.properties                                      //Get the properties of the characteristic
            Log.d("writing","valsf2")
            if (test and BluetoothGattCharacteristic.PROPERTY_WRITE == 0 && test and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE == 0) { //Check that the property is writable
                Log.d("writing","valsf1")
                return
            }
            if (mBluetoothGatt!!.writeCharacteristic(characteristic)) {                       //Request the BluetoothGatt to do the Write
                //The request was accepted, this does not mean the write completed
                /*  if(characteristic.getUuid().toString().equalsIgnoreCase(getString(R.string.char_uuid_missed_connection))){
Log.d(
                }*/
                Log.d("req","accepted")
            } else {
                                                 //Write request was not accepted by the BluetoothGatt
            }

        } catch (e: Exception) {

        }

    }

    // Retrieve and return a list of supported GATT services on the connected device
    fun getSupportedGattServices(): List<BluetoothGattService>? {

        return if (mBluetoothGatt == null) {                                                   //Check that we have a valid GATT connection

            null
        } else mBluetoothGatt!!.services

    }


    // Disconnects an existing connection or cancel a pending connection
    // BluetoothGattCallback.onConnectionStateChange() will get the result
    @SuppressLint("MissingPermission")
    fun disconnect() {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {                      //Check that we have a GATT connection to disconnect

            return
        }

        mBluetoothGatt?.disconnect()                                                    //Disconnect GATT connection
    }

    // Request a read of a given BluetoothGattCharacteristic. The Read result is reported asynchronously through the
    // BluetoothGattCallback onCharacteristicRead callback method.
    // For information only. This application uses Indication to receive updated characteristic data, not Read
    @SuppressLint("MissingPermission")
    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {                      //Check that we have access to a Bluetooth radio
            return
        }
        val status =
            mBluetoothGatt!!.readCharacteristic(characteristic)                              //Request the BluetoothGatt to Read the characteristic

    }

    /**
     * The remaining Data need to Update to the First Position Onwards
     *
     * @param byteValue
     * @param currentPos
     */
    private fun processData(byteValue: ByteArray, currentPos: Int) {
        var i = currentPos
        var j = 0
        while (i < byteValue.size) {
            mCompleResponseByte[j] = byteValue[i]
            i++
            j++
        }
    }

    private fun processIntent(intent: Intent?, isNeedToSend: Boolean) {
        if (isNeedToSend) {
            val value =
                String(mCompleResponseByte).trim { it <= ' ' } //new String(mCompleResponseByte, "UTF-8");

            intent!!.putExtra(BLEConstants.EXTRA_DATA, value)
            sendBroadcast(intent)
        }
        for (i in mCompleResponseByte.indices) {
            mCompleResponseByte[i] = 0
        }
    }

    // Initialize by getting the BluetoothManager and BluetoothAdapter
    fun initialize(): Boolean {
        if (mBluetoothManager == null) {                                                //See if we do not already have the BluetoothManager
            mBluetoothManager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager //Get the BluetoothManager

            if (mBluetoothManager == null) {                                            //See if we failed

                return false                                                           //Report the error
            }
        }
        mBluetoothAdapter =
            mBluetoothManager!!.adapter                             //Ask the BluetoothManager to get the BluetoothAdapter

        if (mBluetoothAdapter == null) {                                                //See if we failed


            return false                                                               //Report the error
        }

        return true                                                                    //Success, we have a BluetoothAdapter to control the radio
    }

    // Enable notification on a characteristic
    // For information only. This application uses Indication, not Notification
    @SuppressLint("MissingPermission")
    fun setCharacteristicNotification(
        characteristic: BluetoothGattCharacteristic,
        enabled: Boolean
    ) {
        Log.d("Notifying char", X_ACCEL_CHARACTERISTICS_UUID.toString())
        Log.d("Notifying Descriptor", X_ACCEL_DESCRIPTOR_UUID.toString())
        try {

            if (mBluetoothAdapter == null || mBluetoothGatt == null) {                      //Check that we have a GATT connection


                return
            }
            mBluetoothGatt!!.setCharacteristicNotification(
                characteristic,
                enabled
            )          //Enable notification and indication for the characteristic

            for (des in characteristic.descriptors) {

                if (null != des) {
                    des.value =
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE         //Set the value of the descriptor to enable notification
                    mBluetoothGatt!!.writeDescriptor(des)

                }

            }
        } catch (e: Exception) {

        }
        readingsActivityLaunched = false
    }

    //connecting to servicer for bluetooth communication
 @SuppressLint("MissingPermission")
 override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val characteristicUuid: UUID? = intent?.getSerializableExtra("characteristicUuid") as? UUID
        val deviceAddress: String? = intent?.getSerializableExtra("deviceAddress") as? String
    if (intent != null) {
         intent1=intent
    }
        val dataArray: String? =
            intent?.getSerializableExtra("dataArray") as? String
     if(dataArray!=null){

     val doubleArray = dataArray?.toString()
     val byteBuffer = ByteBuffer.allocate(8 * dataArray!!.length)
         doubleArray?.forEach { byteBuffer.putChar(it) }
         byteArray = byteBuffer.array()

        deviceAddress?.let { BLEConnectionManager.connect(it) }
        mBluetoothGatt?.discoverServices()}
     if (!servicesDiscovered) {
         // Services haven't been discovered yet, do not proceed
         return START_STICKY
     }

        val gattServiceList = mBluetoothGatt?.services
        var characteristic: BluetoothGattCharacteristic? = null

        if (gattServiceList != null) {

            for (service in gattServiceList) {


                for (char in service.characteristics) {

                    if (char.uuid == characteristicUuid) {
                        characteristic = char

                        break
                    }
                }

                if (characteristic != null) {

                    break
                }
            }
        }



         //   characteristic.value = byteArray

            mBluetoothGatt?.writeCharacteristic(characteristic)


        return super.onStartCommand(intent, flags, startId)
    }




    @SuppressLint("MissingPermission")
    fun writeCharacteristic1(text: String?) {
        if(text!=null||text!="") {var characteristic: BluetoothGattCharacteristic? =null

            characteristic?.value= text?.toByteArray()
            val desiredCharacteristic =
                findCharacteristicByUUID(mBluetoothGatt, knownCharacteristicUUID)
                mBluetoothGatt!!.writeCharacteristic(desiredCharacteristic)
        }
    }


}