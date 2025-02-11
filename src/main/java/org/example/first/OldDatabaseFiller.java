package org.example.first;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class OldDatabaseFiller {

    public static void setupDatabase() throws ClassNotFoundException {
        String dbUrl = "jdbc:sqlite:src/main/resources/database1.db"; // Путь к базе данных


        try (Connection conn = DriverManager.getConnection(dbUrl)) {

            if (conn != null) {
                System.out.println("Соединение с базой данных установлено.");

                String createTableSQL = "CREATE TABLE currencies ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "code VARCHAR(30) NOT NULL UNIQUE, "
                        + "full_name VARCHAR(30) NOT NULL, "
                        + "sign VARCHAR(30) NOT NULL);";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Таблица Currencies создана или уже существует.");
                }

                String insertDataSQL = "INSERT INTO currencies (code, full_name, sign) VALUES "
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

    public static void setupDatabase2() {
        String dbUrl = "jdbc:sqlite:src/main/resources/database.db1"; // Путь к базе данных

        try (Connection conn = DriverManager.getConnection(dbUrl)) {

            if (conn != null) {
                System.out.println("Соединение с базой данных установлено.");

                String createExchangeRatesTableSQL = "CREATE TABLE exchange_rates ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "base_currency_id INTEGER NOT NULL, "
                        + "target_currency_id INTEGER NOT NULL, "
                        + "rate DECIMAL(6) NOT NULL, "
                        + "UNIQUE(base_currency_id, target_currency_id), "
                        + "FOREIGN KEY (base_currency_id) REFERENCES currencies(id), "
                        + "FOREIGN KEY (target_currency_id) REFERENCES currencies(id));";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createExchangeRatesTableSQL);
                    System.out.println("Таблица ExchangeRates создана или уже существует.");
                }


                String insertExchangeRatesDataSQL = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES "
                        + "(1, 2, 0.85), "  // USD -> EUR
                        + "(1, 3, 0.75), "  // USD -> GBP
                        + "(1, 4, 74.32), " // USD -> RUB
                        + "(2, 1, 1.18), "  // EUR -> USD
                        + "(2, 3, 0.88), "  // EUR -> GBP
                        + "(2, 4, 87.65), " // EUR -> RUB
                        + "(3, 1, 1.33), "  // GBP -> USD
                        + "(3, 2, 1.14), "  // GBP -> EUR
                        + "(3, 4, 99.87);"; // GBP -> RUB

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(insertExchangeRatesDataSQL);
                    System.out.println("Данные добавлены в таблицу ExchangeRates.");
                }

            }
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws ClassNotFoundException {
        setupDatabase2();

    }
}
