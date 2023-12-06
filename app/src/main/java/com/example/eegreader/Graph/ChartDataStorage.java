/**package com.example.eegreader.Graph;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class ChartDataStorage {

    private static final String PREF_NAME = "chart_data_pref";
    private static final String KEY_CHART_DATA = "chart_data";

    // Save chart data to SharedPreferences
    public static void saveChartDataToStorage(Context context, LineData lineData) {
        if (lineData == null) {
            return;
        }

        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LineData.class, new LineDataSetDeserializer())
                    .serializeSpecialFloatingPointValues()
                    .create();

            // Convert LineData to JSON string
            String json = gson.toJson(lineData);

            // Log the JSON string before writing it to the file
            Log.d("ChartDataStorage", "JSON string before writing to file: " + json);

            // Get the Downloads directory
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            // Create a file in the Downloads directory
            File file = new File(downloadsDir, "chart_data.json");

            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();

            // Save the file path to SharedPreferences for future retrieval
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CHART_DATA, file.getAbsolutePath());
            editor.apply();

            // Save the chart image to the Downloads directory
            // Chart.saveToGallery(file.getName(), "Chart Image", "", Chart.SaveToGalleryFormat.PNG, 85);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            Log.e("ChartDataStorage", "Error saving chart data: " + e.getMessage());
        }
    }

    // Retrieve chart data from SharedPreferences
    public static LineData retrieveChartDataFromStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Retrieve file path from SharedPreferences
        String filePath = sharedPreferences.getString(KEY_CHART_DATA, null);

        if (filePath != null) {
            try {
                Log.d("found", "chart");
                // Read JSON data from the file
                String jsonData = FileUtils.readFileToString(new File(filePath), "UTF-8");
                Log.d("json is", jsonData);
                // Convert JSON data back to LineData using Gson
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LineData.class, new LineDataSetDeserializer())
                        .serializeSpecialFloatingPointValues()
                        .create();
//                Log.d("final json is", gson.fromJson(jsonData, LineData.class).toString());
                return gson.fromJson(jsonData, LineData.class);


            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception appropriately
                Log.e("ChartDataStorage", "Error reading chart data: " + e.getMessage());
            }
        }

        return null;
    }

    public static class LineDataSetDeserializer implements JsonDeserializer<LineData> {
        // Define the list of properties to handle
        private final String[] SPECIAL_PROPERTIES = {"mFormLineWidth", "mFormSize"};

        @Override
        public LineData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Check and handle special floating-point values for each property
            for (String propertyName : SPECIAL_PROPERTIES) {
                handleSpecialFloatingPointValues(jsonObject);
            }

            // Deserialize the LineDataSet with the modified JSON
            return new Gson().fromJson(jsonObject, LineData.class);
        }

        public class ILineDataSetInstanceCreator implements InstanceCreator<ILineDataSet> {
            @Override
            public ILineDataSet createInstance(Type type) {
                // You need to provide an implementation of ILineDataSet
                // Here, I'm assuming LineDataSet is an implementation of ILineDataSet
                return new LineDataSet(); // Adjust this line based on your actual implementation
            }
        }
            private void handleSpecialFloatingPointValues(JsonObject jsonObject){
                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    String propertyName1 = entry.getKey();
                    JsonElement propertyElement = entry.getValue();

                    if (propertyElement.isJsonPrimitive() && propertyElement.getAsJsonPrimitive().isString()) {
                        String propertyValue = propertyElement.getAsString();
                        if (isSpecialFloatingPointValue(propertyValue)) {
                            // Handle special floating-point values by replacing with a default value
                            jsonObject.addProperty(propertyName1, 0.0);
                        }

                    }}}}
                    private static boolean isSpecialFloatingPointValue (String value) {
                        return value.equals("NaN") || value.equals("Infinity") || value.equals("-Infinity");

                }



    private ILineDataSet deserializeLineDataSet(JsonElement datasetJson) {
            if (datasetJson.isJsonObject()) {
                JsonObject datasetObject = datasetJson.getAsJsonObject();
                Log.d("dtype", String.valueOf(datasetJson.getAsJsonObject()));
        //        return new Gson().fromJson(datasetJson, LineDataSet.class);
                // Check for a specific field that indicates the type of the dataset
                if (datasetObject.has("datasetType") && datasetObject.get("datasetType").isJsonPrimitive()) {
                        String datasetType = datasetObject.get("datasetType").getAsString();
                 Log.d("dtype", String.valueOf(datasetJson.getAsJsonObject().get("mDataSets")));
                    // Based on the datasetType, instantiate athe appropriate implementation of ILineDataSet
                    switch (datasetType) {
                        case "LineDataSet":
                            return new Gson().fromJson(datasetJson, LineDataSet.class);
                        case "AnotherCustomDataSet":
                            //return new Gson().fromJson(datasetJson, AnotherCustomDataSet.class);
                        // Add more cases as needed for different types of datasets
                        default:
                            // Handle unknown dataset types or throw an exception
                            throw new JsonParseException("Unknown datasetType: " + datasetType);
                    }
                } else {
                    // Handle the case where the datasetType field is missing or not a primitive value
                    return new Gson().fromJson(datasetJson, LineDataSet.class);
                }
            } else {
                // Handle the case where the datasetJson is not a JsonObject
                throw new JsonParseException("Invalid dataset JSON structure");
            }
        }

    }
/**
    public class IFillFormatterDeserializer implements JsonDeserializer<IFillFormatter> {

        @Override
        public IFillFormatter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            Log.d("deser","Exception");
            // Simply return null to ignore deserialization of IFillFormatter
            return null;
        }
    }
}
*/