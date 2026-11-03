package analysis;

import java.util.List;

public class RowColumnAnalyzer implements Analyzer {

    @Override
    public String getName() {
        return "Rows and Columns";
    }

    @Override
    public String analyze(String[] headers, List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rows: ").append(rows.size()).append("\n");
        sb.append("Columns: ").append(headers.length).append("\n");
        sb.append("Column names: ").append(String.join(", ", headers));
        return sb.toString();
    }
}
