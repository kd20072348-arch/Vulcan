package vulcan.view;

import vulcan.analysis.AnalysisEngine;
import vulcan.analysis.ColumnProfileAnalyzer;
import vulcan.analysis.DuplicateAnalyzer;
import vulcan.analysis.MissingValueAnalyzer;
import vulcan.analysis.RowColumnAnalyzer;
import vulcan.database.DatasetRepository;
import vulcan.model.AnalysisResult;
import vulcan.model.Dataset;
import vulcan.parser.CsvParser;
import vulcan.parser.ProgressListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Main window. Implements ProgressListener (interface) so background threads can report progress.
 * Threads used: "csv-loader", "analysis-coordinator" (+ pool workers), "db-saver", "db-loader".
 */
public class MainFrame extends JFrame implements ProgressListener {
    private static final int PREVIEW_ROWS = 500;

    private final DatasetRepository repository;
    private final AnalysisEngine engine = new AnalysisEngine(List.of(
            new RowColumnAnalyzer(), new MissingValueAnalyzer(),
            new DuplicateAnalyzer(), new ColumnProfileAnalyzer()));

    private Dataset dataset;
    private List<AnalysisResult> results;

    private final JLabel rowsValue = new JLabel("-");
    private final JLabel colsValue = new JLabel("-");
    private final JLabel missingValue = new JLabel("-");
    private final JLabel dupValue = new JLabel("-");
    private final JTable previewTable = new JTable();
    private final JTable historyTable = new JTable();
    private final JTextArea reportArea = new JTextArea();
    private final BarChartPanel chart = new BarChartPanel();
    private final JProgressBar progress = new JProgressBar(0, 100);
    private final JLabel status = new JLabel("Open a CSV file to begin.");
    private final JButton openBtn = new JButton("Open CSV");
    private final JButton analyzeBtn = new JButton("Run Analysis");
    private final JButton saveBtn = new JButton("Save to Database");
    private final JButton historyBtn = new JButton("Load History");

    public MainFrame(DatasetRepository repository) {
        super("Vulcan - CSV Analyzer");
        this.repository = repository;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        openBtn.addActionListener(e -> openCsv());
        analyzeBtn.addActionListener(e -> runAnalysis());
        saveBtn.addActionListener(e -> saveToDatabase());
        historyBtn.addActionListener(e -> loadHistory());
        updateButtons(false);
    }

