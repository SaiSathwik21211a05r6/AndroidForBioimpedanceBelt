package com.example.eegreader.USB;


import static com.example.eegreader.BLEModule.BLEService.ACTION_DATA_AVAILABLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eegreader.Acknowledgement;
import com.example.eegreader.BLEModule.BLEConstants;
//import com.example.eegreader.Graph.ChartDataStorage;
import com.example.eegreader.BLEModule.BLEService;
import com.example.eegreader.Graph.CustomDataPoint;
import com.example.eegreader.Graph.YourXAxisFormatter;

import com.example.eegreader.R;
import com.example.eegreader.ReadingsDB.ReadingsList;
import com.example.eegreader.ReadingsDB.ReadingsRepository;
import com.example.eegreader.ReadingsDB.ReadingsViewModal;
import com.example.eegreader.ReadingsDB.ReadingsViewModalFactory;
import com.example.eegreader.RecyclerView.DataAdapter;

import com.example.eegreader.StatusDB.StatusViewModal;
import com.example.eegreader.database.StatusDatabase;
import com.example.eegreader.database.StatusList;
import com.example.eegreader.database.StatusRepository;
import com.example.eegreader.database.StatusViewModalFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.np.lekotlin.blemodule.BLEConnectionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.example.eegreader.ReadingsDB.ReadingsDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class USBClass extends AppCompatActivity implements ServiceConnection,SerialListener {
    UsbDevice device = null;

    //private DatabaseHelper dbHelper;
    private ImageButton stopButton;
    private int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    private ReadingsViewModal readingsViewModal;
    private List<ReadingsList> list;
    private ImageButton startButton;
    private ImageButton saveButton;
    private Button reportButton;
    private final ArrayList<DevicesFragment.ListItem> listItems = new ArrayList<>();
    private ArrayAdapter<DevicesFragment.ListItem> listAdapter;
    private int baudRate = 9600;
    private int p1=0;
    private int p2=0;
    private String visitdate;
    private String visittime;
    boolean permissionasking = false;
    private String frequency="10000.00";
    private String ndp="11";

    //Helper methods for usb connection
    @Override
    public void onSerialConnect() {
        Log.d("connection","hi".toString());
        connected = Connected.True;
        readingsViewModal.insertReadings(new ReadingsList(patiiid,visitdate, visittime,readingsJSON.toString()));
        receiveText.append("Running via USB...");
    }

    @Override
    public void onSerialConnectError(Exception e) {
Log.d("connection",e.toString());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    @Override
    public void onSerialRead(ArrayDeque<byte[]> datas) {
      //  Log.d("reading","Data");
          receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        Log.d("io",e.toString());
        disconnect();
        receiveText.append("Device Disconnected. Connect it again and click start button\n");


    }




    private enum Connected {False, Pending, True}

    private long startTime = System.currentTimeMillis();
    private BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;
    private SerialService service;
private int dataCount;
    private TextView receiveText;
    private TextView sendText;
    private TextView sendText1;
    private TextView sendText2;
    private TextView pid;
    private TextView vid;
    private TextView vtime;
    private TextView vdate;
private int mode=0;
    private TextView sendText3;
    private TerminalFragment.ControlLines controlLines;
    // Declare dataPoints as a list of your custom data points
    List<CustomDataPoint> dataPoints = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextUtil.HexWatcher hexWatcher;
    List<Entry> frequencyEntries = new ArrayList<>();
    List<Entry> phaseEntries = new ArrayList<>();
    List<Entry> bioimpedanceEntries = new ArrayList<>();
    private StringBuilder receivedData = new StringBuilder();
    private LineChart linechart;
    private LineChart linechart1;
    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean controlLinesEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    private List<String> dataList = new ArrayList<>(); // Your data source
    private DataAdapter adapter = new DataAdapter(dataList);
    private StatusViewModal statusViewModal;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Handler handler = new Handler();
    HashMap<Integer, int[]> myMap = new HashMap<>();
    public USBClass() {


        int counter = 1;

        for (int i = 0; i <= 6; i++) {
            if (i==2){
                continue;
            }
            for (int j = 0; j <= 6; j++) {
                if(j==2){
                    continue;
                }
                    myMap.put(counter, new int[]{i, j});
                    counter++;

            }
        }
        //Listener to respond to USB Connection
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    connect(granted);
                }
            }
        };
    }

    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extrasBundle = intent.getExtras();
            if (extrasBundle != null) {
                for (String key : extrasBundle.keySet()) {
                    Object value = extrasBundle.get(key);

                }
            } else {

            }
            String datas = intent.getStringExtra(com.example.eegreader.BLEModule.BLEService.EXTRA_DATA);
            String uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID);
            SpannableStringBuilder spn = new SpannableStringBuilder();




                    String msg = new String(datas);

                    //adapter.updateData(msg);
                    if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                        msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);

                        if (pendingNewline && msg.charAt(0) == '\n') {
                            if (spn.length() >= 2) {
                                spn.delete(spn.length() - 2, spn.length());
                            } else {

                            }

                        pendingNewline = msg.charAt(msg.length() - 1) == '\r';

                        datacount=datacount+1;
//Log.d("dataCount", String.valueOf(datacount));
                        numberExtract(receivedData.toString());
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                        csvstring.append(  dateFormat.format(calendar.getTime())+","+frequency11+","+number3+","+number4+","+p1+","+p2+"\n");
                        sumNumber3 += number3;
                        sumNumber4 += number4;
                        //    Log.d("sumno", String.valueOf(sumNumber3));
                        count++;
                    }
                    spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
                    if (!hexEnabled) {
                        String msg1 = new String(msg);
                        receivedData.append(msg1);
                    }
                    //   Log.d("received is",msg.toString());
                    //     Log.d("received1 is",receivedData.toString());

                    if(datacount==dataCount&&frequency11<frequency21)
                    {
                        if(configurationcounter+1<=36) {
                            configurationcounter = configurationcounter + 1;
                            p1 = myMap.get(configurationcounter)[0];
                            p2 = myMap.get(configurationcounter)[1];
                            receivedData1=receivedData;
                            try {
                                DateTimeFormatter timeFormat = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    JSONArray jsonArray = new JSONArray();
                                    jsonArray.put(String.valueOf(frequency11));
                                    jsonArray.put(String.valueOf(number3));
                                    jsonArray.put(String.valueOf(number4));
                                    jsonArray.put(String.valueOf(p1));
                                    jsonArray.put(String.valueOf(p2));
                                    timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                                    readingsJSON.put(LocalTime.now().format(timeFormat).toString(), jsonArray.toString());
                                    Log.d("impedance", String.valueOf(number3));

                                    ReadingsList items = new ReadingsList(patiiid,visitdate, visittime, readingsJSON.toString());
                                    Thread t = new Thread(new Runnable(){
                                        public void run() {
                                            Log.d("database", readingsViewModal.toString());
                                            //   String updatedReadings = readingsJSON.getJSONObject(visittime).toString();
                                            readingsViewModal.updateReadings(patiiid, visitdate, visittime, String.valueOf(readingsJSON));
                                        } });
                                    t.start();
                                }  } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            receivedData1.append(","+p1+","+p2);

                            datacount = 0;
                        }
                        else {
                            frequency11++;
//Log.d("frequency 1", String.valueOf(frequency1));
                            configurationcounter = 1;
                            p1 = myMap.get(configurationcounter)[0];
                            p2 = myMap.get(configurationcounter)[1];
                            receivedData1.append(","+p1+","+p2);

                        }
                        // receivedData.toString().replace(receivedData.toString(),"");
                        receivedData.toString().replace("enter values: \n","");



                        if(sendText.getText().toString().length()==0||sendText.getText().toString()==null||sendText1.getText().toString()==null||sendText1.getText().toString().length()==0||sendText2.getText().toString().length()==0||sendText3.getText().toString().length()==0) {
                            receiveText.setText("\nCurrently reading on "+frequency11+"Hz:"+"("+p1+","+p2+")\n");
                            send("break");
                            send(frequency11+",11," + p1 + "," + p2);
                            double averageNumber3 = sumNumber3 / count;
                            double averageNumber4 = sumNumber4 / count;
                            adapter.updateData(averageNumber3+","+averageNumber4+","+frequency11+","+p1+","+p2);
                            datacount=0;
                            //receivedData1.append(p1+","+p2);
                            //   processAndDisplayData(receivedData.toString());

                        }  else {
                            send("break");
                            send(frequency11+",11," + p1 + "," + p2);
                            // processAndDisplayData(receivedData.toString());
                            receiveText.setText("\nCurrently reading on "+frequency11+"Hz:"+"("+p1+","+p2+")");
                            double averageNumber3 = sumNumber3 / count;
                            double averageNumber4 = sumNumber4 / count;
                            adapter.updateData(number3+","+number4+","+frequency11+","+p1+","+p2);
                            datacount=0;
                            try {

                                //readingsViewModal.insertReadings(new ReadingsList(patiiid, visitdate, visittime, Calendar.getInstance().getTime().toString(), frequency11, number3, number4, Double.parseDouble(String.valueOf(p1)),Double.parseDouble(String.valueOf(p2))));
                            } catch (Exception e) {
                                Log.d("Adding is",e.toString()); // Handle the exception according to your requirements
                            }
                        }//receivedData.

