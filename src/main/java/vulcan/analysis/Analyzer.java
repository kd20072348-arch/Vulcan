package vulcan.analysis;

import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;

/**
 * Interface #2: every analysis feature implements this contract.
 * Implementations must be stateless / read-only so they can run in parallel threads.
 */
public interface Analyzer {
    String name();
    AnalysisResult analyze(Dataset data);
}
