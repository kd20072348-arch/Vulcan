package analysis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateAnalyzer implements Analyzer {

    @Override
    public String getName() {
        return "Duplicate Rows";
    }

    @Override
    public String analyze(String[] headers, List<String[]> rows) {
        Set<String> seen = new HashSet<>();
        int duplicates = 0;
        for (String[] row : rows) {
            String key = String.join("|", row).toLowerCase();  // turn the row into one String
            if (!seen.add(key)) {                              // add() returns false if already there
                duplicates++;
            }
        }
        return "Duplicate rows found: " + duplicates;
    }
}