//receivedData.delete(0,receivedData.length()-1);
//receiveText.append(receivedData);
                    }}



            if (containsNumber(String.valueOf(receivedData)) || containsDecimal(String.valueOf(receivedData))) {
                dataBuffer.append(receivedData);
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {
                    processAndDisplayData(receivedData.toString());
                    // processAndDisplayData(receivedData.toString());
                    lastProcessingTime = currentTime;
                    saveReadingsToSQLite(frequency11,number3,number4,p1,p2);
                    // Clear the buffer after processing
                    dataBuffer.setLength(0);
                    receivedData.delete(0,receivedData.length()-1);

                }
            }
            else {
                // Handle non-numeric data here if needed
            }


        }
    };


Boolean savec=false;
Boolean stopc=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdata);
    AppBarLayout bottomAppBar = findViewById(R.id.bottomBar);
        onAttach(this);
        stopButton = bottomAppBar.findViewById(R.id.stopbutton);
        saveButton = bottomAppBar.findViewById(R.id.savebutton);
        startButton = bottomAppBar.findViewById(R.id.startbutton);
        reportButton = bottomAppBar.findViewById(R.id.report);
        ReadingsDatabase readingsDatabase=ReadingsDatabase.Companion.invoke(this);
        ReadingsRepository readingsRepository = new ReadingsRepository(readingsDatabase);
        ReadingsViewModalFactory factory = new ReadingsViewModalFactory(readingsRepository);
        readingsViewModal = new ViewModelProvider(this, (ViewModelProvider.Factory) factory).get(ReadingsViewModal.class);
        StatusDatabase statusDatabase=StatusDatabase.Companion.invoke(this);
        StatusRepository statusRepository = new StatusRepository(statusDatabase);
        StatusViewModalFactory stfactory = new StatusViewModalFactory(statusRepository);
        statusViewModal = new ViewModelProvider(this, (ViewModelProvider.Factory) stfactory).get(StatusViewModal.class);


        //Toolbar toolbar = findViewById(R.id.toolbar);
        patiiid = getIntent().getStringExtra("patient id");
        visitid = getIntent().getStringExtra("visit id");
      //  dbHelper = new DatabaseHelper(this);
