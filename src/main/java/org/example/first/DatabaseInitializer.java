package org.example.first;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void setupDatabase() throws ClassNotFoundException {
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

    public static void setupDatabase2() {
        String dbUrl = "jdbc:sqlite:src/main/resources/database.db"; // Путь к базе данных

        try (Connection conn = DriverManager.getConnection(dbUrl)) {

            if (conn != null) {
                System.out.println("Соединение с базой данных установлено.");

                String createExchangeRatesTableSQL = "CREATE TABLE ExchangeRates ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "BaseCurrencyId INTEGER NOT NULL, "
                        + "TargetCurrencyId INTEGER NOT NULL, "
                        + "Rate DECIMAL(6) NOT NULL, "
                        + "UNIQUE(BaseCurrencyId, TargetCurrencyId), "
                        + "FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(id), "
                        + "FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(id));";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createExchangeRatesTableSQL);
                    System.out.println("Таблица ExchangeRates создана или уже существует.");
                }


                String insertExchangeRatesDataSQL = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES "
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


    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:src/main/resources/database.db";

    }
}
