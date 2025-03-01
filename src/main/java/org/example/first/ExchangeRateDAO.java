package org.example.first;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {

    public List<ExchangeRateDTO> getAll() throws SQLException {
        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
        String query = "SELECT er.id AS exchangeRateId, " +
                "er.rate AS exchangeRate, " +
                "c1.id AS baseId, " +
                "c1.code AS baseCode, " +
                "c1.full_name AS baseFullName, " +
                "c1.sign AS baseSign, " +
                "c2.id AS targetId, " +
                "c2.code AS targetCode, " +
                "c2.full_name AS targetFullName, " +
                "c2.sign AS targetSign " +
                "FROM exchange_rates er " +
                "JOIN currencies c1 ON er.base_currency_id = c1.id " +
                "JOIN currencies c2 ON er.target_currency_id = c2.id ";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String newTarget = " - " + rs.getString("targetFullName");
                exchangeRates.add(new ExchangeRateDTO(
                        rs.getLong("exchangeRateId"),
                        new CurrencyDTO(
                                rs.getLong("baseId"),
                                rs.getString("baseCode"),
                                rs.getString("baseFullName"),
                                rs.getString("baseSign")),
                        new CurrencyDTO(
                                rs.getLong("targetId"),
                                rs.getString("targetCode"),
                                newTarget,
                                rs.getString("targetSign")),
                        rs.getDouble("exchangeRate")
                ));
            }
        }
        return exchangeRates;
    }

    public ExchangeRateDTO getRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        String query = "SELECT er.id AS exchangeRateId, " +
                "er.rate AS exchangeRate, " +
                "c1.id AS baseId, " +
                "c1.full_name AS baseFullName, " +
                "c1.code AS baseCode, " +
                "c1.sign AS baseSign, " +
                "c2.id AS targetId, " +
                "c2.full_name AS targetFullName, " +
                "c2.code AS targetCode, " +
                "c2.sign AS targetSign " +
                "FROM exchange_rates er " +
                "JOIN currencies c1 ON er.base_currency_id = c1.id " +
                "JOIN currencies c2 ON er.target_currency_id = c2.id " +
                "WHERE c1.code = ? AND c2.code = ?";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ExchangeRateDTO(
                            rs.getLong("exchangeRateId"),
                            new CurrencyDTO(
                                    rs.getLong("baseId"),
                                    rs.getString("baseCode"),
                                    rs.getString("baseFullName"),
                                    rs.getString("baseSign")),
                            new CurrencyDTO(
                                    rs.getLong("targetId"),
                                    rs.getString("targetCode"),
                                    rs.getString("targetFullName"),
                                    rs.getString("targetSign")),
                            rs.getDouble("exchangeRate")
                    );
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        String query = "UPDATE exchange_rates " +
                "SET rate = ? " +
                "WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?) " +
                "AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);
            stmt.executeUpdate();
        }
    }

    public void insert(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        String query =  "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) " +
                        "VALUES (" +
                            "(SELECT id FROM currencies WHERE code = ?), " +
                            "(SELECT id FROM currencies WHERE code = ?), " +
                            "? " +
                        ")";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            stmt.setDouble(3, rate);
            stmt.executeUpdate();
        }
    }

    public boolean isExists(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        String query =  "SELECT id " +
                        "FROM exchange_rates " +
                        "WHERE base_currency_id = (SELECT id " +
                                                  "FROM currencies " +
                                                  "WHERE code = ?) " +
                        "AND target_currency_id = (SELECT id " +
                                                  "FROM currencies " +
                                                  "WHERE code = ?)";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }
}


