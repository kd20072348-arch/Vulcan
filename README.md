CSV Analyzer
A Java-based CSV analysis application built using Java Swing for the graphical user interface and JDBC + MySQL for database connectivity.

The project is designed to allow users to import CSV files, view dataset information, analyze data, and store dataset information in a MySQL database.

Project Status
Completed
Java Swing GUI
CSV file selection using JFileChooser
Dashboard-style interface
Sidebar navigation
Dataset statistics cards
JTable dataset preview
MySQL database setup
JDBC connection
JDBC INSERT functionality
JDBC SELECT functionality
Display database records through the Swing interface
Planned
Full CSV parsing
Automatic row/column counting
Missing-value analysis
Duplicate detection
Bar charts
Pie charts
Histograms
Complete database table display
Improved error handling
Final UI polishing
Technologies Used
Technology	Purpose
Java	Main programming language
Java Swing	Graphical User Interface
JDBC	Java-to-MySQL connectivity
MySQL	Database
MySQL Connector/J	JDBC driver
JTable	Dataset display
JFileChooser	CSV file selection
Project Structure
csv_analyser/
│
├── lib/
│   └── mysql-connector-j-26.7.0.jar
│
├── src/
│   └── main/
│       └── java/
│           │
│           ├── Main.java
│           │
│           ├── view/
│           │   └── MainFrame.java
│           │
│           └── database/
│               └── DatabaseManager.java
│
├── out/
│
└── README.md
steps to run: cd to project directory compile:"javac -cp "lib\mysql-connector-j-26.7.0.jar" -d out src\main\java\database\DatabaseManager.java src\main\java\view\MainFrame.java src\main\java\Main.java" run:"java -cp "out;lib\mysql-connector-j-26.7.0.jar" Main"

*note ensure MySQL database is already setup