visitdate=getIntent().getStringExtra("date");
visittime=getIntent().getStringExtra("time");
Log.d("intent",patiiid+visitid+visitdate+visittime);
        RadioGroup genderRadioGroup =  bottomAppBar.findViewById(R.id.genderedit);
        RadioButton usbRadioButton = findViewById(R.id.usb);
        RadioButton bleRadioButton = findViewById(R.id.ble);
        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.usb) {
                    mode = 0;
                    // USB Radio button is selected
                    // Handle USB selection here
                } else if (checkedId == R.id.ble) {
                    mode = 1;
                    // Bluetooth Radio button is selected
                    // Handle Bluetooth selection here
                }
            }
        });

        pid = findViewById(R.id.patid);
        vid = findViewById(R.id.vid);
        vtime = findViewById(R.id.vtime);
        vdate = findViewById(R.id.vdate);
       pid.setText(patiiid);
        vid.setText(visitid);
        vtime.setText(visittime);
        vdate.setText(visitdate);
        //tabular view for readings
        recyclerView = findViewById(R.id.recview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
receiveText=findViewById(R.id.receive_text);
        //four inputs

        sendText = bottomAppBar.findViewById(R.id.input1);
        sendText1 =  bottomAppBar.findViewById(R.id.input2);
        sendText2 =  bottomAppBar.findViewById(R.id.input3);
        sendText3 =  bottomAppBar.findViewById(R.id.input4);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == 0) {
                    if (connected != Connected.True) {
                        if (sendText.getText().toString().length() != 0 && sendText1.getText().toString().length() != 0 && sendText2.getText().toString().length() != 0 && sendText3.getText().toString().length() != 0) {
                            connectionmanager();
                            Log.d("hello", "here");
                            send("break");
                            send(sendText.getText().toString() + "," + sendText1.getText().toString() + "," + sendText2.getText().toString() + "," + sendText3.getText().toString());
                            frequency11 = Float.parseFloat(sendText.getText().toString());
                            frequency21 = Integer.parseInt(sendText1.getText().toString());
                            frequencystep1 = Integer.parseInt(sendText2.getText().toString());
                            dataCount = Integer.parseInt(sendText3.getText().toString());
                        } else {
                            receiveText.setText("Enter Some Values");
                        }
                    } else {
                        if (sendText.getText().toString().length() != 0 && sendText1.getText().toString().length() != 0 && sendText2.getText().toString().length() != 0 && sendText3.getText().toString().length() != 0) {
                            send("break");
                            send(sendText.getText().toString() + "," + sendText1.getText().toString() + "," + sendText2.getText().toString() + "," + sendText3.getText().toString());
                            frequency11 = Float.parseFloat(sendText.getText().toString());
                            frequency21 = Integer.parseInt(sendText1.getText().toString());
                            frequencystep1 = Integer.parseInt(sendText2.getText().toString());
                            dataCount = Integer.parseInt(sendText3.getText().toString());
                            resetActivityState();
                            //   send("break");
                        } else {
                            //  Toast.makeText(, "Enter Some Values", Toast.LENGTH_LONG);
                            receiveText.setText("Enter Some Values");

                        }
                    }
                } else {
                    startBLEScan();
                }

            }   });
        //disconnecting
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
stopc=true;

                disconnect();
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReadingsToCSV();

                //     Log.d("linecha saving", linechart.getLineData().getDataSets().toString());
                //  ChartDataStorage.saveChartDataToStorage(USBClass.this, linechart.getLineData());
                //  linechart1.setData(ChartDataStorage.retrieveChartDataFromStorage(USBClass.this));
                linechart1.notifyDataSetChanged();
                savec=true;
                if(savec==true && stopc==true)
                {Intent intent = new Intent(getApplicationContext(), Acknowledgement.class);
                intent.putExtra("patid",patiiid);
                intent.putExtra("date",visitdate);
                intent.putExtra("time",visittime);
                intent.putExtra("visit id",visitid);
                intent.putExtra("freqstart",frequency11);
                intent.putExtra("freqend",frequency21);
                startActivity(intent);

                disconnect();}
                else Toast.makeText(getApplicationContext(),"Please stop and save the readings to continue",Toast.LENGTH_SHORT);
            }
        });

        //saving readings as csv file
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveReadingsToCSV();

           //     Log.d("linecha saving", linechart.getLineData().getDataSets().toString());
              //  ChartDataStorage.saveChartDataToStorage(USBClass.this, linechart.getLineData());
              //  linechart1.setData(ChartDataStorage.retrieveChartDataFromStorage(USBClass.this));
                linechart1.notifyDataSetChanged();
                savec=true;
            }
        });

        hexWatcher = new TextUtil.HexWatcher(sendText);
        hexWatcher.enable(hexEnabled);
        sendText.addTextChangedListener(hexWatcher);
        sendText.setHint(hexEnabled ? "HEX mode" : "");

        //Real time graph plotting
        linechart = findViewById(R.id.lineChart1);
        linechart.getXAxis().setEnabled(false);
        linechart.getXAxis().setEnabled(true);
        linechart.getXAxis().setDrawGridLines(false);
        linechart.getAnimation();
        linechart.getDescription().setEnabled(false);
        linechart.getAxisLeft().setDrawGridLines(true);
        linechart.getAxisLeft().setLabelCount(6, true); // Number of labels (10 steps + 1)
       // linechart.getAxisLeft().setAxisMinimum(150000f); // Minimum value for Y-axis
     //   linechart.getAxisLeft().setAxisMaximum(309000f); // Maximum value for Y-axis
        float stepValue = 50f;
        linechart.getAxisLeft().setGranularity(stepValue);
        linechart1 = findViewById(R.id.lineChart2);
        linechart1.getXAxis().setEnabled(false);
        linechart1.getXAxis().setEnabled(true);
        linechart1.getXAxis().setDrawGridLines(false);
        linechart1.getAnimation();
        linechart1.getDescription().setEnabled(false);
        linechart1.getAxisLeft().setDrawGridLines(true);
        linechart1.getAxisLeft().setLabelCount(6, true); // Number of labels (10 steps + 1)
      //  linechart1.getAxisLeft().setAxisMaximum(-95f); // Minimum value for Y-axis
      //  linechart.getAxisLeft().setAxisMinimum(-85f); // Maximum value for Y-axis
        float stepValue1 = 1f;
        linechart1.getAxisLeft().setGranularity(stepValue1);
        // Define the number of electrode combination pairs to display at a time
        int electrodePairsToShow = 5;

// Create a custom X-axis value formatter

// Configure the X-axis with the custom value formatter
        XAxis xAxis = linechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Assuming each data point is separated by 1 unit
       // xAxis.setValueFormatter(customFormatter);


// Set any other chart configurations (labels, axis scaling, etc.)

