package com.example.eegreader.USB;

//May find applications in future versions


//import static com.example.eegreader.RecyclerView.PatientRecyclerAdaptorKt.patientid;





import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eegreader.BLEModule.BLEConstants;
import com.example.eegreader.Graph.CustomDataPoint;
import com.example.eegreader.Graph.YourXAxisFormatter;

import com.example.eegreader.R;
import com.example.eegreader.RecyclerView.DataAdapter;
import com.example.eegreader.database.NoSQL.Reading;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class    TerminalFragment extends Fragment implements ServiceConnection, SerialListener {
    UsbDevice device = null;
    private final ArrayList<DevicesFragment.ListItem> listItems = new ArrayList<>();
    private ArrayAdapter<DevicesFragment.ListItem> listAdapter;
    private int baudRate = 9600;
    boolean permissionasking=false;
    private enum Connected { False, Pending, True }
    private long startTime = System.currentTimeMillis();
    private BroadcastReceiver broadcastReceiver;
    private int deviceId, portNum;
    private UsbSerialPort usbSerialPort;
    private SerialService service;

    private TextView receiveText;
    private TextView sendText;
    private TextView sendText1;
    private TextView sendText2;
    private TextView sendText3;
    private ControlLines controlLines;
    // Declare dataPoints as a list of your custom data points
    List<CustomDataPoint> dataPoints = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextUtil.HexWatcher hexWatcher;
    List<Entry> frequencyEntries = new ArrayList<>();
    List<Entry> phaseEntries = new ArrayList<>();
    List<Entry> bioimpedanceEntries = new ArrayList<>();
    private StringBuilder receivedData = new StringBuilder();
    private LineChart linechart;
    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean controlLinesEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    private List<String> dataList = new ArrayList<>(); // Your data source
    private DataAdapter adapter = new DataAdapter(dataList);
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    Reading reading = new Reading();
    private Handler handler = new Handler();


    private static final int CHART_UPDATE_INTERVAL = 1000; // Update the chart every 1 second
    private Runnable updateChartRunnable = new Runnable() {
        @Override
        public void run() {
            // Update the chart here with new data
          //updateChartWithData(); // Implement this method

        }
    };

    public TerminalFragment() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Constants.INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    connect(granted);
                }
            }
        };
    }
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hello", "Received Intent: " + intent.getExtras());
            Bundle extrasBundle = intent.getExtras();
            if (extrasBundle != null) {
                for (String key : extrasBundle.keySet()) {
                    Object value = extrasBundle.get(key);
                    Log.d("hello", "Key: " + key + ", Value: " + value);
                }
            } else {
                Log.d("hello", "No extras in the intent.");
            }
            String data = intent.getStringExtra(com.example.eegreader.BLEModule.BLEService.EXTRA_DATA);
            String uuId = intent.getStringExtra(BLEConstants.EXTRA_UUID);

            Log.i("hello", "ACTION_DATA_AVAILABLE " + data);
        }
    };
    private void registerServiceReceiver() {
        if (getActivity() != null) {
            IntentFilter filter = new IntentFilter(com.example.eegreader.BLEModule.BLEConstants.ACTION_DATA_AVAILABLE);
            getActivity().registerReceiver(dataReceiver, filter);
        }
    }

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Optional if you want offline persistence
        databaseReference = FirebaseDatabase.getInstance().getReference("patients");
        try {
            patiiiid = getArguments().getString("patiid");
            visitiid = getArguments().getString("visitid");

            if (patiiiid != null && visitiid != null) {
                // Your code when both values are not null
            } else {
                // Handle the case where either or both values are null
            }
        } catch (NullPointerException e) {
            // Handle the exception or provide a default value if necessary
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//Log.d("patient id for db",patiiiid);
//Log.d("visit id for db",visitiid);
        setHasOptionsMenu(true);
        setRetainInstance(true);
      connectionmanager();

    }
