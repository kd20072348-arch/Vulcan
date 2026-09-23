package main.java;

import java.sql.Connection;

import main.java.database.databaseManager;

public class Main {

    public static void main(String[] args) {

        try {

            Connection connection = databaseManager.getConnection();

            System.out.println("Database connected successfully!");

            connection.close();

        } catch (Exception e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}