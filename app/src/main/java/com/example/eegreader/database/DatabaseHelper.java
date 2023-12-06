/**package com.example.eegreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.eegreader.database.NoSQL.Reading;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReadingsDB";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "Readings";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PATIENT_ID = "PatientId";
    private static final String COLUMN_VISIT_DATE = "VisitDate";
    private static final String COLUMN_VISIT_TIME = "VisitTime";
    private static final String COLUMN_TIMESTAMP = "Timestamp";
    private static final String COLUMN_FREQUENCY = "Frequency";
    private static final String COLUMN_BIOIMPEDANCE = "Bioimpedance";
    private static final String COLUMN_PHASE = "Phase";
    private static final String COLUMN_ELECTRODE_COMBINATION_1 = "ElectrodeCombination1";
    private static final String COLUMN_ELECTRODE_COMBINATION_2 = "ElectrodeCombination2";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +

                COLUMN_PATIENT_ID + " INTEGER, " +
                COLUMN_VISIT_DATE + " TEXT, " +
                COLUMN_VISIT_TIME + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_FREQUENCY + " REAL, " +
                COLUMN_BIOIMPEDANCE + " REAL, " +
                COLUMN_PHASE + " REAL, " +
                COLUMN_ELECTRODE_COMBINATION_1 + " INTEGER, " +
                COLUMN_ELECTRODE_COMBINATION_2 + " INTEGER"+
                  ")";


        db.execSQL(createTableQuery);
    }
    // In DatabaseHelper class
    public List<Reading> getAllReadings() {
        List<Reading> readings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Reading1 reading = new Reading1();
                // Populate the Reading object with values from the cursor
                reading.setPatientId(cursor.getInt(cursor.getColumnIndex(COLUMN_PATIENT_ID)));
                reading.setVisitDate(cursor.getString(cursor.getColumnIndex(COLUMN_VISIT_DATE)));
                reading.setVisitTime(cursor.getString(cursor.getColumnIndex(COLUMN_VISIT_TIME)));
                reading.setTimeStamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                reading.setBioimpedance(cursor.getString(cursor.getColumnIndex(COLUMN_BIOIMPEDANCE)));
                reading.setVisitTime(cursor.getString(cursor.getColumnIndex(COLUMN_PHASE)));
                reading.setVisitTime(cursor.getString(cursor.getColumnIndex(COLUMN_VISIT_TIME)));
                reading.setVisitTime(cursor.getString(cursor.getColumnIndex(COLUMN_VISIT_TIME)));
                reading.setVisitTime(cursor.getString(cursor.getColumnIndex(COLUMN_VISIT_TIME)));
                // ... populate other fields

                readings.add(reading);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return readings;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertReading(int patientId, String visitDate, String visitTime, String timestamp,
                              double frequency, double bioimpedance, double phase,
                              int electrodeCombination1, int electrodeCombination2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATIENT_ID, patientId);
        values.put(COLUMN_VISIT_DATE, visitDate);
        values.put(COLUMN_VISIT_TIME, visitTime);
        values.put(COLUMN_TIMESTAMP, timestamp);
        values.put(COLUMN_FREQUENCY, frequency);
        values.put(COLUMN_BIOIMPEDANCE, bioimpedance);
        values.put(COLUMN_PHASE, phase);
        values.put(COLUMN_ELECTRODE_COMBINATION_1, electrodeCombination1);
        values.put(COLUMN_ELECTRODE_COMBINATION_2, electrodeCombination2);
        db.insert(TABLE_NAME, null, values);
        Log.d("InsertData", "PatientID: " + patientId + ", VisitDate: " + visitDate + ", VisitTime: " + visitTime + ", Timestamp: " + timestamp + " ...");

        db.close();
    }

}
*/