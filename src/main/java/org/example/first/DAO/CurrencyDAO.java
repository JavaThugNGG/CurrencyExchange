package org.example.first.DAO;

import org.example.first.DTO.CurrencyDTO;
import org.example.first.db.DatabaseConnectionProvider;
import org.example.first.exceptions.ElementNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    public CurrencyDTO getCurrency(String code) throws SQLException {
        String query =  "SELECT * " +
                        "FROM currencies " +
                        "WHERE code = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return CurrencyDTO.parseToCurrencyDTO(rs);
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }

    public List<CurrencyDTO> getAllCurrencies() throws SQLException {
        List<CurrencyDTO> currencies = new ArrayList<>();
        String query =  "SELECT * " +
                        "FROM currencies";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                currencies.add(CurrencyDTO.parseToCurrencyDTO(rs));
            }
        }
        return currencies;
    }

    public long insertCurrency(String fullName, String code, String sign) throws SQLException {
        String query =  "INSERT INTO currencies (full_name, code, sign) " +
                        "VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, code);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                return generatedKeys.getLong(1);
            }
        }
    }

    public boolean isCurrencyExists(String code) throws SQLException {
        String query =  "SELECT COUNT(*) " +
                        "FROM currencies " +
                        "WHERE code = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }
}

