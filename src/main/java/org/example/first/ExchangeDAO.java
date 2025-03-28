package org.example.first;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.RoundingMode;

public class ExchangeDAO {
    public RawExchangeDTO getRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        String query = "SELECT c1.id AS baseId, " +
                "c1.full_name AS baseName, " +
                "c1.code AS baseCode, " +
                "c1.sign AS baseSign, " +
                "c2.id AS targetId, " +
                "c2.full_name AS targetName, " +
                "c2.code AS targetCode, " +
                "c2.sign AS targetSign, " +
                "rate " +
                "FROM exchange_rates er " +
                "JOIN currencies c1 ON er.base_currency_id = c1.id " +
                "JOIN currencies c2 ON er.target_currency_id = c2.id " +
                "WHERE c1.code = ? AND c2.code = ?";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return RawExchangeDTO.parseToExchangeDAODTO(resultSet, amount);
                } else {
                    throw new SQLException();
                }
            }
        }
    }


    BigDecimal getRate(String fromCurrencyCode, String toCurrencyCode) throws SQLException {
        String query = "SELECT rate FROM exchange_rates er " +
                "JOIN currencies c1 ON er.base_currency_id = c1.id " +
                "JOIN currencies c2 ON er.target_currency_id = c2.id " +
                "WHERE c1.code = ? AND c2.code = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, fromCurrencyCode);
            stmt.setString(2, toCurrencyCode);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("rate");
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }

    CurrencyDTO getCurrencyDetails(String currencyCode) throws SQLException {
        String query = "SELECT id, full_name, code, sign FROM currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, currencyCode);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String fullName = resultSet.getString("full_name");
                    String code = resultSet.getString("code");
                    String sign = resultSet.getString("sign");
                    return new CurrencyDTO(id, fullName, code, sign);
                } else {
                    throw new SQLException();
                }
            }
        }
    }

}