// Display the chart


        ValueFormatter yFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Format the Y-axis labels as needed
                return String.format("%.1f", value);
            }
        };
        linechart.getAxisLeft().setValueFormatter(yFormatter);
        linechart.setTouchEnabled(true);
        linechart.setDragEnabled(true);
        linechart.setScaleEnabled(true);
        linechart1.getAxisLeft().setValueFormatter(yFormatter);
        linechart1.setTouchEnabled(true);
        linechart1.setDragEnabled(true);
        linechart1.setScaleEnabled(true);
        String frequency_temp = sendText.getText().toString();
        if (frequency_temp != null && frequency_temp !="" ){
            frequency = frequency_temp;
        }
        String ndp_temp= sendText1.getText().toString();
        if (ndp_temp != null && ndp_temp != "" ){
            ndp = ndp_temp;
        }
        //Communication from app



        //Cloud storage
         databaseReference = FirebaseDatabase.getInstance().getReference("patients");

       // connectionmanager();

    }
    private void resetActivityState() {
        // Reset your activity state to its initial state here

        // For example, you can clear text fields, reset variables, or perform any other required actions

        receiveText.setText(""); // Clear a TextView
        // Reset any other views or variables
data= new String[]{null};
rzMagValues.clear();
rzPhaseValues.clear();
number2.clear();
p1=0;
p2=0;
configurationcounter=0;
if(receivedData.length()!=0)
    receivedData.delete(0,receivedData.length()-1);

        // You can also refresh your chart or clear the X-axis labels if needed
        linechart.getXAxis().setValueFormatter(new YourXAxisFormatter(lastProcessingTime-startTime,frequency11,p1,p2 ));
frequencyEntries.clear();
phaseEntries.clear();
        linechart.clear();
        linechart.notifyDataSetChanged();
        linechart.invalidate();
        linechart1.getXAxis().setValueFormatter(new YourXAxisFormatter(lastProcessingTime-startTime,frequency11,p1,p2 ));
        linechart1.notifyDataSetChanged();
        linechart1.invalidate();
        linechart1.clear();
        adapter.notifyDataSetChanged();
        dataList.clear();
        adapter.notifyDataSetChanged();
    }
    //initiating USB Connection
    public void connectionmanager() {
        Log.d("connected", "here");

        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
        UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
        listItems.clear();

        receiveText.setText("\nTrying to connect");
        if(usbManager.getDeviceList().values().isEmpty())
        {   Log.d("here","Trying to connect");
            receiveText.setText("No Device Connected.");}
        for (UsbDevice device : usbManager.getDeviceList().values()) {

            UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
            if (driver == null) {

                driver = usbCustomProber.probeDevice(device);
            }
            if (driver != null) {
                for (int port = 0; port < driver.getPorts().size(); port++) {
                    listItems.add(new DevicesFragment.ListItem(device, port, driver));
                    Bundle args = new Bundle();
                    deviceId = device.getDeviceId();
                    port = 0; // You can modify this if needed
                    baudRate = 9600;
                    Log.d("conncetion","initiation");
                    connect();
                    break;
                }
            } else {
                listItems.add(new DevicesFragment.ListItem(device, 0, null));
            }

        }
    }

    //connection logic
    private void connect() {
        onAttach(this);
        connect(null);
    }
