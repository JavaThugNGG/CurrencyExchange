package currencyExchange.DAO;

import currencyExchange.DTO.RawExchangeDTO;
import currencyExchange.db.DatabaseConnectionProvider;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    return RawExchangeDTO.parseToRawExchangeDTO(resultSet, amount);
                } else {
                    throw new SQLException();
                }
            }
        }
    }
}
