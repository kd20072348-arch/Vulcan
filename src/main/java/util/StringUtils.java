package util;

/** Small String helpers used by the analyzers. */
public class StringUtils {

    // A cell is missing if it is empty or says NA / null (case does not matter)
    public static boolean isMissing(String cell) {
        String c = cell.trim();
        return c.isEmpty() || c.equalsIgnoreCase("NA") || c.equalsIgnoreCase("null");
    }

    // True for numbers like 12, -3, 4.75
    public static boolean isNumber(String cell) {
        return cell.trim().matches("-?\\d+(\\.\\d+)?");
    }
}
