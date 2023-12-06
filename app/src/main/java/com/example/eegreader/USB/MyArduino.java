package com.example.eegreader.USB;

//import static androidx.core.app.AppOpsManagerCompat.Api29Impl.getSystemService;

import static android.app.PendingIntent.getActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import kotlin.text.Charsets;
//import me.aflak.arduino.Arduino;
//import me.aflak.arduino.ArduinoListener;

/**
 * Created by Omar on 21/05/2017.
 */
//Old library that may find applications in case of requirement for light weight apps
public class MyArduino implements UsbSerialInterface.UsbReadCallback, SerialInputOutputManager.Listener {
    private Context context;
    private ArduinoListener listener;
private SerialSocket socket;
    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private UsbReceiver usbReceiver;
    private UsbManager usbManager;
    private UsbDevice lastArduinoAttached;

    private int baudRate;
    private boolean isOpened;
    private List<Integer> vendorIds;
    private List<Byte> bytesReceived;
    private byte delimiter;
   // private UsbSerialPort serialPort;
    private static final String ACTION_USB_DEVICE_PERMISSION = "me.aflak.arduino.USB_PERMISSION";
    private static final int DEFAULT_BAUD_RATE = 230_400;
    private static final byte DEFAULT_DELIMITER = '\n';
    private SerialInputOutputManager ioManager;
    public MyArduino(Context context, int baudRate) {
        init(context, baudRate);
    }
    private StringBuilder dataBuffer = new StringBuilder();

    public MyArduino(Context context) {
            init(context, DEFAULT_BAUD_RATE);

    }

    private void init(Context context, int baudRate) {
        this.context = context;
        this.usbReceiver = new UsbReceiver();
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.baudRate = baudRate;
        this.isOpened = false;
        this.vendorIds = new ArrayList<>();
        this.vendorIds.add(3368);
        this.bytesReceived = new ArrayList<>();
        this.delimiter = DEFAULT_DELIMITER;
    }

    public void setArduinoListener(ArduinoListener listener) {
        this.listener = listener;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(ACTION_USB_DEVICE_PERMISSION);
        context.registerReceiver(usbReceiver, intentFilter);

        lastArduinoAttached = getAttachedArduino();
        if (lastArduinoAttached != null && listener != null) {
            listener.onArduinoAttached(lastArduinoAttached);
        }
    }

    public void unsetArduinoListener() {
        this.listener = null;
    }

    public void open(UsbDevice device) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_DEVICE_PERMISSION), PendingIntent.FLAG_MUTABLE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_DEVICE_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);
        usbManager.requestPermission(device, permissionIntent);
    }

    public void reopen() {
        open(lastArduinoAttached);
    }

    public void close() {
        if (serialPort != null) {
            serialPort.close();
        }
        if (connection != null) {
            connection.close();
        }

        isOpened = false;
        context.unregisterReceiver(usbReceiver);
    }

    public void send(byte[] bytes) {
        if (serialPort != null) {
            Log.d("hello",Arrays.toString(bytes));
            serialPort.write(bytes);
        }

    }

    public void setDelimiter(byte delimiter){
        this.delimiter = delimiter;
    }

    public void setBaudRate(int baudRate){
        this.baudRate = baudRate;
    }

    public void addVendorId(int id){
        vendorIds.add(id);
    }

    @Override
    public void onNewData(byte[] data) {
        if(listener != null)
            listener.onArduinoMessage(data);
    }

    @Override
    public void onRunError(Exception e) {
        Log.d("HELLO", "HI");
    }




    private class UsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device;
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (hasId(device.getVendorId())) {
                            lastArduinoAttached = device;
                            if (listener != null) {
                                listener.onArduinoAttached(device);
                            }
                        }
                        break;
                    case UsbManager.ACTION_USB_DEVICE_DETACHED:
                        device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (hasId(device.getVendorId())) {
                            if (listener != null) {
                                listener.onArduinoDetached();
                            }
                        }
                        break;
                    case ACTION_USB_DEVICE_PERMISSION:
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                            if (hasId(device.getVendorId())) {
                                // Find all available drivers from attached devices.

                            //     usbSerialPort = driver.getPorts().get(portNum);
                                connection = usbManager.openDevice(device);
                                serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                                if (serialPort != null) {
                                    if (serialPort.open()) {

                                        Log.d("I ","AM HERE");
                                     //   SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), connection, serialPort);
                                //        service.connect(socket);
                                        serialPort.setDTR(true);
                                        serialPort.setRTS(true);

                                      //  setUiEnabled(true);
                                        //   ioManager = new SerialInputOutputManager(serialPort, this);
                                    //    ioManager.start();
                                        serialPort.setBaudRate(230400);
                                        serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                        serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);

                                        serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                        //serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_DSR_DTR);
                                        serialPort.read(MyArduino.this);
                                       // serialPort.setBreak(100);

                                        //serialPort.setPortName("0");
                                  //      UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);





                                    //   serialPort.setFlowControl(UsbSerialInterface.);
                                       // String newline = "\r\n";
                                      //  serialPort.write(newline.getBytes());
                                     //  serialPort.read(MyArduino.this);
                                       // serialPort.getInitialBaudRate();
                                      //  serial

                                        isOpened = true;
                    //    ioManager = new SerialInputOutputManager(serialPort,  this);
                                      //  ioManager.start();
                                        if (listener != null) {



                                            listener.onArduinoOpened();

                                        }
                                    }
                                }
                            }
                        } else if (listener != null) {
                            listener.onUsbPermissionDenied();
                        }
                        break;
                }
            }
        }
    }


    private UsbDevice getAttachedArduino() {
        HashMap<String, UsbDevice> map = usbManager.getDeviceList();
        for (UsbDevice device : map.values()) {
            if (hasId(device.getVendorId())) {
                return device;
            }
        }
        return null;
    }

    private List<Integer> indexOf(byte[] bytes, byte b){
        List<Integer> idx = new ArrayList<>();
        for(int i=0 ; i<bytes.length ; i++){
            if(bytes[i] == b){
                idx.add(i);
            }
        }
        return idx;
    }

    private List<Byte> toByteList(byte[] bytes){
        List<Byte> list = new ArrayList<>();
        for(byte b : bytes){
            list.add(b);
        }
        return list;
    }

    private byte[] toByteArray(List<Byte> bytes){
        byte[] array = new byte[bytes.size()];
        for(int i=0 ; i<bytes.size() ; i++){
            array[i] = bytes.get(i);
        }
        return array;
    }
