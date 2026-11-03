package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Reads a CSV file into a list of rows. Each row is a String array. */
public class CsvReader {

    public static List<String[]> read(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine && line.startsWith("\uFEFF")) {
                    line = line.substring(1);              // remove the invisible BOM Excel adds
                }
                firstLine = false;
                if (line.trim().isEmpty()) {
                    continue;                              // skip blank lines
                }
                String[] cells = line.split(",", -1);      // split the line at every comma
                for (int i = 0; i < cells.length; i++) {
                    cells[i] = cells[i].trim();            // remove extra spaces
                }
                rows.add(cells);
            }
        }
        return rows;
    }
}
