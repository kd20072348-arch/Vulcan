package vulcan.model;

import java.util.Collections;
import java.util.List;

/** Immutable holder for a parsed CSV file. Immutability makes it safe to share between threads. */
public final class Dataset {
    private final String name;
    private final List<String> headers;
    private final List<String[]> rows;

    public Dataset(String name, List<String> headers, List<String[]> rows) {
        this.name = name;
        this.headers = Collections.unmodifiableList(headers);
        this.rows = Collections.unmodifiableList(rows);
    }

    public String getName() { return name; }
    public List<String> getHeaders() { return headers; }
    public List<String[]> getRows() { return rows; }
    public int getRowCount() { return rows.size(); }
    public int getColumnCount() { return headers.size(); }
}
