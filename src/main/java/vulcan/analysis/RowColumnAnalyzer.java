package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;

public class RowColumnAnalyzer implements Analyzer {
    public static final String NAME = "Rows & Columns";

    @Override public String name() { return NAME; }

    @Override
    public AnalysisResult analyze(Dataset d) {
        StringBuilder sb = new StringBuilder();
        sb.append("File: ").append(d.getName()).append('\n');
        sb.append("Rows: ").append(d.getRowCount()).append('\n');
        sb.append("Columns: ").append(d.getColumnCount()).append("\n\n");
        for (int i = 0; i < d.getColumnCount(); i++) {
            sb.append(String.format("  %2d. %s%n", i + 1, d.getHeaders().get(i)));
        }
        return new AnalysisResult(NAME, sb.toString(), d.getRowCount());
    }
}