    // ---------------------------------------------------------------- layout

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(0x1F2A37));
        side.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        side.setPreferredSize(new Dimension(190, 0));

        JLabel logo = new JLabel("VULCAN");
        logo.setForeground(Color.WHITE);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 22f));
        logo.setAlignmentX(LEFT_ALIGNMENT);
        side.add(logo);
        side.add(Box.createVerticalStrut(24));

        for (JButton b : new JButton[]{openBtn, analyzeBtn, saveBtn, historyBtn}) {
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            side.add(b);
            side.add(Box.createVerticalStrut(10));
        }
        return side;
    }

    private JPanel buildCenter() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 0));
        cards.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        cards.add(card("Rows", rowsValue));
        cards.add(card("Columns", colsValue));
        cards.add(card("Missing cells", missingValue));
        cards.add(card("Duplicate rows", dupValue));

        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        previewTable.setEnabled(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Preview", new JScrollPane(previewTable));
        tabs.addTab("Report", new JScrollPane(reportArea));
        tabs.addTab("Chart", chart);
        tabs.addTab("Database History", new JScrollPane(historyTable));

        JPanel center = new JPanel(new BorderLayout());
        center.add(cards, BorderLayout.NORTH);
        center.add(tabs, BorderLayout.CENTER);
        return center;
    }

    private JPanel card(String title, JLabel value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD0D7DE)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        JLabel t = new JLabel(title);
        t.setForeground(Color.GRAY);
        value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
        p.add(t, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        progress.setPreferredSize(new Dimension(220, 18));
        bar.add(status, BorderLayout.CENTER);
        bar.add(progress, BorderLayout.EAST);
        return bar;
    }

    // ---------------------------------------------------------------- ProgressListener

    /** Called from worker threads, so the UI update is posted to the Swing (event dispatch) thread. */
    @Override
    public void onProgress(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            progress.setValue(percent);
            status.setText(message);
        });
    }

    // ---------------------------------------------------------------- actions (each runs on its own thread)

    private void openCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();

        updateButtons(true);
        Thread loader = new Thread(() -> {
            try {
                Dataset d = CsvParser.parse(file, this);
                SwingUtilities.invokeLater(() -> showDataset(d));
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> showError("Could not read file", ex));
            } finally {
                SwingUtilities.invokeLater(() -> updateButtons(false));
            }
        }, "csv-loader");
        loader.start();
    }

    private void runAnalysis() {
        if (dataset == null) return;
        Dataset snapshot = dataset;
        updateButtons(true);
        Thread coordinator = new Thread(() -> {
            try {
                List<AnalysisResult> r = engine.runAll(snapshot, this);
                SwingUtilities.invokeLater(() -> showResults(r));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                SwingUtilities.invokeLater(() -> updateButtons(false));
            }
        }, "analysis-coordinator");
        coordinator.start();
    }

    private void saveToDatabase() {
        if (dataset == null || results == null) return;
        Dataset d = dataset;
        int missing = headline(MissingValueAnalyzer.NAME);
        int dups = headline(DuplicateAnalyzer.NAME);
        updateButtons(true);
        new Thread(() -> {
            try {
                repository.saveSummary(d.getName(), d.getRowCount(), d.getColumnCount(), missing, dups);
                onProgress(100, "Saved summary of " + d.getName() + " to MySQL.");
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> showError("Database error", ex));
            } finally {
                SwingUtilities.invokeLater(() -> updateButtons(false));
            }
        }, "db-saver").start();
    }

    private void loadHistory() {
        updateButtons(true);
        new Thread(() -> {
            try {
                List<String[]> rows = repository.loadHistory();
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel m = readOnlyModel(new Object[]{
                            "ID", "File", "Rows", "Columns", "Missing", "Duplicates", "Analysed at"});
                    rows.forEach(m::addRow);
                    historyTable.setModel(m);
                    status.setText("Loaded " + rows.size() + " saved analyses.");
                });
            } catch (SQLException ex) {
                SwingUtilities.invokeLater(() -> showError("Database error", ex));
            } finally {
                SwingUtilities.invokeLater(() -> updateButtons(false));
            }
        }, "db-loader").start();
    }

    // ---------------------------------------------------------------- UI updates (always on the Swing thread)

    private void showDataset(Dataset d) {
        dataset = d;
        results = null;
        DefaultTableModel m = readOnlyModel(d.getHeaders().toArray());
        int shown = Math.min(PREVIEW_ROWS, d.getRowCount());
        for (int i = 0; i < shown; i++) m.addRow(d.getRows().get(i));
        previewTable.setModel(m);
        rowsValue.setText(String.valueOf(d.getRowCount()));
        colsValue.setText(String.valueOf(d.getColumnCount()));
        missingValue.setText("-");
        dupValue.setText("-");
        reportArea.setText("");
        chart.setData("", java.util.Map.of());
        status.setText("Loaded " + d.getName() + " (showing first " + shown + " rows). Click Run Analysis.");
    }

    private void showResults(List<AnalysisResult> r) {
        results = r;
        StringBuilder sb = new StringBuilder();
        for (AnalysisResult res : r) {
            sb.append("===== ").append(res.getName().toUpperCase()).append(" =====\n")
              .append(res.getSummary()).append("\n");
        }
        reportArea.setText(sb.toString());
        reportArea.setCaretPosition(0);
        missingValue.setText(String.valueOf(headline(MissingValueAnalyzer.NAME)));
        dupValue.setText(String.valueOf(headline(DuplicateAnalyzer.NAME)));
        for (AnalysisResult res : r) {
            if (res.getName().equals(MissingValueAnalyzer.NAME)) {
                chart.setData("Missing values per column", res.getChartData());
            }
        }
        status.setText("Analysis complete.");
    }

    private int headline(String analyzerName) {
        if (results == null) return 0;
        for (AnalysisResult r : results) {
            if (r.getName().equals(analyzerName)) return r.getHeadline();
        }
        return 0;
    }

    private void updateButtons(boolean busy) {
        openBtn.setEnabled(!busy);
        historyBtn.setEnabled(!busy);
        analyzeBtn.setEnabled(!busy && dataset != null);
        saveBtn.setEnabled(!busy && results != null);
        if (busy) progress.setValue(0);
    }

    private void showError(String title, Exception ex) {
        status.setText(title + ": " + ex.getMessage());
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    private static DefaultTableModel readOnlyModel(Object[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
    }
}
