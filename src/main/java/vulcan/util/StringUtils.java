package vulcan.util;

import java.util.Set;

/**
 * String helper methods used by the parser and the analyzers.
 * (Review 2 - Strings: trim, toLowerCase, replaceAll, matches, StringBuilder, equalsIgnoreCase)
 */
public final class StringUtils {

    private static final Set<String> MISSING_TOKENS = Set.of("na", "n/a", "null", "nan", "none", "-", "?");

    private StringUtils() { }

    /** A cell is "missing" if it is null, blank, or a placeholder such as NA / null / NaN. */
    public static boolean isMissing(String s) {
        if (s == null) return true;
        String t = s.trim();
        return t.isEmpty() || MISSING_TOKENS.contains(t.toLowerCase());
    }

    /** True for plain integers and decimals such as 12, -3, 4.75. */
    public static boolean isNumeric(String s) {
        return s != null && s.trim().matches("-?\\d+(\\.\\d+)?");
    }

    /** "  Unit Price " -> "unit_price". Blank headers become column_N. */
    public static String normalizeHeader(String raw, int index) {
        String h = raw == null ? "" : raw.trim().toLowerCase().replaceAll("[\\s\\-]+", "_");
        return h.isEmpty() ? "column_" + (index + 1) : h;
    }

    /** Shortens long text for tables and chart labels. */
    public static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, Math.max(0, max - 1)) + "...";
    }

    /** Pads text on the right so report columns line up. */
    public static String padRight(String s, int width) {
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}