private UsbManager usbManager;
    private UsbSerialDriver driver;
    private void connect(Boolean permissionGranted) {
if(service==null) {
    Log.d("conncetion","service");
    // Log.d("connecting", "attempt");
}
    usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);


        for (UsbDevice v : usbManager.getDeviceList().values())
            if (v.getDeviceId() == deviceId)
                device = v;
        if (device == null) {
            Log.d("conncetion","nof");
            //status("connection failed: device not found");
return;
        }
        driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            Log.d("conncetion","nod");
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if (driver == null) {
            Log.d("conncetion","nod2");
return;
        }
        if (driver.getPorts().size() < portNum) {
            Log.d("conncetion","nop");
            return;
        }

        //User permission required for android
        if (permissionasking == false && permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            Log.d("conncetion","perm");
            int flags1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent1 = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags1);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent1);
            Handler handler = new Handler();
            UsbSerialDriver finalDriver = driver;
            receiveText.setText("\nPlease wait while app connects");
            Toast.makeText(this,"Please wait while app connects to the device\n",Toast.LENGTH_LONG);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("conncetion","continuing");
                    // Continue with the connection process after the delay
                    connectionContinue(finalDriver, usbManager);
                }
            }, 8000);
            return;
        }    connectionContinue(driver,usbManager);}
    public void connectionContinue(UsbSerialDriver driver, UsbManager usbManager)
    { receiveText.setText("Please wait while the app is connecting");
        if(service==null)
            onAttach(this);
        if(connected==Connected.True){receiveText.setText("\nDevice is already connected");
            Toast.makeText(this,"Already connected",Toast.LENGTH_LONG);
            disconnect();
        connectionmanager();}
        if(usbSerialPort==null)
         usbSerialPort = driver.getPorts().get(portNum);

if(driver.getDevice()==null)
{receiveText.setText("\nNo Device is connected");return;}
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null) {

            return;
        }
        this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));

        if (service != null)
            service.attach(this);
        else {
            startService(new Intent(this, SerialService.class));

        }
        if (initialStart && service != null) {

            initialStart = false;
            this.runOnUiThread(this::connect);
        }

        if (service != null)
            service.attach(this);
        else {
            this.getApplicationContext().startService(new Intent(this.getApplicationContext(), SerialService.class));

        } // prevents service destroy on unbind from recreated activity caused by orientation change

        connected = Connected.Pending;
        try {

            usbSerialPort.open(usbConnection);
            try {

                usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (UnsupportedOperationException e) {
                Toast.makeText(this,"Device Connection failed",Toast.LENGTH_SHORT);
                Log.d("unsupp","unsupported");
            }

            SerialSocket socket = new SerialSocket(this.getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            onSerialConnect();


        } catch (Exception e) {
            onSerialConnectError(e);

        }
    }
    private void onRequestPermission(Boolean permissionGranted, UsbManager usbManager, UsbSerialDriver driver) {

        if( permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {

            int flags1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent1 = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags1);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent1);

        }
    }

    //disconnecting
    private void disconnect() {

Log.d("disconncted","disconnected");
if(csvstring.length()>100)
    saveReadingsToCSV();
        connected = Connected.False;
//        controlLines.stop();

if(service!=null)
    service.disconnect();



    }

    //communication from app
    private void send(String str) {
        Log.d("Freq", String.valueOf(frequency11));
        Log.d("Freq", String.valueOf(frequency21));
        Log.d("Freq", String.valueOf(frequencystep1));

        Log.d("passed",str);
       // Log.d("entered empty values", String.valueOf(sendText.getText().toString()));
        if(connected != Connected.True) {
            Toast.makeText(this, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
           // receiveText.append("Please press the button on the device and \nwait until the reading is obtained for the provided Electrode Combination\n");
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //    receiveText.append(spn);

            service.write(msg);
            Log.d("written",msg);

        } catch (SerialTimeoutException e) {
            // status("write timeout: " + e.getMessage());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    //If the device requires ByteArrays as input

private double frequency11=1000.00;
    private double frequency21=2000.00;
    private double frequencystep1=1.00;
    private static final long PROCESSING_INTERVAL = 3 * 1000; // 60 seconds in milliseconds
    private long lastProcessingTime = 0;
    double rzMagValue = 0.0;
    String[] noparts= new String[0];
    private int datacount=0;
    Handler chartUpdateHandler = new Handler();
    String[] lines=new String[0];
    String[] lines1=new String[0];
    String[] lines2=new String[0];
    private StringBuilder receivedData1;
    private int configurationcounter=0;
    double rzPhaseValue = 0.0;
    List<Double> rzMagValues = new ArrayList<>();
    List<Double> rzPhaseValues = new ArrayList<>();
    StringBuilder numbers=new StringBuilder();
    private StringBuilder dataBuffer = new StringBuilder();
    ArrayList numbers1=new ArrayList();
    StringBuilder csvstring=new StringBuilder();
    private double sumNumber3 = 0;
    private double sumNumber4 = 0;
    private int count = 0;
    JSONObject readingsJSON = new JSONObject();
    //Data from device
    private void receive(ArrayDeque<byte[]> datas) {

        SpannableStringBuilder spn = new SpannableStringBuilder();


        for (byte[] data : datas) {

            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                String msg = new String(data);

                //adapter.updateData(msg);
                if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);

                    if (pendingNewline && msg.charAt(0) == '\n') {
                        if (spn.length() >= 2) {
                            spn.delete(spn.length() - 2, spn.length());
                        } else {

                        }
                    }
                    pendingNewline = msg.charAt(msg.length() - 1) == '\r';

                    datacount=datacount+1;
//Log.d("dataCount", String.valueOf(datacount));
                    numberExtract(receivedData.toString());
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                    csvstring.append(  dateFormat.format(calendar.getTime())+","+frequency11+","+number3+","+number4+","+p1+","+p2+"\n");
                    sumNumber3 += number3;
                    sumNumber4 += number4;
                //    Log.d("sumno", String.valueOf(sumNumber3));
                    count++;
                }
                spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
                if (!hexEnabled) {
                    String msg1 = new String(msg);
                    receivedData.append(msg1);
                }
             //   Log.d("received is",msg.toString());
           //     Log.d("received1 is",receivedData.toString());

                if(datacount==dataCount&&frequency11<frequency21)
                {
                    if(configurationcounter+1<=36) {
                    configurationcounter = configurationcounter + 1;
                    p1 = myMap.get(configurationcounter)[0];
                    p2 = myMap.get(configurationcounter)[1];
                        receivedData1=receivedData;
                        try {
                            DateTimeFormatter timeFormat = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(String.valueOf(frequency11));
                                jsonArray.put(String.valueOf(number3));
                                jsonArray.put(String.valueOf(number4));
                                jsonArray.put(String.valueOf(p1));
                                jsonArray.put(String.valueOf(p2));
                                timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                                readingsJSON.put(LocalTime.now().format(timeFormat).toString(), jsonArray.toString());
                               Log.d("impedance", String.valueOf(number3));

                                ReadingsList items = new ReadingsList(patiiid,visitdate, visittime, readingsJSON.toString());
                                Thread t = new Thread(new Runnable(){
                                    public void run() {
                                        Log.d("database", readingsViewModal.toString());
                                     //   String updatedReadings = readingsJSON.getJSONObject(visittime).toString();
                                        readingsViewModal.updateReadings(patiiid, visitdate, visittime, String.valueOf(readingsJSON));
                                    } });
                                t.start();
                                         }  } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        receivedData1.append(","+p1+","+p2);

datacount = 0;
                    }
                else {
frequency11++;
//Log.d("frequency 1", String.valueOf(frequency1));
                    configurationcounter = 1;
                    p1 = myMap.get(configurationcounter)[0];
                    p2 = myMap.get(configurationcounter)[1];
                    receivedData1.append(","+p1+","+p2);

                }
               // receivedData.toString().replace(receivedData.toString(),"");
                    receivedData.toString().replace("enter values: \n","");



                  if(sendText.getText().toString().length()==0||sendText.getText().toString()==null||sendText1.getText().toString()==null||sendText1.getText().toString().length()==0||sendText2.getText().toString().length()==0||sendText3.getText().toString().length()==0) {
                      receiveText.setText("\nCurrently reading on "+frequency11+"Hz:"+"("+p1+","+p2+")\n");
                      send("break");
                      send(frequency11+",11," + p1 + "," + p2);
                      double averageNumber3 = sumNumber3 / count;
                      double averageNumber4 = sumNumber4 / count;
                      adapter.updateData(averageNumber3+","+averageNumber4+","+frequency11+","+p1+","+p2);
                      datacount=0;
                       //receivedData1.append(p1+","+p2);
                   //   processAndDisplayData(receivedData.toString());

                   }  else {
                      send("break");
                       send(frequency11+",11," + p1 + "," + p2);
                    // processAndDisplayData(receivedData.toString());
                      receiveText.setText("\nCurrently reading on "+frequency11+"Hz:"+"("+p1+","+p2+")");
                      double averageNumber3 = sumNumber3 / count;
                      double averageNumber4 = sumNumber4 / count;
                      adapter.updateData(number3+","+number4+","+frequency11+","+p1+","+p2);
                      datacount=0;
                      try {

                          //readingsViewModal.insertReadings(new ReadingsList(patiiid, visitdate, visittime, Calendar.getInstance().getTime().toString(), frequency11, number3, number4, Double.parseDouble(String.valueOf(p1)),Double.parseDouble(String.valueOf(p2))));
                      } catch (Exception e) {
                          Log.d("Adding is",e.toString()); // Handle the exception according to your requirements
                      }
                  }//receivedData.

//receivedData.delete(0,receivedData.length()-1);
//receiveText.append(receivedData);
            }}}



        if (containsNumber(String.valueOf(receivedData)) || containsDecimal(String.valueOf(receivedData))) {
            dataBuffer.append(receivedData);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {
                processAndDisplayData(receivedData.toString());
               // processAndDisplayData(receivedData.toString());
                lastProcessingTime = currentTime;
                saveReadingsToSQLite(frequency11,number3,number4,p1,p2);
                // Clear the buffer after processing
                dataBuffer.setLength(0);
               receivedData.delete(0,receivedData.length()-1);

            }
        }
        else {
            // Handle non-numeric data here if needed
        }

    }
    double number3;
    double number4;
    public void numberExtract(String finalMsg)
    {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)");

