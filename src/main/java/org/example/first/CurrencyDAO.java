package org.example.first;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    public CurrencyDTO getByCode(String code) throws SQLException {
        String query = "SELECT * FROM Currencies WHERE code = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CurrencyDTO(
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("code"),
                            rs.getString("sign")
                    );
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }

    public List<CurrencyDTO> getAll() throws SQLException {
        List<CurrencyDTO> currencies = new ArrayList<>();
        String query = "SELECT * FROM Currencies";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                currencies.add(new CurrencyDTO(
                        rs.getString("id"),
                        rs.getString("full_name"),
                        rs.getString("code"),
                        rs.getString("sign")
                ));
            }
        }
        return currencies;
    }

    public CurrencyDTO getById(String code) throws SQLException {
        String query = "SELECT * FROM Currencies WHERE id = ?";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CurrencyDTO(
                            rs.getString("id"),
                            rs.getString("full_name"),
                            rs.getString("code"),
                            rs.getString("sign")
                    );
                } else {
                    throw new ElementNotFoundException();
                }
            }
        }
    }

    public String insert(String fullName, String code, String sign) throws SQLException {
        if (isCurrencyExist(code)) {
            throw new ElementAlreadyExistsException();
        }

        String query = "INSERT INTO Currencies (full_name, code, sign) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, fullName);
            stmt.setString(2, code);
            stmt.setString(3, sign);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                return String.valueOf(generatedKeys.getLong(1));
            }
        }
    }

    private boolean isCurrencyExist(String code) throws SQLException {
        String query = "SELECT COUNT(*) FROM Currencies WHERE code = ?";
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

