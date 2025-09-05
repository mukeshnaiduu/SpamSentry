package com.spamsentry;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;

public class DataCleaner {
    public static void main(String[] args) throws Exception {
        String in = args.length > 0 ? args[0] : "data/emails.csv";
        String out = args.length > 1 ? args[1] : "data/emails_clean.csv";

        CSVReader r = new CSVReader(new FileReader(in));
        CSVWriter w = new CSVWriter(new FileWriter(out));

        String[] header = r.readNext(); // consume header if present
        // always write normalized header
        w.writeNext(new String[]{"text", "spam"});

        String[] row;
        int inCount = 0, outCount = 0, skipped = 0;
        while ((row = r.readNext()) != null) {
            inCount++;
            if (row.length < 2) { skipped++; continue; }
            String rawText = row[0] == null ? "" : row[0];
            String rawLabel = row[1] == null ? "0" : row[1];
            String cleaned = TextPreprocessor.normalize(rawText);
            if (cleaned.isEmpty()) { skipped++; continue; }
            String lbl = rawLabel.trim();
            String cls = (lbl.equals("1") || lbl.equalsIgnoreCase("spam") || lbl.equalsIgnoreCase("true")) ? "1" : "0";
            w.writeNext(new String[]{cleaned, cls});
            outCount++;
        }

        r.close();
        w.close();

        System.out.printf("Input rows: %d, Written rows: %d, Skipped: %d\n", inCount, outCount, skipped);
        System.out.println("Cleaned file written to: " + out);
    }
}