// The input string containing numbers
        String input = finalMsg.toString();

// Create a matcher for the input string
        Matcher matcher = pattern.matcher(input);

// Find and print all matched numbers
        while (matcher.find()) {
            String number = matcher.group();
            if(Double.parseDouble(number)>130000.00)
                number3= Double.parseDouble(number);
            else if(Double.parseDouble(number)<100.00) number4=Double.parseDouble(number);


            //number2.add(Double.valueOf(number));
    }}
    ArrayList<Double> number2 = new ArrayList<>();
    String[] data = {null};
    Stack<Double> readings = new Stack<>();
    //processing Data
    public void processAndDisplayData(String finalMsg)
    {
        Runnable backgroundTask = new Runnable() {
            @Override
            public void run() {


                Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)");

// The input string containing numbers
                String input = finalMsg.toString();

// Create a matcher for the input string
                Matcher matcher = pattern.matcher(input);

// Find and print all matched numbers
                while (matcher.find()) {
                    String number = matcher.group();

                    number2.add(Double.valueOf(number));
                   // readings.add(Double.valueOf(number));
                   // Log.d("numbers are ", String.valueOf(readings.peek()));
                   // Log.d("numbers2 are", String.valueOf(number2));
                    number.replace(number,"");
                    if (rzMagValues.size() < 3 ) {
                        if(Double.valueOf(number) > 100.00)
                        {rzMagValues.add(Double.valueOf(number));}

                    } else if (rzMagValues.size() >= 3 && Double.valueOf(number) > 100.00) {
                        rzMagValues.clear();
                        rzMagValues.add(Double.valueOf(number));


                    }
                    if (rzPhaseValues.size() < 3 && Double.valueOf(number) < 100.00) {

                        rzPhaseValues.add(Double.valueOf(number.trim()));



                    } else if (rzPhaseValues.size() >= 3&& Double.valueOf(number) < 100.00) {
                        rzPhaseValues.clear();
                        rzPhaseValues.add(Double.valueOf(number.trim()));
                    }
                }

                //number2.clear();

                for (int i = 0; i < rzPhaseValues.size(); i++) {
                   // double normalizedRzPhaseValue = normalizeValue(rzPhaseValues.get(i), -100, 150000);
                 //   rzPhaseValues.set(i, normalizedRzPhaseValue);
                }
finalMsg.replace(finalMsg,"");


                try {

                    long currentTime = System.currentTimeMillis();
                    long timeElapsed = currentTime - startTime;

                    // Create a DataPoint object with the parsed values
                    CustomDataPoint dataPoint = new CustomDataPoint(timeElapsed, rzMagValues, rzPhaseValues);
                    dataPoints.add(dataPoint);

                    // Update your data sets


                    // Update the chart with the new data

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(rzMagValues.size()!=0&&rzPhaseValues.size()!=0)
                                data[0] = new String(rzMagValues.get(0).toString().concat(","+rzPhaseValues.get(0).toString()+","+frequency11+","+p1+","+p2));
                            //  Log.d("data passed", data[0]);
                            // UI-related code to update the chart goes here
                            updateChartWithData(dataPoints);
                            String[] parts=finalMsg.split(",");
                            // Update your UI here

                           // adapter.updateData(data[0]);
linechart.isSaveEnabled();

                            adapter.setRecyclerView(recyclerView);
                        }
                    });


                } catch (NumberFormatException e) {
                    // Handle parsing errors
                    // Log.e("Data Parsing", "Error parsing data: " + msg);
                } }
        };
        new Thread(backgroundTask).start();

    }

    //Extracting Number
    private boolean isDecimal(String str) {
        return str.matches("-?\\d*\\.?\\d+");
    }
    public static double normalizeValue(double value, double min, double max) {
        if (value < min) {
            return 0.0;  // If the value is less than the minimum, set it to 0.
        } else if (value > max) {
            return 100.0;  // If the value is greater than the maximum, set it to 100.
        }
        else {
            // Calculate the range for scaling (max - min)
            double range = max - min;

            // Calculate the normalized value, taking negative values into account
            return (value - min) / range * 100.0;
        }
    }
    public static boolean containsNumber(String input) {
        // Define a regular expression pattern to match any digit
        String regex = ".*\\d+.*";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(input);

        // Check if the input contains a digit
        return matcher.matches();
    }
    public boolean containsDecimal(String str) {
        // Define a regular expression pattern for a decimal number
        // This pattern allows for an optional minus sign, digits before and after the decimal point.
        String decimalPattern = "-?\\d*\\.?\\d+";

        // Use the Pattern class to compile the regular expression
        Pattern pattern = Pattern.compile(decimalPattern);

        // Use the pattern to check if the input string contains a decimal
        return pattern.matcher(str).find();
    }

    //format for the file name is: readings_date_time.csv and saves in the downloads folder of the mobile

    private void saveReadingsToCSV() {
       if (csvstring.length() <100) {
            Toast.makeText(this, "No data to save.", Toast.LENGTH_SHORT).show();
            return;
        }
        visitdate = visitdate.replace(":", "");
        visittime = visittime.replace(":", "");
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(visitdate);
            } catch (ParseException e) {
                Log.d("time parsing",e.toString());
            }

// Format visit time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm", Locale.getDefault());
            Date time = null;
            try {
                time = timeFormat.parse(visittime);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("time parsing",e.toString());
            }

