package com.example.eegreader.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static List<String[]> readCsvFile(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            try {
                data = reader.readAll();
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }
        }

        return data;
    }
}
