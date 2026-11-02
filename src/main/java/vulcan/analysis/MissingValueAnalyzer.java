package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;
import vulcan.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MissingValueAnalyzer implements Analyzer {
    public static final String NAME = "Missing Values";

    @Override public String name() { return NAME; }

    @Override
    public AnalysisResult analyze(Dataset d) {
        Map<String, Integer> perColumn = new LinkedHashMap<>();
        for (String h : d.getHeaders()) perColumn.put(h, 0);

        int total = 0;
        for (String[] row : d.getRows()) {
            for (int c = 0; c < row.length; c++) {
                if (StringUtils.isMissing(row[c])) {
                    perColumn.merge(d.getHeaders().get(c), 1, Integer::sum);
                    total++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Total missing cells: ").append(total).append("\n\n");
        for (Map.Entry<String, Integer> e : perColumn.entrySet()) {
            double pct = d.getRowCount() == 0 ? 0 : 100.0 * e.getValue() / d.getRowCount();
            sb.append("  ").append(StringUtils.padRight(e.getKey(), 20))
              .append(String.format("%5d  (%.1f%%)%n", e.getValue(), pct));
        }
        return new AnalysisResult(NAME, sb.toString(), total, perColumn);
    }
}
