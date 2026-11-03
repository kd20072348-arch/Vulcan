package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import analysis.Analyzer;
import analysis.DuplicateAnalyzer;
import analysis.MissingValueAnalyzer;
import analysis.RowColumnAnalyzer;
import util.CsvReader;

public class MainFrame extends JFrame {

    // Main colors
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color SIDEBAR = new Color(31, 41, 55);
    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(31, 41, 55);
    private final Color SECONDARY_TEXT = new Color(107, 114, 128);

    private JLabel rowValue;
    private JLabel columnValue;
    private JLabel missingValue;

    private JTable dataTable;

    // ---- Review 2: data loaded from the CSV file + the analyzers ----
    private String[] headers;
    private List<String[]> rows;
    private JButton analyzeButton;

    private final MissingValueAnalyzer missingAnalyzer = new MissingValueAnalyzer();

    // Interface in use: every item is a different class, but all are Analyzers
    private final Analyzer[] analyzers = {
            new RowColumnAnalyzer(),
            missingAnalyzer,
            new DuplicateAnalyzer()
    };

    public MainFrame() {

        setTitle("CSV Analyzer");
        setSize(1200, 750);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        createHeader();
        createSidebar();
        createMainContent();

        setVisible(true);
    }

    // ==============================
    // HEADER
    // ==============================

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setPreferredSize(new Dimension(1200, 75));
        header.setBackground(WHITE);

        header.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0,
                        new Color(220, 224, 230)
                )
        );

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(
                titlePanel,
                BoxLayout.Y_AXIS
        ));

        titlePanel.setBackground(WHITE);
        titlePanel.setBorder(
                new EmptyBorder(10, 25, 10, 10)
        );

        JLabel title = new JLabel("CSV ANALYZER");

        title.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        22)
        );

        title.setForeground(TEXT);

        JLabel subtitle =
                new JLabel("Data Analysis & Management");

        subtitle.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        12)
        );

        subtitle.setForeground(SECONDARY_TEXT);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        // Database status

        JPanel statusPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        20,
                        25
                )
        );

        statusPanel.setBackground(WHITE);

        JLabel status = new JLabel(
                "●  Database Connected"
        );

        status.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        13)
        );

        status.setForeground(
                new Color(22, 163, 74)
        );

        statusPanel.add(status);

        header.add(
                statusPanel,
                BorderLayout.EAST
        );

        add(header, BorderLayout.NORTH);
    }

    // ==============================
    // SIDEBAR
    // ==============================

    private void createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(
                new Dimension(210, 0)
        );

        sidebar.setBackground(SIDEBAR);

        sidebar.setLayout(
                new BoxLayout(
                        sidebar,
                        BoxLayout.Y_AXIS
                )
        );

        sidebar.setBorder(
                new EmptyBorder(25, 15, 20, 15)
        );

        JLabel menuTitle =
                new JLabel("MENU");

        menuTitle.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        11)
        );

        menuTitle.setForeground(
                new Color(156, 163, 175)
        );

        menuTitle.setBorder(
                new EmptyBorder(0, 10, 15, 0)
        );

        sidebar.add(menuTitle);

        JButton importMenuButton =
                createMenuButton("📁   Import CSV");

        importMenuButton.addActionListener(
                e -> openCSVFile()
        );

        sidebar.add(importMenuButton);

        sidebar.add(createMenuButton(
                "📋   Data Preview"
        ));

        JButton analysisMenuButton =
                createMenuButton("📊   Analysis");

        analysisMenuButton.addActionListener(
                e -> runAnalysis()
        );

        sidebar.add(analysisMenuButton);

        sidebar.add(createMenuButton(
                "📈   Charts"
        ));

        JButton databaseButton =
        createMenuButton("🗄   Database");

        databaseButton.addActionListener(
                e -> showDatabaseRecords()
        );

