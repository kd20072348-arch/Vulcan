# CSV Analyzer

A Java-based CSV analysis application built using **Java Swing** for the graphical user interface and **JDBC + MySQL** for database connectivity.

The application is designed to allow users to import CSV files, view dataset information, analyze data, and store dataset information in a MySQL database.

---

## 📌 Project Status

### ✅ Completed

* Java Swing GUI
* CSV file selection using `JFileChooser`
* Dashboard-style interface
* Sidebar navigation
* Dataset statistics cards
* `JTable` dataset preview
* MySQL database setup
* JDBC connection
* JDBC `INSERT` functionality
* JDBC `SELECT` functionality
* Display database records through the Swing interface

### 🚧 Planned

* Full CSV parsing
* Automatic row/column counting
* Missing-value analysis
* Duplicate detection
* Bar charts
* Pie charts
* Histograms
* Complete database table display
* Improved error handling
* Final UI polishing

---

## 🛠️ Technologies Used

| Technology            | Purpose                    |
| --------------------- | -------------------------- |
| **Java**              | Main programming language  |
| **Java Swing**        | Graphical User Interface   |
| **JDBC**              | Java-to-MySQL connectivity |
| **MySQL**             | Database                   |
| **MySQL Connector/J** | JDBC driver                |
| **JTable**            | Dataset display            |
| **JFileChooser**      | CSV file selection         |

---

## 📂 Project Structure

```text
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
```

---

## ▶️ How to Run

### 1. Clone or download the project

Open the project directory in your terminal:

```bash
cd csv_analyser
```

### 2. Make sure MySQL is configured

Ensure that the required MySQL database has already been created and that the credentials in `DatabaseManager.java` are correctly configured.

### 3. Compile the project

Run:

```bash
javac -cp "lib\mysql-connector-j-26.7.0.jar" -d out src\main\java\database\DatabaseManager.java src\main\java\view\MainFrame.java src\main\java\Main.java
```

### 4. Run the application

```bash
java -cp "out;lib\mysql-connector-j-26.7.0.jar" Main
```

---

## 🗄️ Database

The application uses **MySQL** through **JDBC** to store and retrieve dataset information.

The database must be configured before running the application.

The JDBC connection is managed through:

```text
database/
└── DatabaseManager.java
```

---

## 📊 Planned Analysis Features

The final version of CSV Analyzer is intended to provide automated dataset analysis, including:

* Row and column statistics
* Missing-value detection
* Duplicate-row detection
* Numerical data analysis
* Categorical data analysis
* Dataset visualizations
* Bar charts
* Pie charts
* Histograms

---

