package org.example.first;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeDAO {
    public ExchangeDTO getRate(String baseCurrencyCode, String targetCurrencyCode, double amount) throws SQLException {
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
                    double rate = resultSet.getDouble("rate");
                    double convertedAmount = rate * amount;
                    double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;

                    return new ExchangeDTO(new CurrencyDTO(resultSet.getLong("baseId"),
                                                resultSet.getString("baseName"),
                                                resultSet.getString("baseCode"),
                                                resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getLong("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                                    resultSet.getDouble("rate"),
                            amount,
                            roundedConvertedAmount);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateFromReverseRate(String baseCurrencyCode, String targetCurrencyCode, double amount) throws SQLException {
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
                    double rate = resultSet.getDouble("rate");
                    double reversedRate = 1 / rate;
                    double convertedAmount = reversedRate * amount;
                    double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;

                    return new ExchangeDTO(new CurrencyDTO(resultSet.getLong("baseId"),
                            resultSet.getString("baseName"),
                            resultSet.getString("baseCode"),
                            resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getLong("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                            resultSet.getDouble("rate"),
                            amount,
                            roundedConvertedAmount);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateWithIntermediate(String baseCurrencyCode, String targetCurrencyCode, String intermediateCurrencyCode, double amount) throws SQLException {
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
             PreparedStatement stmt1 = conn.prepareStatement(query);
             PreparedStatement stmt2 = conn.prepareStatement(query)) {
            stmt1.setString(1, intermediateCurrencyCode);
            stmt1.setString(2, baseCurrencyCode);
            String fromName, fromCode, fromSign;
            double rate1;
            long fromId;
            try (ResultSet resultSet = stmt1.executeQuery()) {
                if (resultSet.next()) {
                    fromId = resultSet.getLong("targetId");
                    fromName = resultSet.getString("targetName");
                    fromCode = resultSet.getString("targetCode");
                    fromSign = resultSet.getString("targetSign");
                    rate1 = resultSet.getDouble("rate");
                } else {
                    throw new SQLException();
                }
            }

            stmt2.setString(1, targetCurrencyCode);
            stmt2.setString(2, intermediateCurrencyCode);
            String toName, toCode, toSign;
            double rate2;
            long toId;
            try (ResultSet resultSet = stmt2.executeQuery()) {
                if (resultSet.next()) {
                    toId = resultSet.getLong("targetId");
                    toName = resultSet.getString("targetName");
                    toCode = resultSet.getString("targetCode");
                    toSign = resultSet.getString("targetSign");
                    rate2 = resultSet.getDouble("rate");
                } else {
                    throw new SQLException();
                }
            }

            double rate = rate1 / rate2;
            double convertedAmount = rate * amount;
            double roundedConvertedAmount = Math.round(convertedAmount * 100.0) / 100.0;

            return new ExchangeDTO(new CurrencyDTO(fromId, fromName, fromCode, fromSign),
                    (new CurrencyDTO(toId, toName, toCode, toSign)),
                    rate,
                    amount,
                    roundedConvertedAmount);
        }

    }
}
