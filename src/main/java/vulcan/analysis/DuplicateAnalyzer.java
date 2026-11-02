package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateAnalyzer implements Analyzer {
    public static final String NAME = "Duplicate Rows";

    @Override public String name() { return NAME; }

    @Override
    public AnalysisResult analyze(Dataset d) {
        Set<String> seen = new HashSet<>();
        List<Integer> duplicateRows = new ArrayList<>();
        int rowNo = 1;
        for (String[] row : d.getRows()) {
            String key = String.join("\u0001", row).toLowerCase();   // case-insensitive comparison
            if (!seen.add(key)) duplicateRows.add(rowNo);
            rowNo++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate rows found: ").append(duplicateRows.size()).append("\n");
        if (!duplicateRows.isEmpty()) {
            sb.append("Row numbers (data rows, 1-based): ");
            for (int i = 0; i < Math.min(15, duplicateRows.size()); i++) {
                if (i > 0) sb.append(", ");
                sb.append(duplicateRows.get(i));
            }
            if (duplicateRows.size() > 15) sb.append(" ... and ").append(duplicateRows.size() - 15).append(" more");
            sb.append('\n');
        }
        return new AnalysisResult(NAME, sb.toString(), duplicateRows.size());
    }
}
