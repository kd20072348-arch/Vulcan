package vulcan.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Output of one Analyzer: a text summary, one headline number, and optional chart data. */
public final class AnalysisResult {
    private final String name;
    private final String summary;
    private final int headline;
    private final Map<String, Integer> chartData;

    public AnalysisResult(String name, String summary, int headline, Map<String, Integer> chartData) {
        this.name = name;
        this.summary = summary;
        this.headline = headline;
        this.chartData = Collections.unmodifiableMap(new LinkedHashMap<>(chartData));
    }

    public AnalysisResult(String name, String summary, int headline) {
        this(name, summary, headline, Map.of());
    }

    public String getName() { return name; }
    public String getSummary() { return summary; }
    public int getHeadline() { return headline; }
    public Map<String, Integer> getChartData() { return chartData; }
}