String decodedString="";
    @Override
    public void onReceivedData(byte[] bytes) {
        Log.d("received", String.valueOf(bytes.length));
     //   Log.d("",serialPort.)
        //Log.d("baud rate is", String.valueOf(serialPort.getInputStream()));
        for(byte b: bytes) {
            int numericValue = b;
            char Character = (char) numericValue;
            decodedString += Character;
            Log.d("Received String is", String.valueOf(numericValue));
        }

        for (byte b : bytes) {
            int numericValue = b;
            char character = (char) numericValue;
            dataBuffer.append(character);
        }

        // Process the data or check for a specific condition to send it to your listener.
        processDataFromBuffer();
       // Log.d("Received String is",numericValue);

        String receivedString = new String(bytes, StandardCharsets.UTF_8);
        Log.d("received string",receivedString);
        bytesReceived.addAll(toByteList(bytes));
        listener.onArduinoMessage(bytes);
        if (bytes.length != 0) {
            List<Integer> idx = indexOf(bytes, delimiter);

            if(idx.isEmpty()){
              //  bytesReceived.addAll(toByteList(bytes));

              //  listener.onArduinoMessage(toByteArray(bytesReceived));
            } else{
                int offset = 0;
                for(int index : idx){
                    byte[] tmp = Arrays.copyOfRange(bytes, offset, index);
                    bytesReceived.addAll(toByteList(tmp));
                    if(listener != null) {
                        listener.onArduinoMessage(toByteArray(bytesReceived));
                    }
                    bytesReceived.clear();
                    offset = index + 1;
                }

                if(offset < bytes.length){
                    byte[] tmp = Arrays.copyOfRange(bytes, offset, bytes.length);
                    bytesReceived.addAll(toByteList(tmp));
                }
            }
        }
    }
    private void processDataFromBuffer() {
        // Check if the buffer contains a complete data packet, for example, a newline-delimited packet.
        String bufferContents = dataBuffer.toString();
        Log.d("processd data is ", bufferContents);
        if (bufferContents.contains("\n")) {
            // Split the data into individual lines (assuming each line is a separate data packet).
            String[] lines = bufferContents.split("\n");
            Log.d("processd data is ", Arrays.toString(lines));
            for (String line : lines) {
                // Send the line to your listener or process it as needed.
                if (listener != null) {
                    listener.onArduinoMessage(line.getBytes());
                }
            }

            // Clear the processed data from the buffer.
            int lastNewlineIndex = bufferContents.lastIndexOf("\n");
            if (lastNewlineIndex != -1) {
                dataBuffer.delete(0, lastNewlineIndex + 1);
            }
        }
    }

    public boolean isOpened() {
        return isOpened;
    }

    private boolean hasId(int id) {
        Log.i(getClass().getSimpleName(), "Vendor id : "+id);
        for(int vendorId : vendorIds){
            if(vendorId==id){
                return true;
            }
        }
        return false;
    }
}