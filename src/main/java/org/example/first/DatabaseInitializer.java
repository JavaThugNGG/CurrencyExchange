package org.example.first;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void setupDatabase() {
        String dbUrl = "jdbc:sqlite:src/main/resources/database.db"; // Путь к базе данных

        try (Connection conn = DriverManager.getConnection(dbUrl)) {

            if (conn != null) {
                System.out.println("Соединение с базой данных установлено.");

                String createTableSQL = "CREATE TABLE Currencies ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "code VARCHAR(30) NOT NULL UNIQUE, "
                        + "full_name VARCHAR(30) NOT NULL, "
                        + "sign VARCHAR(30) NOT NULL);";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Таблица Currencies создана или уже существует.");
                }

                String insertDataSQL = "INSERT INTO Currencies (code, full_name, sign) VALUES "
                        + "('USD', 'United States Dollar', '$'), "
                        + "('EUR', 'Euro', '€'), "
                        + "('GBP', 'British Pound', '£'), "
                        + "('RUB', 'Russian Ruble', '₽');";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(insertDataSQL);
                    System.out.println("Данные добавлены в таблицу Currencies.");
                }

            }
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        setupDatabase();
    }
}
