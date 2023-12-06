package com.example.eegreader.Graph;

import java.util.List;
//creating data points for plotting graphs
public class CustomDataPoint {
    private long timeElapsed;
    private List<Double> rzMagValues;
    private List<Double> rzPhaseValues;

    public CustomDataPoint(long timeElapsed, List<Double> rzMagValues, List<Double> rzPhaseValues) {
        this.timeElapsed = timeElapsed;
        this.rzMagValues = rzMagValues;
        this.rzPhaseValues = rzPhaseValues;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public List<Double> getRzMagValues() {
        return rzMagValues;
    }

    public List<Double> getRzPhaseValues() {
        return rzPhaseValues;
    }
}
