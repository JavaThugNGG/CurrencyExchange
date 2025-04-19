package currencyExchange.dao;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.db.DatabaseConnectionProvider;
import currencyExchange.mappers.CurrencyMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    public CurrencyDto getCurrency(String code)  {
        String query =  """
                        SELECT *
                        FROM currencies
                        WHERE code = ?;
                        """;
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return CurrencyMapper.toDto(rs);
            }
            throw new ElementNotFoundException("Запрашиваемая валюта не найдена");
        }
        catch (SQLException e) {
            throw new RuntimeException("Ошибка при работе с базой данных");
        }
    }

    public List<CurrencyDto> getAllCurrencies() {
        List<CurrencyDto> currencies = new ArrayList<>();
        String query =  """
                        SELECT *
                        FROM currencies;
                        """;
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                currencies.add(CurrencyMapper.toDto(rs));
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    public long insertCurrency(String fullName, String code, String sign) {
        String query =  """
                        INSERT INTO currencies (full_name, code, sign)
                        VALUES (?, ?, ?);
                        """;
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, code);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            return generatedKeys.getLong(1);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isCurrencyExists(String code) {
        String query =  """
                        SELECT COUNT(*)
                        FROM currencies
                        WHERE code = ?;
                        """;
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
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

