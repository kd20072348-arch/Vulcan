package vulcan.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of DatasetRepository using JDBC.
 * Credentials come from environment variables so no password is committed to GitHub:
 *   VULCAN_DB_URL (default jdbc:mysql://localhost:3306/csv_analyzer), VULCAN_DB_USER (root), VULCAN_DB_PASS
 * Methods are synchronized because several worker threads may use the same instance.
 */
public class DatabaseManager implements DatasetRepository {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager() {
        this(env("VULCAN_DB_URL", "jdbc:mysql://localhost:3306/csv_analyzer"),
             env("VULCAN_DB_USER", "root"),
             env("VULCAN_DB_PASS", ""));
    }

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private static String env(String key, String fallback) {
        String v = System.getenv(key);
        return v == null || v.isBlank() ? fallback : v;
    }

    private Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(url, user, password);
        try (Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS dataset_analysis ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "file_name VARCHAR(255) NOT NULL,"
                    + "row_count INT, column_count INT,"
                    + "missing_count INT, duplicate_count INT,"
                    + "analysed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        }
        return c;
    }

    @Override
    public synchronized void saveSummary(String fileName, int rows, int columns, int missing, int duplicates)
            throws SQLException {
        String sql = "INSERT INTO dataset_analysis (file_name, row_count, column_count, missing_count, duplicate_count) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fileName);
            ps.setInt(2, rows);
            ps.setInt(3, columns);
            ps.setInt(4, missing);
            ps.setInt(5, duplicates);
            ps.executeUpdate();
        }
    }

    @Override
    public synchronized List<String[]> loadHistory() throws SQLException {
        List<String[]> out = new ArrayList<>();
        String sql = "SELECT id, file_name, row_count, column_count, missing_count, duplicate_count, analysed_at "
                   + "FROM dataset_analysis ORDER BY id DESC";
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                out.add(new String[]{
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7)});
            }
        }
        return out;
    }
}