public void connectionmanager()
{
    UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
    UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
    UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
    listItems.clear();
    for(UsbDevice device : usbManager.getDeviceList().values()) {
        UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
        if(driver == null) {
            driver = usbCustomProber.probeDevice(device);
        }
        if(driver != null) {
            for(int port = 0; port < driver.getPorts().size(); port++)
            {   listItems.add(new DevicesFragment.ListItem(device, port, driver));
                Bundle args = new Bundle();
                deviceId= device.getDeviceId();
                port= 0; // You can modify this if needed
                 baudRate=9600;
              break;}
        } else {
            listItems.add(new DevicesFragment.ListItem(device, 0, null));
        }

}}
    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect1();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }
    private Runnable updateChartWithData(List<CustomDataPoint> dataPoint) {
        Log.d("I am in","chart");
        // Create new LineData and LineDataSet
        LineDataSet frequencyDataSet = new LineDataSet(frequencyEntries, "Frequency");
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
        //    frequencyDataSet.addEntry(new Entry(timeElapsed, dataPoint.getRzMagValues().floatValue()));

        break;}
        // Add rzPhaseValues to the phaseEntries
        for (int i = 0; i < rzPhaseValues.size(); i++) {        phaseDataSet.addEntry(new Entry(lastProcessingTime-startTime, rzPhaseValues.get(i).floatValue())); break;
        }
        LineData lineData = new LineData(frequencyDataSet,phaseDataSet);       // Update with your appropriate data


        // Add to the Frequency data set






        // Update the chart
        linechart.setData(lineData);
        linechart.getDescription().setText("Timed Data Chart");
        //linechart.getXAxis().setValueFormatter(new YourXAxisFormatter(frequency11, p1, p2)); // Implement your X-axis formatting
        linechart.getAxisLeft().setAxisMinimum(0f); // Set Y-axis minimum value
        linechart.getAxisRight().setEnabled(false);

        // Add data to the chart
        // Update with your appropriate data
        ; // Add to the Phase data set
        Log.d("frequency for chart", String.valueOf(lineData));
        // Refresh the chart
        linechart.notifyDataSetChanged();
        linechart.invalidate();
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        Log.d("I am ","started");
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        if(controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class));
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        if(controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(broadcastReceiver);
        if(controlLines != null)
            controlLines.stop();
        super.onPause();
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
Log.d("connected","connected");
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    @Override
    public void onBackStackChanged() {

    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        recyclerView = view.findViewById(R.id.recview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        sendText = view.findViewById(R.id.input1);
        sendText1 = view.findViewById(R.id.input2);
        sendText2 = view.findViewById(R.id.input3);
        sendText3 = view.findViewById(R.id.input4);

        Log.d("sending", String.valueOf(sendText));
        hexWatcher = new TextUtil.HexWatcher(sendText);
        hexWatcher.enable(hexEnabled);
        sendText.addTextChangedListener(hexWatcher);
        sendText.setHint(hexEnabled ? "HEX mode" : "");
        linechart = view.findViewById(R.id.lineChart);
        linechart.getXAxis().setEnabled(false);
        linechart.getXAxis().setEnabled(true);

        linechart.getXAxis().setDrawGridLines(false);
        linechart.getAnimation();
        linechart.getDescription().setEnabled(false);
        linechart.getAxisLeft().setDrawGridLines(true);

// Set Y-axis label count and step
        linechart.getAxisLeft().setLabelCount(11, true); // Number of labels (10 steps + 1)
        linechart.getAxisLeft().setAxisMinimum(0f); // Minimum value for Y-axis
        linechart.getAxisLeft().setAxisMaximum(500f); // Maximum value for Y-axis

// Calculate the step value
        float stepValue = 100f;
        linechart.getAxisLeft().setGranularity(stepValue);

// Customize the Y-axis value formatter (Optional)
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

        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()+","+sendText1.getText().toString()+","+sendText2.getText().toString()+","+sendText3.getText().toString()));
        controlLines = new ControlLines(view);
        return view;
    }





    /*
     * Serial + UI
     */
    private void connect() {
        connect(null);
    }

    private void connect(Boolean permissionGranted) {


        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);


        for(UsbDevice v : usbManager.getDeviceList().values())
            if(v.getDeviceId() == deviceId)
                device = v;
        if(device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if(driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }

        if( permissionasking==false&&permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            Log.d("entering","here");
        //    permissionasking = true;
            int flags1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent1 = PendingIntent.getBroadcast(getActivity(), 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags1);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent1);

           connect();
return;
        }
        usbSerialPort = driver.getPorts().get(portNum);


        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));
        Log.d("I am ","started");
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class));
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
        if(controlLinesEnabled && controlLines != null && connected == Connected.True)
            controlLines.start();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change

        connected = Connected.Pending;
        try {
            usbSerialPort.open(usbConnection);
            try {
                usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (UnsupportedOperationException e) {
                status("Setting serial parameters failed: " + e.getMessage());
            }
            Log.d("connecting", String.valueOf(permissionGranted));
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), usbConnection, usbSerialPort);
            service.connect(socket);
            // usb connect is not asynchronous. connect-success and connect-error are returned immediately from socket.connect
            // for consistency to bluetooth/bluetooth-LE app use same SerialListener and SerialService classes
            onSerialConnect();


        } catch (Exception e) {
            onSerialConnectError(e);
            Log.d("error",e.toString());
        }
    }

    private void onRequestPermission(Boolean permissionGranted, UsbManager usbManager, UsbSerialDriver driver) {

        if( permissionGranted == null && !usbManager.hasPermission(driver.getDevice())) {
            Log.d("entering","here");
            int flags1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent1 = PendingIntent.getBroadcast(getActivity(), 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags1);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent1);

        }
    }
