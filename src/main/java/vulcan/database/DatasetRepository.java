package vulcan.database;

import java.sql.SQLException;
import java.util.List;

/** Interface #3: storage abstraction. DatabaseManager (MySQL/JDBC) is one implementation. */
public interface DatasetRepository {
    void saveSummary(String fileName, int rows, int columns, int missing, int duplicates) throws SQLException;

    /** Each entry: id, file name, rows, columns, missing, duplicates, analysed-at. */
    List<String[]> loadHistory() throws SQLException;
}
