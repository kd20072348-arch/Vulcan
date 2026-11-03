package analysis;

import java.util.List;

/** The interface: every analysis feature must provide a name and an analyze method. */
public interface Analyzer {
    String getName();
    String analyze(String[] headers, List<String[]> rows);
}
