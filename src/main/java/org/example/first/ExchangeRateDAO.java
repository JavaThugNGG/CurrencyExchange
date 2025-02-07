package org.example.first;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAO {

    public List<ExchangeRate> getAll() throws SQLException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        String query = "SELECT * FROM ExchangeRates";

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                exchangeRates.add(new ExchangeRate(
                        rs.getString("id"),
                        rs.getString("baseCurrencyId"),
                        rs.getString("targetCurrencyId"),
                        rs.getString("rate")
                ));
            }
        }
        return exchangeRates;
    }
}
