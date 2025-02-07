package org.example.first;

import java.sql.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ExchangeRateDAO {

    public List<ExchangeRateDTO> getAll() throws SQLException {
        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
        String query = "SELECT " +
                        "er.id AS resultId, " +
                        "er.Rate AS resultRate, " +
                            "c1.id AS resultCurrencyId1, " +
                            "c1.code AS resultCode1, " +
                            "c1.full_name AS resultFullName1, " +
                            "c1.sign AS resultSign1, " +
                            "c2.id AS resultCurrencyId2, " +
                            "c2.code AS resultCode2, " +
                            "c2.full_name AS resultFullName2, " +
                            "c2.sign AS resultSign2 " +
                        "FROM ExchangeRates er " +
                        "JOIN Currencies c1 ON er.BaseCurrencyId = c1.id " +
                        "JOIN Currencies c2 ON er.TargetCurrencyId = c2.id ";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                exchangeRates.add(new ExchangeRateDTO(
                        rs.getString("resultId"),
                        new CurrencyDTO(
                                rs.getString("resultCurrencyId1"),
                                rs.getString("resultCode1"),
                                rs.getString("resultFullName1"),
                                rs.getString("resultSign1")),
                        new CurrencyDTO(
                                rs.getString("resultCurrencyId2"),
                                rs.getString("resultCode2"),
                                rs.getString("resultFullName2"),
                                rs.getString("resultSign2")),
                        rs.getString("resultRate")
                ));
            }
        }
        return exchangeRates;
    }

    public ExchangeRateDTO getRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        String query = "SELECT " +
                            "er.id AS resultId, " +
                            "er.BaseCurrencyId AS resultBaseCurrencyId, " +        //как-будто айдишники тут лишние, нет?
                            "er.TargetCurrencyId AS resultTargetCurrencyId, " +
                            "er.Rate AS resultRate, " +
                                "c1.id AS resultId1, " +
                                "c2.id AS resultId2, " +
                                "c1.code AS resultCode1, " +
                                "c2.code AS resultCode2, " +
                                "c1.full_name AS resultName1, " +
                                "c2.full_name AS resultName2, " +
                                "c1.sign AS resultSign1, " +
                                "c2.sign AS resultSign2 " +
                        "FROM ExchangeRates er " +
                        "JOIN Currencies c1 ON er.BaseCurrencyId = c1.id " +
                        "JOIN Currencies c2 ON er.TargetCurrencyId = c2.id " +
                        "WHERE c1.code = ? AND c2.code = ?";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new ExchangeRateDTO(
                            rs.getString("resultId"),
                            new CurrencyDTO(
                                    rs.getString("resultId1"),
                                    rs.getString("resultCode1"),
                                    rs.getString("resultName1"),
                                    rs.getString("resultSign1")),
                            new CurrencyDTO(
                                    rs.getString("resultId2"),
                                    rs.getString("resultCode2"),
                                    rs.getString("resultName2"),
                                    rs.getString("resultSign2")),
                            rs.getString("resultRate")
                    );
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }
}
