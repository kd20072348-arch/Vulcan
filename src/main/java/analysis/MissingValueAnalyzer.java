package analysis;

import util.StringUtils;

import java.util.List;

public class MissingValueAnalyzer implements Analyzer {

    @Override
    public String getName() {
        return "Missing Values";
    }

    @Override
    public String analyze(String[] headers, List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < headers.length; col++) {
            sb.append(headers[col]).append(": ")
              .append(countColumn(col, rows)).append(" missing\n");
        }
        sb.append("Total missing cells: ").append(countMissing(headers, rows));
        return sb.toString();
    }

    // Also used by the "Missing Values" card on the dashboard
    public int countMissing(String[] headers, List<String[]> rows) {
        int total = 0;
        for (int col = 0; col < headers.length; col++) {
            total += countColumn(col, rows);
        }
        return total;
    }

    private int countColumn(int col, List<String[]> rows) {
        int count = 0;
        for (String[] row : rows) {
            if (col >= row.length || StringUtils.isMissing(row[col])) {
                count++;
            }
        }
        return count;
    }
}
