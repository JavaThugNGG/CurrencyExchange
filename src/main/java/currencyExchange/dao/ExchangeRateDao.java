package currencyExchange.dao;

import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.db.DatabaseConnectionProvider;
import currencyExchange.mappers.ExchangeRateMapper;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {
    public List<ExchangeRateDto> getAllRates() {
        List<ExchangeRateDto> exchangeRates = new ArrayList<>();
        String query = """
                SELECT er.id AS rateId,
                er.rate AS rate,
                c1.id AS baseId,
                c1.code AS baseCode,
                c1.full_name AS baseName,
                c1.sign AS baseSign,
                c2.id AS targetId,
                c2.code AS targetCode,
                c2.full_name AS targetName,
                c2.sign AS targetSign
                FROM exchange_rates er
                JOIN currencies c1 ON er.base_currency_id = c1.id
                JOIN currencies c2 ON er.target_currency_id = c2.id;
                """;

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                exchangeRates.add(ExchangeRateMapper.toDto(rs));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    public ExchangeRateDto getRate(String baseCurrencyCode, String targetCurrencyCode) {
        String query = """
                SELECT er.id AS rateId,
                er.rate AS rate,
                c1.id AS baseId,
                c1.full_name AS baseName,
                c1.code AS baseCode,
                c1.sign AS baseSign,
                c2.id AS targetId,
                c2.full_name AS targetName,
                c2.code AS targetCode,
                c2.sign AS targetSign
                FROM exchange_rates er
                JOIN currencies c1 ON er.base_currency_id = c1.id
                JOIN currencies c2 ON er.target_currency_id = c2.id
                WHERE c1.code = ? AND c2.code = ?;
                """;

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return ExchangeRateMapper.toDto(rs);
            }
            throw new ElementNotFoundException("Запрашиваемый элемент не найден");
        }
        catch (SQLException e) {
            throw new RuntimeException("Ошибка при взаимодействии с базой данных");
        }
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        String query = """
                UPDATE exchange_rates
                SET rate = ?
                WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
                AND target_currency_id = (SELECT id FROM currencies WHERE code = ?);
                """;

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        String query =  """
                        INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
                        VALUES (
                            (SELECT id FROM currencies WHERE code = ?),
                            (SELECT id FROM currencies WHERE code = ?),
                            ?
                        );
                        """;

        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            stmt.setBigDecimal(3, rate);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        String query =  """
                        SELECT id
                        FROM exchange_rates
                        WHERE base_currency_id = (SELECT id
                                                  FROM currencies
                                                  WHERE code = ?)
                        AND target_currency_id = (SELECT id
                                                  FROM currencies
                                                  WHERE code = ?);
                        """;
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            return false;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


