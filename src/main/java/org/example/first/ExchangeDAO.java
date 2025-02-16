package org.example.first;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeDAO {
    public ExchangeDTO getRate(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException {
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
                    String rate = resultSet.getString("rate");
                    double rateDouble = Double.parseDouble(rate);
                    double amountDouble = Double.parseDouble(amount);
                    double convertedAmount = rateDouble * amountDouble;
                    String convertedAmountString = String.valueOf(convertedAmount);

                    return new ExchangeDTO(new CurrencyDTO(resultSet.getString("baseId"),
                                                resultSet.getString("baseName"),
                                                resultSet.getString("baseCode"),
                                                resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getString("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                                    resultSet.getString("rate"),
                            amount,
                            convertedAmountString);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateFromReverseRate(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException {
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
                    String rate = resultSet.getString("rate");
                    double rateDouble = Double.parseDouble(rate);
                    double reversedRateDouble = 1 / rateDouble;
                    double amountDouble = Double.parseDouble(amount);
                    double convertedAmount = reversedRateDouble * amountDouble;
                    String convertedAmountString = String.valueOf(convertedAmount);

                    return new ExchangeDTO(new CurrencyDTO(resultSet.getString("baseId"),
                            resultSet.getString("baseName"),
                            resultSet.getString("baseCode"),
                            resultSet.getString("baseSign")),
                            new CurrencyDTO(resultSet.getString("targetId"),
                                    resultSet.getString("targetName"),
                                    resultSet.getString("targetCode"),
                                    resultSet.getString("targetSign")),
                            resultSet.getString("rate"),
                            amount,
                            convertedAmountString);

                } else {
                    throw new SQLException();
                }
            }
        }
    }

    public ExchangeDTO getRateWithIntermediate(String baseCurrencyCode, String targetCurrencyCode, String intermediateCurrencyCode, String amount) throws SQLException {
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
            String fromId, fromName, fromCode, fromSign, rate1;
            try (ResultSet resultSet = stmt1.executeQuery()) {
                if (resultSet.next()) {
                    fromId = resultSet.getString("targetId");
                    fromName = resultSet.getString("targetName");
                    fromCode = resultSet.getString("targetCode");
                    fromSign = resultSet.getString("targetSign");
                    rate1 = resultSet.getString("rate");
                } else {
                    throw new SQLException();
                }
            }

            stmt2.setString(1, targetCurrencyCode);
            stmt2.setString(2, intermediateCurrencyCode);
            String toId, toName, toCode, toSign, rate2;
            try (ResultSet resultSet = stmt2.executeQuery()) {
                if (resultSet.next()) {
                    toId = resultSet.getString("targetId");
                    toName = resultSet.getString("targetName");
                    toCode = resultSet.getString("targetCode");
                    toSign = resultSet.getString("targetSign");
                    rate2 = resultSet.getString("rate");
                } else {
                    throw new SQLException();
                }
            }

            double rate1Double = Double.parseDouble(rate1);
            double rate2Double = Double.parseDouble(rate2);
            double rateDouble = rate1Double / rate2Double;
            String rate = String.valueOf(rateDouble);
            double amountDouble = Double.parseDouble(amount);
            double convertedAmount = rateDouble * amountDouble;
            String convertedAmountString = String.valueOf(convertedAmount);

            return new ExchangeDTO(new CurrencyDTO(fromId, fromName, fromCode, fromSign),
                    (new CurrencyDTO(toId, toName, toCode, toSign)),
                    rate,
                    amount,
                    convertedAmountString);
        }

    }
}
