package com.example.eegreader.Graph;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eegreader.R;
import com.example.eegreader.database.CsvDataAdapter;
import com.example.eegreader.database.CsvParser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Archives extends AppCompatActivity {
private String visitdate;
private String visitTime;
    private String patiiid;
    private TextView pid;
    private TextView vid;
    private TextView vdate;
    private TextView vtime;
    private String visitid;
private int count=0;
    List<Entry> frequencyEntry = new ArrayList<>();
    List<Entry> phaseEntry = new ArrayList<>();


    private CsvDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.archive);

        RecyclerView recyclerView = findViewById(R.id.recview2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CsvDataAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
visitdate=getIntent().getStringExtra("visitdate");
visitTime=getIntent().getStringExtra("visittime");
        //Toolbar toolbar = findViewById(R.id.toolbar);
        patiiid = getIntent().getStringExtra("patid");
        visitid = getIntent().getStringExtra("visitid");
        pid = findViewById(R.id.patid);
        vid = findViewById(R.id.vid);
        vtime = findViewById(R.id.vtime);
        vdate = findViewById(R.id.vdate);
        pid.setText(patiiid);
        vid.setText(visitid);
        vtime.setText(visitTime);
        vdate.setText(visitdate);
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Create LineData object with our datasets
        LineData lineData = new LineData(new ArrayList<>());
        LineData lineData1 = new LineData(new ArrayList<>());
        adapter = new CsvDataAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        if (visitTime != null) {
            Log.d("file", "downloading");
            // Parse the visit time to a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD", Locale.US);
            Date visitTimeDate;
            try {
                visitTimeDate = sdf.parse(visitTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            if (visitTimeDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(visitTimeDate);

                // Create a loop to iterate over 30-minute intervals
                long endTime = calendar.getTimeInMillis() + 30 * 60 * 1000;  // 30 minutes in milliseconds

                while (calendar.getTimeInMillis() <= endTime) {
                    String timeSlot = new SimpleDateFormat("HHmm", Locale.US).format(calendar.getTime());
                     String dateSlot = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
                    String formattedDate = visitdate.replace("-", "");
                    // Construct the file name based on the time slot
                    String fileName = "readings_" + visitTime + "_" + formattedDate + ".csv";
                    Log.d("searching for", fileName);

                    File file = new File(downloadDirectory, fileName);

                    if (file.exists()) {
                        Uri uri = FileProvider.getUriForFile(
                                this,
                                getApplicationContext().getPackageName() + ".provider",
                                file
                        );

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "text/csv");


                        try {
                            Log.d("File not", "downloaded1");
                            boolean isFirstRow = true;

                            List<String[]> csvData = CsvParser.readCsvFile(file.getAbsolutePath());
                            if(csvData.size()>2) {

                                Log.d("csvdata", Arrays.toString(csvData.get(1)));
                                for (String[] rowData : csvData) {


                                   // Skip the first row
                                    try{


                                        float yValueFrequency = Float.parseFloat(rowData[1]);  // Assuming frequency data is at index 2
                                        float yValuePhase = Float.parseFloat(rowData[2]);  // Assuming phase data is at index 3
Log.d("line","iterate");
                                        frequencyEntry.add(new Entry(count, yValueFrequency));
                                        phaseEntry.add(new Entry(count, yValuePhase));
count=count+1;

                                }
                                    catch (Exception e)
                                    {}}

                                LineChart lineChart = findViewById(R.id.lineChart1);
                                lineChart.getDescription().setText("Timed Data Chart");
                                LineChart lineChart1 = findViewById(R.id.lineChart2);
                                lineChart1.getDescription().setText("Timed Data Chart");
                                //    YourXAxisFormatter.updateValues(frequency11, p1, p2);
                                // linechart.getXAxis().setValueFormatter(new YourXAxisFormatter());

                                ValueFormatter xFormatter = new ValueFormatter() {
                                    @Override
                                    public String getAxisLabel(float value, AxisBase axis) {
                                        // Format the Y-axis labels as needed
                                        return String.format("%.1f", value);
                                    }
                                };
                                lineChart.getXAxis().setLabelCount(3,true);
                                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                              //  lineChart1.getXAxis().setLabelCount(3,true);
                                lineChart.getXAxis().setValueFormatter(
                                        xFormatter);
                                lineChart1.getXAxis().setLabelCount(3,true);
                                lineChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                //  lineChart1.getXAxis().setLabelCount(3,true);
                                lineChart1.getXAxis().setValueFormatter(
                                        xFormatter);
                             //   linechart1.getXAxis().setValueFormatter(
                                        //xFormatter);
                                //linechart.getXAxis().set// Implement your X-axis formatting
                                // linechart.getAxisLeft().setAxisMinimum(150000f);
                                // linechart.getAxisLeft().setAxisMimum(150000f);
                                //  linechart.getAxisLeft().setAxisMinimum(400f);
                                lineChart.getAxisRight().setEnabled(false);
                                lineChart.getXAxis().setDrawGridLines(false);
                                lineChart1.getAxisRight().setEnabled(false);
                                lineChart1.getXAxis().setDrawGridLines(false);
                              //  lineChart1.setData(lineData1);
                               // linechart1.getDescription().setText("Timed Phase Chart");


                                //   linechart1.getAxisLeft().setAxisMinimum(-80f);

                          //      linechart1.getAxisRight().setEnabled(false);
                                // Create LineDataSet with the entry lists
                                LineDataSet dataSetFrequency = new LineDataSet(frequencyEntry, "Frequency Data");
                                dataSetFrequency.setColor(Color.BLUE);  // Set line color

                                LineDataSet dataSetPhase = new LineDataSet(phaseEntry, "Phase Data");
                                dataSetPhase.setColor(Color.RED);  // Set line color
                                lineData1 = new LineData(dataSetPhase);
                                Log.d("linedata", lineData.getDataSets().toString());
                                // Set data to the chart
                                lineChart1.setData(lineData1);
                                lineChart1.notifyDataSetChanged();
                                // Refresh the chart
                                lineChart1.invalidate();
                                // Create LineData object with our datasets
                                lineData = new LineData(dataSetFrequency);
                                Log.d("linedata", lineData.getDataSets().toString());
                                // Set data to the chart
                                lineChart.setData(lineData);
                                lineChart.notifyDataSetChanged();
                                // Refresh the chart
                                lineChart.invalidate();
                                adapter.setData(csvData);
                                adapter.notifyDataSetChanged();
                                break;  // Open the first available file and exit the loop
                            }   } catch (ActivityNotFoundException | IOException e) {
                            // Handle the case where there is no app to open CSV files
                            // You can show a message or provide an alternative action here
                            Log.d("File not", e.toString());
                        }
                    }
                    Log.d("File not", "downloaded5");
                    // Move to the next 30-minute interval
                    calendar.add(Calendar.MINUTE, 1);
                }

                // Handle the case where no file is found within the 30-minute intervals
                // You can show a message or provide an alternative action here
            } else {
                Toast.makeText(this, "No file downloaded for this recording", Toast.LENGTH_SHORT).show();
            }
        }
    }


        // Change "your_csv_file_path" to the actual path of your CSV file


            // Set up the adapter with the CSV data


        }
