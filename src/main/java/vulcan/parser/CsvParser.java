package vulcan.parser;

import vulcan.model.Dataset;
import vulcan.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Hand-written CSV parser (no libraries) built on String / StringBuilder operations.
 * Supports quoted fields, commas inside quotes, and "" as an escaped quote.
 */
public final class CsvParser {

    private CsvParser() { }

    public static Dataset parse(File file, ProgressListener listener) throws IOException {
        long totalBytes = Math.max(1, file.length());
        long readBytes = 0;
        int lineNo = 0;
        List<String> headers = null;
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                readBytes += line.length() + 1;
                lineNo++;
                if (lineNo == 1 && line.startsWith("\uFEFF")) line = line.substring(1); // strip BOM
                if (line.trim().isEmpty()) continue;

                List<String> fields = splitLine(line);
                if (headers == null) {
                    headers = new ArrayList<>();
                    for (int i = 0; i < fields.size(); i++) {
                        headers.add(StringUtils.normalizeHeader(fields.get(i), i));
                    }
                } else {
                    String[] row = new String[headers.size()];
                    for (int i = 0; i < row.length; i++) {
                        row[i] = i < fields.size() ? fields.get(i) : "";   // pad short rows
                    }
                    rows.add(row);                                          // extra fields are dropped
                }
                if (lineNo % 500 == 0) {
                    int pct = (int) Math.min(99, readBytes * 100 / totalBytes);
                    listener.onProgress(pct, "Parsing " + file.getName() + " ... " + rows.size() + " rows");
                }
            }
        }
        if (headers == null) throw new IOException("The file is empty.");
        listener.onProgress(100, "Loaded " + rows.size() + " rows from " + file.getName());
        return new Dataset(file.getName(), headers, rows);
    }

    /** Splits one CSV line into trimmed fields. */
    public static List<String> splitLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else {
                    cur.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString().trim());
        return fields;
    }
}