// Combine formatted date and time
            SimpleDateFormat combinedFormat = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault());
            String formattedDateTime = combinedFormat.format(date) + "_" + timeFormat.format(time);


            String fileName = "readings_" + visitdate+"_"+visittime + ".csv";
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(directory, fileName);
            FileWriter writer = new FileWriter(file);
            writer.append("Frequency,Bioimpedance,Phase,Electrode Combination 1, Electrode Combination 2\n");
            // Write the received data to the CSV file.
            if(csvstring!=null)
            {
                writer.append(csvstring.toString());

//                Bitmap chartBitmap = linechart.getChartBitmap();

// Specify the file path where you want to save the image
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/chart_image.png";

// Save the chart image to the specified file path
                linechart.saveToGallery(filePath);

// Display a message to the user
                Toast.makeText(this, "Chart saved to " + filePath, Toast.LENGTH_SHORT).show();
            }

            // Close the FileWriter.
            writer.close();

            // Clear the received data.
            receivedData.setLength(0);
//statusViewModal.insertstatus(new StatusList(patiiid,Integer.parseInt(visitid),visitdate,visittime));
            Toast.makeText(this, "Data saved to Downloads/" + fileName, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveReadingsToSQLite(double frequency11, double bioimpedance, double phase, int electrodeCombination1, int electrodeCombination2) {




            // Insert the data into SQLite database.
            //dbHelper.insertReading(Integer.parseInt(patiiid),visitdate,visittime,getCurrentTime(),frequency11, bioimpedance, phase, electrodeCombination1, electrodeCombination2);

    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    // Function to get the current time in the desired format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    // Function to get the current date and time in the desired format
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    //Real time graph plotting
    private Runnable updateChartWithData(List<CustomDataPoint> dataPoint) {
        // Create new LineData and LineDataSet

        LineDataSet frequencyDataSet = new LineDataSet(frequencyEntries, "Bioimpedance");
        frequencyDataSet.setColor(Color.BLUE);
        frequencyDataSet.setValueTextColor(Color.BLACK);
        frequencyDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineDataSet phaseDataSet = new LineDataSet(phaseEntries, "Phase");
        phaseDataSet.setColor(Color.RED);
        phaseDataSet.setValueTextColor(Color.BLACK);
        phaseDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        for (int i = 0; i < rzMagValues.size(); i++) {

            if(!(rzMagValues.get(i).floatValue()<0.0))
                frequencyDataSet.addEntry(new Entry(lastProcessingTime-startTime, rzMagValues.get(i).floatValue()));

            break;}

        for (int i = 0; i < rzPhaseValues.size(); i++) {
            phaseDataSet.addEntry(new Entry(lastProcessingTime-startTime, (rzPhaseValues.get(i).floatValue()))); break;
        }
        LineData lineData = new LineData(frequencyDataSet);       // Update with your appropriate data
        LineData lineData1 = new LineData(phaseDataSet);

        // Add to the Frequency data set





        // Update the chart
        linechart.setData(lineData);

        linechart.getDescription().setText("Timed Data Chart");
    //    YourXAxisFormatter.updateValues(frequency11, p1, p2);
       // linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());

        ValueFormatter xFormatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // Format the Y-axis labels as needed
                return String.format("%.1f", value);
            }
        };
        linechart.getXAxis().setLabelCount(3,true);
        linechart1.getXAxis().setLabelCount(3,true);
        linechart.getXAxis().setValueFormatter(
                xFormatter);
        linechart1.getXAxis().setValueFormatter(
                xFormatter);
        //linechart.getXAxis().set// Implement your X-axis formatting
       // linechart.getAxisLeft().setAxisMinimum(150000f);
       // linechart.getAxisLeft().setAxisMimum(150000f);
      //  linechart.getAxisLeft().setAxisMinimum(400f);
     linechart.getAxisRight().setEnabled(false);
        linechart1.setData(lineData1);
        linechart1.getDescription().setText("Timed Phase Chart");


     //   linechart1.getAxisLeft().setAxisMinimum(-80f);

      linechart1.getAxisRight().setEnabled(false);

        // Add data to the chart
        // Update with your appropriate data
        ; // Add to the Phase data set
rzMagValues.clear();
rzPhaseValues.clear();

        // Refresh the chart
        linechart.notifyDataSetChanged();
        linechart.invalidate();
        linechart1.notifyDataSetChanged();
        linechart1.invalidate();
        return null;
    }