sidebar.add(databaseButton);

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createMenuButton(
                "⚙   Settings"
        ));

        add(sidebar, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        45
                )
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setForeground(
                new Color(229, 231, 235)
        );

        button.setBackground(SIDEBAR);

        button.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        14)
        );

        button.setBorder(
                new EmptyBorder(10, 12, 10, 10)
        );

        button.setFocusPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return button;
        
    }

    // ==============================
    // MAIN CONTENT
    // ==============================

    private void createMainContent() {

        JPanel mainPanel = new JPanel(
                new BorderLayout()
        );

        mainPanel.setBackground(BACKGROUND);

        mainPanel.setBorder(
                new EmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );

        JPanel content = new JPanel();

        content.setBackground(BACKGROUND);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel welcome =
                new JLabel(
                        "Welcome to CSV Analyzer"
                );

        welcome.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        25)
        );

        welcome.setForeground(TEXT);

        welcome.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(welcome);

        JLabel description =
                new JLabel(
                        "Import a CSV file to begin analyzing your data."
                );

        description.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        14)
        );

        description.setForeground(
                SECONDARY_TEXT
        );

        description.setBorder(
                new EmptyBorder(5, 0, 20, 0)
        );

        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(description);

        // Statistics cards

        JPanel cards = new JPanel(
                new GridLayout(
                        1,
                        3,
                        15,
                        0
                )
        );

        cards.setBackground(BACKGROUND);

        cards.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        110
                )
        );

        cards.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rowValue = new JLabel("0");

        columnValue = new JLabel("0");

        missingValue = new JLabel("0");

        cards.add(createCard(
                "ROWS",
                rowValue
        ));

        cards.add(createCard(
                "COLUMNS",
                columnValue
        ));

        cards.add(createCard(
                "MISSING VALUES",
                missingValue
        ));

        content.add(cards);

        content.add(
                Box.createVerticalStrut(25)
        );

        // Dataset preview title

        JLabel previewTitle =
                new JLabel("Dataset Preview");

        previewTitle.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        18)
        );

        previewTitle.setForeground(TEXT);

        previewTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(previewTitle);

        content.add(
                Box.createVerticalStrut(10)
        );

        // Table

        String[] columns = {
                "Column 1",
                "Column 2",
                "Column 3"
        };

        Object[][] data = {
                {
                        "No dataset",
                        "loaded",
                        "yet"
                }
        };

        dataTable = new JTable(
                data,
                columns
        );

        dataTable.setRowHeight(35);

        dataTable.setFont(
                new Font("Segoe UI",
                        Font.PLAIN,
                        13)
        );

        dataTable.getTableHeader()
                .setFont(
                        new Font("Segoe UI",
                                Font.BOLD,
                                13)
                );

        JScrollPane scrollPane =
                new JScrollPane(dataTable);

        scrollPane.setPreferredSize(
                new Dimension(800, 220)
        );

        scrollPane.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        content.add(scrollPane);

        content.add(
                Box.createVerticalStrut(20)
        );

        // Buttons

        JPanel actionPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.LEFT,
                        10,
                        0
                )
        );

        actionPanel.setBackground(BACKGROUND);

        actionPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JButton importButton =
                new JButton("Import CSV");

        analyzeButton =
                new JButton("Analyze Dataset");
        
        importButton.addActionListener(e -> openCSVFile());
        analyzeButton.addActionListener(e -> runAnalysis());

        stylePrimaryButton(importButton);

        styleSecondaryButton(analyzeButton);

        actionPanel.add(importButton);
        actionPanel.add(analyzeButton);

        content.add(actionPanel);

        mainPanel.add(
                content,
                BorderLayout.CENTER
        );

        add(mainPanel, BorderLayout.CENTER);
    }

    // ==============================
    // STATISTIC CARD
    // ==============================

    private JPanel createCard(
            String title,
            JLabel value
    ) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(
                        card,
                        BoxLayout.Y_AXIS
                )
        );

        card.setBackground(WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 228, 233)
                        ),
                        new EmptyBorder(
                                15,
                                20,
                                15,
                                20
                        )
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        11)
        );

        titleLabel.setForeground(
                SECONDARY_TEXT
        );

        value.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        28)
        );

        value.setForeground(PRIMARY);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(value);

        return card;
    }

    // ==============================
    // BUTTON STYLES
    // ==============================

    private void stylePrimaryButton(
            JButton button
    ) {

        button.setBackground(PRIMARY);
        button.setForeground(WHITE);

        button.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        13)
        );

        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(
                        12,
                        22,
                        12,
                        22
                )
        );
    }
    // ==============================
    // REVIEW 2: LOAD CSV (THREADS)
    // ==============================

    private void openCSVFile() {

        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setDialogTitle("Select CSV File");

        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        // THREAD 1: read the file in the background so the window does not freeze
        Thread loaderThread = new Thread(() -> {

            try {

                List<String[]> all = CsvReader.read(selectedFile);

                if (all.isEmpty()) {
                    throw new IOException("The file is empty.");
                }

                String[] fileHeaders = all.get(0);                    // first line = column names
                List<String[]> fileRows = all.subList(1, all.size());

                // Swing components may only be changed from the Swing thread
                SwingUtilities.invokeLater(
                        () -> showDataset(fileHeaders, fileRows)
                );

                // THREAD 2: store the dataset info in MySQL (existing JDBC method)
                saveToDatabase(
                        selectedFile.getName(),
                        fileRows.size(),
                        fileHeaders.length
                );

            } catch (IOException ex) {

                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(
                                this,
                                "Could not read file:\n" + ex.getMessage(),
                                "File Error",
                                JOptionPane.ERROR_MESSAGE
                        )
                );
            }
        });

        loaderThread.start();
    }

    // Fills the existing table and the ROWS / COLUMNS cards
    private void showDataset(String[] fileHeaders, List<String[]> fileRows) {

        headers = fileHeaders;
        rows = fileRows;

        DefaultTableModel model =
                new DefaultTableModel(fileHeaders, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        int previewCount = Math.min(100, fileRows.size());   // preview = first 100 rows

        for (int i = 0; i < previewCount; i++) {
            model.addRow(fileRows.get(i));
        }

        dataTable.setModel(model);

        rowValue.setText(String.valueOf(fileRows.size()));
        columnValue.setText(String.valueOf(fileHeaders.length));
        missingValue.setText("-");                             // filled in by the analysis
    }

    private void saveToDatabase(String filename, int rowCount, int columnCount) {

        Thread databaseThread = new Thread(() -> {

            try {

                database.databaseManager.insertDataset(
                        filename, rowCount, columnCount
                );

            } catch (Exception ex) {

                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(
                                this,
                                "File loaded, but it could not be saved to the database:\n"
                                        + ex.getMessage(),
                                "Database Error",
                                JOptionPane.WARNING_MESSAGE
                        )
                );
            }
        });

        databaseThread.start();
    }

    // ==============================
    // REVIEW 2: ANALYSIS (THREADS)
    // ==============================

    private void runAnalysis() {

        if (rows == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Import a CSV file first.",
                    "Analysis",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        String[] currentHeaders = headers;
        List<String[]> currentRows = rows;

        String[] results = new String[analyzers.length];
        Thread[] workers = new Thread[analyzers.length];

        analyzeButton.setEnabled(false);

        // THREADS 3..5: one thread per analyzer, all running at the same time
        for (int i = 0; i < analyzers.length; i++) {

            final int index = i;

            workers[i] = new Thread(() -> {

                Analyzer analyzer = analyzers[index];

                try {
                    results[index] = "=== " + analyzer.getName() + " ===\n"
                            + analyzer.analyze(currentHeaders, currentRows)
                            + "\n[ran on " + Thread.currentThread().getName() + "]\n\n";
                } catch (RuntimeException ex) {
                    results[index] = "=== " + analyzer.getName() + " ===\n"
                            + "Analysis failed: " + ex.getMessage() + "\n\n";
                }
            });

            workers[i].start();
        }

        // This thread waits (join) for every worker, then shows the results
        Thread waiterThread = new Thread(() -> {

            try {
                for (Thread worker : workers) {
                    worker.join();
                }
            } catch (InterruptedException ex) {
                SwingUtilities.invokeLater(() -> analyzeButton.setEnabled(true));
                return;
            }

            StringBuilder report = new StringBuilder();

            for (String result : results) {
                report.append(result);
            }

            int totalMissing =
                    missingAnalyzer.countMissing(currentHeaders, currentRows);

            SwingUtilities.invokeLater(() -> {
                missingValue.setText(String.valueOf(totalMissing));
                analyzeButton.setEnabled(true);
                showReport(report.toString());
            });
        });

        waiterThread.start();
    }

    private void showReport(String text) {

        JTextArea area = new JTextArea(text, 18, 50);

        area.setEditable(false);

        area.setFont(
                new Font("Monospaced", Font.PLAIN, 13)
        );

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Analysis Report",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void styleSecondaryButton(
            JButton button
    ) {

        button.setBackground(WHITE);
        button.setForeground(TEXT);

        button.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        13)
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 214, 220)
                        ),
                        new EmptyBorder(
                                11,
                                21,
                                11,
                                21
                        )
                )
        );
    }
    private void showDatabaseRecords() {

    try {

        java.util.List<String> datasets =
                database.databaseManager.getDatasets();

        if (datasets.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No datasets found in the database.",
                    "Database",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        StringBuilder message =
                new StringBuilder();

        message.append(
                "Datasets stored in MySQL:\n\n"
        );

        for (String dataset : datasets) {

            message.append(dataset);
            message.append("\n");
        }

        JOptionPane.showMessageDialog(
                this,
                message.toString(),
                "Database Records",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                this,
                "Database error:\n" +
                e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
}