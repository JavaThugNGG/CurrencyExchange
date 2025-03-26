package org.example.first;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.RoundingMode;

public class ExchangeDAO {
    public ExchangeDTO getRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
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
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    BigDecimal convertedAmount = rate.multiply(amount);
                    BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

                    return new ExchangeDTO(new CurrencyDTO(resultSet.getLong("baseId"),
                                                resultSet.getString("baseName"),
                                                resultSet.getString("baseCode"),
                                                resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getLong("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                                    resultSet.getBigDecimal("rate"),
                            amount,
                            roundedConvertedAmount);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateFromReversedRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
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
                    BigDecimal rate = resultSet.getBigDecimal("rate");
                    BigDecimal reversedRate = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
                    BigDecimal convertedAmount = reversedRate.multiply(amount);
                    BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);


                    return new ExchangeDTO(new CurrencyDTO(resultSet.getLong("baseId"),
                            resultSet.getString("baseName"),
                            resultSet.getString("baseCode"),
                            resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getLong("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                            resultSet.getBigDecimal("rate"),
                            amount,
                            roundedConvertedAmount);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateWithIntermediate(String baseCurrencyCode, String targetCurrencyCode, String intermediateCurrencyCode, BigDecimal amount) throws SQLException {
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
            BigDecimal rate1;
            long fromId;
            try (ResultSet resultSet = stmt1.executeQuery()) {
                if (resultSet.next()) {
                    fromId = resultSet.getLong("targetId");
                    fromName = resultSet.getString("targetName");
                    fromCode = resultSet.getString("targetCode");
                    fromSign = resultSet.getString("targetSign");
                    rate1 = resultSet.getBigDecimal("rate");
                } else {
                    throw new SQLException();
                }
            }

            stmt2.setString(1, targetCurrencyCode);
            stmt2.setString(2, intermediateCurrencyCode);
            String toName, toCode, toSign;
            BigDecimal rate2;
            long toId;
            try (ResultSet resultSet = stmt2.executeQuery()) {
                if (resultSet.next()) {
                    toId = resultSet.getLong("targetId");
                    toName = resultSet.getString("targetName");
                    toCode = resultSet.getString("targetCode");
                    toSign = resultSet.getString("targetSign");
                    rate2 = resultSet.getBigDecimal("rate");
                } else {
                    throw new SQLException();
                }
            }

            BigDecimal rate = rate1.divide(rate2, 8, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = rate.multiply(rate2);
            BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

            return new ExchangeDTO(new CurrencyDTO(fromId, fromName, fromCode, fromSign),
                    (new CurrencyDTO(toId, toName, toCode, toSign)),
                    rate,
                    amount,
                    roundedConvertedAmount);
        }
    }
}
