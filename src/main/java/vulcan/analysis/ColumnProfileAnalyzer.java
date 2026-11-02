package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;
import vulcan.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/** Numeric columns get min / max / mean; text columns get distinct count and most common value. */
public class ColumnProfileAnalyzer implements Analyzer {
    public static final String NAME = "Column Profile";

    @Override public String name() { return NAME; }

    @Override
    public AnalysisResult analyze(Dataset d) {
        StringBuilder sb = new StringBuilder();
        int numericColumns = 0;

        for (int c = 0; c < d.getColumnCount(); c++) {
            int present = 0, numeric = 0;
            double min = Double.MAX_VALUE, max = -Double.MAX_VALUE, sum = 0;
            Map<String, Integer> freq = new HashMap<>();

            for (String[] row : d.getRows()) {
                String v = row[c];
                if (StringUtils.isMissing(v)) continue;
                present++;
                freq.merge(v, 1, Integer::sum);
                if (StringUtils.isNumeric(v)) {
                    double x = Double.parseDouble(v.trim());
                    numeric++;
                    sum += x;
                    min = Math.min(min, x);
                    max = Math.max(max, x);
                }
            }

            sb.append(d.getHeaders().get(c)).append('\n');
            if (present == 0) {
                sb.append("  (empty column)\n\n");
            } else if (numeric == present) {
                numericColumns++;
                sb.append(String.format("  numeric | min %.2f | max %.2f | mean %.2f%n%n", min, max, sum / numeric));
            } else {
                String top = "";
                int topCount = 0;
                for (Map.Entry<String, Integer> e : freq.entrySet()) {
                    if (e.getValue() > topCount) { top = e.getKey(); topCount = e.getValue(); }
                }
                sb.append("  text | ").append(freq.size()).append(" distinct | most common: \"")
                  .append(StringUtils.truncate(top, 30)).append("\" (").append(topCount).append("x)\n\n");
            }
        }
        return new AnalysisResult(NAME, sb.toString(), numericColumns);
    }
}
