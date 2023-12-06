package com.example.eegreader.database.NoSQL;
//Readings child of visit
public class Reading {
    private String readingId;
    private String frequency;
    private String phaseAngle;
    private String bioimpedance;



    public String getReadingId() {
        return readingId;
    }

    public void setReadingId(String readingId) {
        this.readingId = readingId;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPhaseAngle() {
        return phaseAngle;
    }

    public void setPhaseAngle(String phaseAngle) {
        this.phaseAngle = phaseAngle;
    }

    public String getBioimpedance() {
        return bioimpedance;
    }

    public void setBioimpedance(String bioimpedance) {
        this.bioimpedance = bioimpedance;
    }
}
