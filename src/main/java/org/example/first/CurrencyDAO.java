package org.example.first;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    public Currency getByCode(String code) throws SQLException {
        String sql = "SELECT * FROM Currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Currency(
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("code"),
                            rs.getString("sign")
                    );
                }
            }
        }
        throw new CurrencyNotFoundException("Currency with code " + code + " not found.");
    }

    public List<Currency> getAll() throws SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT * FROM Currencies";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                currencies.add(new Currency(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("code"),
                        rs.getString("sign")
                ));
            }
        }
        return currencies;
    }

    public String insert(String fullName, String code, String sign) throws SQLException {
        String sql = "INSERT INTO Currencies (full_name, code, sign) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, code);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return String.valueOf(generatedKeys.getLong(1));
                }
            }
        }
        return null;
    }
}