private void disconnect1()
{
    Log.d("disconnected","disconnected");

    saveReadingsToCSV();
    connected = Connected.False;
    controlLines.stop();
    service.disconnect();
    usbSerialPort = null;
    destroyTerminalFragment();
}
    private void disconnect() {
        Log.d("disconnected","disconnected");

        saveReadingsToCSV();
        connected = Connected.False;
        controlLines.stop();
        service.disconnect();
        usbSerialPort = null;

        replaceTerminalFragment();
    }
    private void destroyTerminalFragment(){
        FragmentManager fragmentManager = getParentFragmentManager(); // Use the appropriate FragmentManager
        TerminalFragment existingTerminalFragment = (TerminalFragment) fragmentManager.findFragmentById(R.id.fragment);
        fragmentManager.beginTransaction().remove(existingTerminalFragment).commit(); // Remove the existing fragment

    }
    private void replaceTerminalFragment() {
        // Create a new instance of TerminalFragment
        DevicesFragment newTerminalFragment = new DevicesFragment();
        FragmentManager fragmentManager = getParentFragmentManager(); // Use the appropriate FragmentManager
        TerminalFragment existingTerminalFragment = (TerminalFragment) fragmentManager.findFragmentById(R.id.fragment);


        // Set arguments for the new fragment based on the existing fragment's arguments
        newTerminalFragment.setArguments(getArguments());

        // Get the FragmentManager

        // Start a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the existing TerminalFragment with the new one
        transaction.replace(R.id.fragment, newTerminalFragment); // Replace R.id.fragment_container with the ID of your fragment container
        // Find the existing fragment
        if (existingTerminalFragment != null) {
            fragmentManager.beginTransaction().remove(existingTerminalFragment).commit(); // Remove the existing fragment


        }
        // Add the transaction to the back stack (optional)
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

        // Destroy the existing TerminalFragment (optional)
     }

    private void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
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
            receiveText.append(spn);

            service.write(msg);
            Log.d("written",msg);
        } catch (SerialTimeoutException e) {
            status("write timeout: " + e.getMessage());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }
   /** private void send(byte[] data) {
        if (connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SpannableStringBuilder spn = new SpannableStringBuilder();
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            service.write(data); // Send the byte array directly
            // Optionally, you can log or display the sent data here.
            Log.d("written data is ", Arrays.toString(data));
            receiveText.append(spn); // Append any debug information as needed.
        } catch (SerialTimeoutException e) {
            status("write timeout: " + e.getMessage());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }*/
   private static final long PROCESSING_INTERVAL = 15 * 1000; // 60 seconds in milliseconds
    private long lastProcessingTime = 0;
   double rzMagValue = 0.0;
   String[] noparts= new String[0];
    Handler chartUpdateHandler = new Handler();
    String[] lines=new String[0];
    String[] lines1=new String[0];
    String[] lines2=new String[0];
    double rzPhaseValue = 0.0;
    List<Double> rzMagValues = new ArrayList<>();
    List<Double> rzPhaseValues = new ArrayList<>();
    StringBuilder numbers=new StringBuilder();
    private StringBuilder dataBuffer = new StringBuilder();
    ArrayList numbers1=new ArrayList();
    private void receive(ArrayDeque<byte[]> datas) {
       /** SpannableStringBuilder spn = new SpannableStringBuilder();
        List<CustomDataPoint> dataPoints = new ArrayList<>();
        for (byte[] data : datas) {
            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                String msg = new String(data);
                Log.d("msg", msg);
                adapter.updateData(msg);

                String[] parts = msg.split(","); // Split data using a comma as the delimiter
                Log.d("database patient id",patiiiid);
                Log.d("database visit id",visitiid);
                if (parts.length == 3) { // Assuming there are four values: time, frequency, phase, and bioimpedance
                    try {
                       // long time = Long.parseLong(parts[0].trim()); // Assuming time is a long
                        float frequency = Float.parseFloat(parts[1].trim()); // Assuming frequency is a float
                        float phase = Float.parseFloat(parts[2].trim()); // Assuming phase is a float
                        float bioimpedance = Float.parseFloat(parts[3].trim()); // Assuming bioimpedance is a float
                        long currentTime = System.currentTimeMillis();

                        long timeElapsed = currentTime - startTime;
                        // Create a DataPoint object with the parsed values
                        CustomDataPoint dataPoint = new CustomDataPoint( timeElapsed,frequency, phase, bioimpedance);
                        dataPoints.add(dataPoint);
                        reading.setFrequency(String.valueOf(frequency));
                        reading.setPhaseAngle(String.valueOf(phase));
                        reading.setBioimpedance(String.valueOf(bioimpedance));
                        for (int i = 0; i < parts.length; i++) {

                            frequencyEntries.add(new Entry(dataPoint.getTime(), dataPoint.getFrequency()));
                            phaseEntries.add(new Entry(dataPoint.getTime(), dataPoint.getPhase()));
                            bioimpedanceEntries.add(new Entry(dataPoint.getTime(), dataPoint.getBioimpedance()));
                        }
                        Log.d("database patient id",patiiiid);
                        Log.d("database visit id",visitiid);
                        // Generate a unique key for the reading
                        String readingId = databaseReference.child("patients").child(patiiiid).child("visits").child(visitiid).child("readings").push().getKey();
//frequencyEntries.set(0,frequency)
// Update the database with the reading data using the Reading object
databaseReference.child("patients").child(patiiiid).child("visits").child(visitiid).child("readings").child(readingId).setValue(reading);
                        LineDataSet frequencyDataSet = new LineDataSet(frequencyEntries, "Frequency");
                        frequencyDataSet.setColor(Color.BLUE);
                        frequencyDataSet.setValueTextColor(Color.BLACK);

                        LineDataSet phaseDataSet = new LineDataSet(phaseEntries, "Phase");
                        phaseDataSet.setColor(Color.RED);
                        phaseDataSet.setValueTextColor(Color.BLACK);

                        LineDataSet bioimpedanceDataSet = new LineDataSet(bioimpedanceEntries, "Bioimpedance");
                        bioimpedanceDataSet.setColor(Color.GREEN);
                        bioimpedanceDataSet.setValueTextColor(Color.BLACK);

                        LineData lineData = new LineData();
                        lineData.addDataSet(frequencyDataSet);
                        lineData.addDataSet(phaseDataSet);
                        lineData.addDataSet(bioimpedanceDataSet);


                        linechart.setData(lineData);
                        linechart.getDescription().setText("Timed Data Chart");
                        linechart.getXAxis().setValueFormatter(new YourXAxisFormatter()); // Implement your X-axis formatting
                        linechart.getAxisLeft().setAxisMinimum(0f); // Set Y-axis minimum value
                        linechart.getAxisRight().setEnabled(false);

                    } catch (NumberFormatException e) {
                        // Handle parsing errors
                        Log.e("Data Parsing", "Error parsing data: " + msg);
                    }
                }

                if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                    // don't show CR as ^M if directly before LF
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                    // special handling if CR and LF come in separate fragments
                    if (pendingNewline && msg.charAt(0) == '\n') {
                        if (spn.length() >= 2) {
                            spn.delete(spn.length() - 2, spn.length());
                        } else {
                            Editable edt = receiveText.getEditableText();
                            if (edt != null && edt.length() >= 2)
                                edt.delete(edt.length() - 2, edt.length());
                        }
                    }
                    pendingNewline = msg.charAt(msg.length() - 1) == '\r';
                }
                spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
                if (!hexEnabled) {
                    String msg1 = new String(msg);
                    receivedData.append(msg1);
                }
            }
        }
        receiveText.append(spn);
        ;

        Log.d("this is received text", String.valueOf(receivedData));*/

        SpannableStringBuilder spn = new SpannableStringBuilder();


        for (byte[] data : datas) {
            if(new String(data) =="^@")
                Toast.makeText(getActivity(),"Expecting non-null values",Toast.LENGTH_SHORT);
            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                String msg = new String(data);

                //adapter.updateData(msg);
                if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                    // Don't show CR as ^M if directly before LF
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);

                    if (pendingNewline && msg.charAt(0) == '\n') {
                        if (spn.length() >= 2) {
                            spn.delete(spn.length() - 2, spn.length());
                        } else {
                            Editable edt = receiveText.getEditableText();
                            if (edt != null && edt.length() >= 2)
                                edt.delete(edt.length() - 2, edt.length());
                        }
                    }
                    pendingNewline = msg.charAt(msg.length() - 1) == '\r';
                }
                spn.append(TextUtil.toCaretString(msg, newline.length() != 0));
                if (!hexEnabled) {
                    String msg1 = new String(msg);
                    receivedData.append(msg1);
                    //    Log.d("received datas", msg1);
                }


          /**  if (containsNumber(msg) || containsDecimal(msg)) {
                    dataBuffer.append(msg);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {
                        processAndDisplayData(spn.toString());
                        lastProcessingTime = currentTime;

                        // Clear the buffer after processing
                        dataBuffer.setLength(0);
                        msg=null;

                    }
                }
                        else {
                    // Handle non-numeric data here if needed
                }*/
                String finalMsg = msg;


            }}

      //  receiveText.append(spn);


      if (containsNumber(String.valueOf(receivedData)) || containsDecimal(String.valueOf(receivedData))) {
            dataBuffer.append(receivedData);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastProcessingTime >= PROCESSING_INTERVAL) {

                processAndDisplayData(receivedData.toString());
                lastProcessingTime = currentTime;

                // Clear the buffer after processing
                dataBuffer.setLength(0);
receivedData.delete(0,receivedData.length()-1);

            }
        }
        else {
            // Handle non-numeric data here if needed
        }

        }
        public void processAndDisplayData(String finalMsg)
        {
            Runnable backgroundTask = new Runnable() {
                @Override
                public void run() {
                    lines=finalMsg.split(",");
              //      Log.d("lines is", Arrays.toString(lines));


            String dataString = numbers.toString();

// Split the string based on commas

            noparts = finalMsg.split(",");
            ;
            for (String part : noparts) {
                lines1=part.split(":");


                   //     rzPhaseValues.add(Double.valueOf(part.trim()));
for(String line2:lines1)
{
    lines2=line2.split(" ");
    String[] lines4 = line2.split("\n");

    for(String line3:lines2) {

        if (isDecimal(line3)) {

            if (rzMagValues.size() < 3 ) {
                if(Double.valueOf(line3) > 100.00)rzMagValues.add(Double.valueOf(line3));




            } else if (rzMagValues.size() >= 3 && Double.valueOf(line3) > 100.00) {
                rzMagValues.clear();
                rzMagValues.add(Double.valueOf(line3));
            }


        }

    }
    for(String line3:lines4) {
        Log.d("line 4is",line3.trim());

        if (isDecimal(line3.trim())) {

            if (rzPhaseValues.size() < 3&& Double.valueOf(line3)>70 && Double.valueOf(line3) < 100.00) {

                    rzPhaseValues.add(Double.valueOf(line3.trim()));



            } else if (rzMagValues.size() >= 3&&Double.valueOf(line3)>70&& Double.valueOf(line3) < 100.00) {
                rzPhaseValues.clear();
                rzPhaseValues.add(Double.valueOf(line3.trim()));
            }

            Log.d("lines3 is", String.valueOf(rzPhaseValues));
        }
    }
    }

                //Log.d("numbers array is", String.valueOf(numbers1));
            }


            // Split data using a comma as the delimiter

            // Assuming there are four values: time, frequency, phase, and bioimpedance
            try {

                long currentTime = System.currentTimeMillis();
                long timeElapsed = currentTime - startTime;

                // Create a DataPoint object with the parsed values
                CustomDataPoint dataPoint = new CustomDataPoint(timeElapsed, rzMagValues, rzPhaseValues);
                dataPoints.add(dataPoint);

                // Update your data sets


                // Update the chart with the new data
                final String[] data = {null};
                      getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(rzMagValues.size()!=0&&rzPhaseValues.size()!=0)
                     data[0] = new String(rzMagValues.get(0).toString().concat(","+rzPhaseValues.get(0).toString()));
                  //  Log.d("data passed", data[0]);
                // UI-related code to update the chart goes here
                updateChartWithData(dataPoints);
String[] parts=finalMsg.split(",");
                            // Update your UI here

                            adapter.updateData(data[0]);

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
    private boolean isDecimal(String str) {
        return str.matches("-?\\d*\\.?\\d+");
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


    private void saveReadingsToCSV() {
        if (receivedData.length() == 0) {
            Toast.makeText(getActivity(), "No data to save.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date());

            String fileName = "readings_" + currentDateTime + ".csv";
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(directory, fileName);
            FileWriter writer = new FileWriter(file);

            // Write the received data to the CSV file.
            writer.append(receivedData.toString());

            // Close the FileWriter.
            writer.close();

            // Clear the received data.
            receivedData.setLength(0);

            Toast.makeText(getActivity(), "Data saved to Downloads/" + fileName, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error saving data.", Toast.LENGTH_SHORT).show();
        }
    }
        void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;

Log.d("I","connect");
        if(controlLinesEnabled)
            controlLines.start();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);

    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    class ControlLines {
        private static final int refreshInterval = 200; // msec

        private final Handler mainLooper;
        private final Runnable runnable;
        private final LinearLayout frame;
        private final ToggleButton rtsBtn, ctsBtn, dtrBtn, dsrBtn, cdBtn, riBtn;

        ControlLines(View view) {
            mainLooper = new Handler(Looper.getMainLooper());
            runnable = this::run; // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks

            frame = view.findViewById(R.id.controlLines);
            rtsBtn = view.findViewById(R.id.controlLineRts);
            ctsBtn = view.findViewById(R.id.controlLineCts);
            dtrBtn = view.findViewById(R.id.controlLineDtr);
            dsrBtn = view.findViewById(R.id.controlLineDsr);
            cdBtn = view.findViewById(R.id.controlLineCd);
            riBtn = view.findViewById(R.id.controlLineRi);
            rtsBtn.setOnClickListener(this::toggle);
            dtrBtn.setOnClickListener(this::toggle);
        }

        private void toggle(View v) {
            ToggleButton btn = (ToggleButton) v;
            if (connected != Connected.True) {
                btn.setChecked(!btn.isChecked());
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String ctrl = "";
            try {
                if (btn.equals(rtsBtn)) { ctrl = "RTS"; usbSerialPort.setRTS(btn.isChecked()); }
                if (btn.equals(dtrBtn)) { ctrl = "DTR"; usbSerialPort.setDTR(btn.isChecked()); }
            } catch (IOException e) {
                status("set" + ctrl + " failed: " + e.getMessage());
            }
        }

        private void run() {
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getControlLines();
                rtsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RTS));
                ctsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CTS));
                dtrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DTR));
                dsrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DSR));
                cdBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CD));
                riBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RI));
                mainLooper.postDelayed(runnable, refreshInterval);
            } catch (IOException e) {
                status("getControlLines() failed: " + e.getMessage() + " -> stopped control line refresh");
            }
        }

        void start() {
            frame.setVisibility(View.VISIBLE);
            if (connected != Connected.True)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getSupportedControlLines();
                if (!controlLines.contains(UsbSerialPort.ControlLine.RTS)) rtsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CTS)) ctsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DTR)) dtrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DSR)) dsrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CD))   cdBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.RI))   riBtn.setVisibility(View.INVISIBLE);
                run();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "getSupportedControlLines() failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        void stop() {
            frame.setVisibility(View.GONE);
            mainLooper.removeCallbacks(runnable);
            rtsBtn.setChecked(false);
            ctsBtn.setChecked(false);
            dtrBtn.setChecked(false);
            dsrBtn.setChecked(false);
            cdBtn.setChecked(false);
            riBtn.setChecked(false);
        }
    }
String patiiiid=null;
    String visitiid=null;
    public static TerminalFragment newInstance(String patiiid, String visitid) {
        TerminalFragment fragment = new TerminalFragment();
        Bundle args = new Bundle();
        args.putString("patiiid", patiiid);
args.putString("visitid",visitid);
        fragment.setArguments(args);
        return fragment;
    }
}