@Override
public void onDestroy()
{
    super.onDestroy();
    unregisterReceiver(dataReceiver);
    disconnect();
}
    //Handling start events
    @Override
    public void onStart() {
        super.onStart();
    //    onAttach(this);
        this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        IntentFilter filter = new IntentFilter(ACTION_DATA_AVAILABLE);
        registerReceiver(dataReceiver, filter);

        if(service != null)
            service.attach(this);
        else
            this.startService(new Intent(this, SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
        if(controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
        if(service != null)
            service.attach(this);
        else
            this.startService(new Intent(this, SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !this.isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions

    public void onAttach(Activity activity) {

        this.bindService(new Intent(this, SerialService.class), this, Context.BIND_AUTO_CREATE);
        if(service != null)
            service.attach(this);
        else
            this.startService(new Intent(this, SerialService.class));
    }


    public void onDetach() {
        try { unbindService(this); } catch(Exception ignored) {}

    }
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ACTION_DATA_AVAILABLE);
        registerReceiver(dataReceiver, filter);

        //  onAttach(this);
//        connectionContinue(driver,usbManager);
        this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        if(service != null)
            service.attach(this);
        else
            this.startService(new Intent(this, SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
        if(controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
    }

    @Override
    public void onPause() {
        this.unregisterReceiver(broadcastReceiver);
        if(csvstring.length()>100)
         saveReadingsToCSV();
        if(controlLines != null)
            controlLines.stop();
        super.onPause();
    }


    //Connection with backend USB Communication service
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart ) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
        this.registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        if(service != null)
            service.attach(this);
        else
            this.startService(new Intent(this, SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            this.runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public void onBackStackChanged() {
        if(csvstring.length()>100)
        saveReadingsToCSV();
        getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }


    String patiiid = "";
    String visitid = "";
    private BLEService bleService;
    private boolean isBound = false;


    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BLEService.LocalBinder binder = (BLEService.LocalBinder) service;
            bleService = binder.getService$app_debug();
            isBound = true;
            //  bleService.registerDataCallback(this@RecordScreen)
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // Handle service disconnected
        }
    };

    private final ScanSettings scanSettings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build();

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice().getName() != null &&
                    result.getDevice().getName().trim().equals("MedDev") &&
                    isValidBluetoothAddress(result.getDevice().getAddress())) {
                stopBLEScan();
               connectDevice(result.getDevice().getAddress());
                Log.d("Found",result.getDevice().getName().trim().toString());
            } else {
                if (result.getDevice().getName() != null){
Log.d("Found",result.getDevice().getName().trim().toString());}
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onScanFailed(int errorCode) {
            // Handle scan failure
            Log.d("failed","restarted");
            bleScanner.stopScan(scanCallback);
            bleScanner.startScan(null, scanSettings, scanCallback);
        }
    };

    private boolean isValidBluetoothAddress(String address) {
        if (address == null) {
            return false;
        }

        String addressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        String reversedAddressPattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        return address.matches(addressPattern) || address.matches(reversedAddressPattern);
    }

    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN = 1;

    @SuppressLint("MissingPermission")
     void startBLEScan() {
        if (!hasRequiredRuntimePermissions(this)) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.BLUETOOTH_SCAN},
                        MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN);
            }
        } else {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
             bleScanner.startScan(null, scanSettings, scanCallback);
            Toast.makeText(this, "Scanning started", Toast.LENGTH_SHORT).show();
        }

    }
    private static final int REQUEST_LOCATION_PERMISSION = 2018;
    private static final String TAG = "RecordScreen";
    private static final int REQUEST_ENABLE_BT = 1000;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothLeScanner bleScanner;

// Initialize BluetoothAdapter and BluetoothLeScanner in onCreate or the appropriate method


    public static boolean hasPermission(Context context, String permissionType) {
        return ContextCompat.checkSelfPermission(context, permissionType) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasRequiredRuntimePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return hasPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            return hasPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    @SuppressLint("MissingPermission")
    private void stopBLEScan() {
        try {
             bleScanner.stopScan(scanCallback);
            Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle the exception (e.g., log an error)
        }
        Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show();
    }

    private void checkLocationPermission() {
        // Check and request location permission
    }

    private void displayRationale() {
        // Display rationale for location permission
    }


    private boolean isLocationPermissionEnabled() {
        // Check if location permission is enabled
        return false;
    }

    private boolean isAboveMarshmallow() {
        // Check if Android version is above Marshmallow
        return false;
    }

    @SuppressLint("MissingPermission")
    private void initBLEModule() {
        // Initialize BLE module
    }

    private void registerServiceReceiver() {
        // Register GATT update receiver
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        // Create GATT update intent filter
        return null;
    }

    private void unRegisterServiceReceiver() {
        // Unregister GATT update receiver
    }




    private void connectDevice(String deviceAddress) {
        new Handler().postDelayed(() -> {
            BLEConnectionManager.INSTANCE.initBLEService(this);

            if (   BLEConnectionManager.INSTANCE.connect(deviceAddress)) {
                Toast.makeText(this, "DEVICE CONNECTED", Toast.LENGTH_SHORT).show();
                Log.d("connected",deviceAddress);
                UUID characteristicUuid = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

                sendArrayToBLEService("YourDataArray", characteristicUuid, deviceAddress);
            } else {
                Toast.makeText(this, "DEVICE CONNECTION FAILED", Toast.LENGTH_SHORT).show();
            }
        }, 100);
    }

    private void sendArrayToBLEService(String dataArray, UUID characteristicUuid, String deviceAddress) {
         Intent bleServiceIntent = new Intent(this, BLEService.class);
         bleServiceIntent.putExtra("characteristicUuid", characteristicUuid);
         bleServiceIntent.putExtra("dataArray", dataArray);
         bleServiceIntent.putExtra("deviceAddress", deviceAddress);
         startService(bleServiceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // case ENABLE_BLUETOOTH_REQUEST_CODE:
            // if (resultCode != RESULT_OK) {
            // promptEnableBluetooth();
            // }
            // break;
        }
    }

    // Callback for BluetoothGattCallback
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        sendDataToCharacteristic(characteristic);
                    }
                }
            }
        }
    };

    private void sendDataToCharacteristic(BluetoothGattCharacteristic characteristic) {
        // ArrayList<Double> dataArray = new ArrayList<>();
        // Intent bleServiceIntent = new Intent(this, BLEService.class);
        // bleServiceIntent.putExtra("characteristicUuid", characteristic.getUuid().toString());
        // bleServiceIntent.putExtra("dataArray", dataArray);
        // startService(bleServiceIntent);
    }

  /**  private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra(com.example.eegreader.BLEModule.BLEService.EXTRA_DATA);
            String uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID);
            SpannableStringBuilder spn = new SpannableStringBuilder();

            // Data processing
            if (data != null) {
                Log.d("processing", "here");

                if (data.equals("^@")) {
                    Toast.makeText(this, "Expecting non-null values", Toast.LENGTH_SHORT).show();
                }
                if (hexEnabled) {
                    spn.append(TextUtil.toHexString(data)).append('\n');
                } else {
                    String msg = data;

                    // adapter.updateData(msg)
                    if (newline.equals(TextUtil.newline_crlf) && !msg.isEmpty()) {
                        // Don't show CR as ^M if directly before LF
                        msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);

                        if (pendingNewline && !msg.isEmpty() && msg.charAt(0) == '\n') {
                            if (spn.length() >= 2) {
                                spn.delete(spn.length() - 2, spn.length());
                            } else {
                                Editable edt = receiveText.getEditableText();
                                if (edt != null && edt.length() >= 2) {
                                    edt.delete(edt.length() - 2, edt.length());
                                }
                            }
                        }
                        pendingNewline = !msg.isEmpty() && msg.charAt(msg.length() - 1) == '\r';
                    }
                    spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
                    if (!hexEnabled) {
                        String msg1 = msg;
                        receivedData.append(msg1);
                    }
                }

                // Number extraction from received string
                if (containsNumber(receivedData.toString()) || containsDecimal(receivedData.toString())) {
                    dataBuffer.append(receiveText.getText());

                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {
                        processAndDisplayData(receivedData.toString());
                        lastProcessingTime = currentTime;

                        // Clear the buffer after processing
                        dataBuffer.setLength(0);
                    }
                } else {
                    // Handle non-numeric data here if needed
                }
            }
        }
    };
*/
}

