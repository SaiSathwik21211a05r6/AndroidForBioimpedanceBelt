package com.example.eegreader.Graph;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class YourXAxisFormatter extends ValueFormatter {
    private final long startTime; // The start time of your data

    private final double frequency11;
    private final int p1;
    private List<String> times;
    private final int p2;


    public YourXAxisFormatter(long startTime,double frequency11, int p1, int p2) {
        this.startTime = startTime;
this.p1=p1;
this.p2=p2;
this.frequency11=frequency11;
    }

    @Override
    public String getFormattedValue(float value) {
        // Calculate the time based on the current value
        long timeInMillis = startTime;

        // Format the time as "hh:mm" in your desired timezone (e.g., Locale.US)
        SimpleDateFormat sdf = new SimpleDateFormat("ss", Locale.US);
        Date date = new Date(timeInMillis);
        return sdf.format(date);//+","+frequency11+":"+"("+p1+","+p2+")";
    }
}



/**package com.example.eegreader.Graph;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class YourXAxisFormatter extends ValueFormatter {
    private double frequencyStart;
    private long time;
    private int p1;
    private int configurationcounter=0;
    private int p2;
    private int currentElectrode1 = 1;
    private int currentElectrode2 = 1;
    private String formattedTime;
    HashMap<Integer, int[]> myMap = new HashMap<>();
    public YourXAxisFormatter(long time,double frequencyStart, int p1, int p2) {
        this.frequencyStart = frequencyStart;
        long timeInMillis = this.time;

// Create a SimpleDateFormat object
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

// Format the time
        formattedTime = dateFormat.format(new Date(timeInMillis));
        this.p1 = p1;
        this.p2 = p2;
        int counter = 1;
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                if (i!=j) {
                    myMap.put(counter, new int[]{i, j});

                    counter++;
                }
            }
    }}

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int frequency = (int) (frequencyStart + value);
        return formatXLabel(frequency);
    }

    public void updateValues(double frequencyStart, int p1, int p2) {
        this.frequencyStart = frequencyStart;
        this.p1 = p1;
        this.p2 = p2;
    }

    private String formatXLabel(int frequency) {
        StringBuilder label = new StringBuilder();
        label.append(this.formattedTime).append(",").append(frequencyStart).append(": (");
        label.append(this.p1).append(",").append(this.p2).append(")");
        if(configurationcounter+1<=30) {
            configurationcounter = configurationcounter + 1;
          /**  this.p1 = myMap.get(configurationcounter)[0];
            this.p2 = myMap.get(configurationcounter)[1];*/
     /**   }
        else {
            this.frequencyStart++;
//Log.d("frequency 1", String.valueOf(frequency1));
            configurationcounter = 1;
          /**  this.p1 = myMap.get(configurationcounter)[0];
            this.p2 = myMap.get(configurationcounter)[1];*/

    /**    }
        // Update electrode combinations
        currentElectrode1++;
        if (currentElectrode2 > 6) {
            currentElectrode2 = 1;
            currentElectrode2++;
        }

        if (currentElectrode1 > 6) {
            currentElectrode1 = 1;
        }

        return label.toString();
    }

    @Override
    public String getFormattedValue(float value) {
        // This method doesn't need to be overridden
        return "";
    }*/

  /**  @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // This method doesn't need to be overridden
        return "";
    }*/

   /** @Override
    public String getFormattedValue(float value, AxisBase axis, ViewPortHandler viewPortHandler) {
        // This method doesn't need to be overridden
        return "";
    }*/
/**}
*/
