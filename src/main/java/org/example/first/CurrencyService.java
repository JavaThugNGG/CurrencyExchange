package org.example.first;


import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public Currency getCurrencyByCode(String code) throws SQLException {
        return currencyDAO.getByCode(code);
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        return currencyDAO.getAll();
    }

    public Currency createCurrency(String fullName, String code, String sign) {
        validateCurrency(fullName, code, sign);

        try {
            if (currencyDAO.getByCode(code) != null) {
                throw new CurrencyConflictException("Валюта с кодом " + code + " уже существует.");
            }
            String id = currencyDAO.insert(fullName, code, sign);
            return new Currency(id, fullName, code, sign);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка создания валюты", e);
        }
    }

    public boolean validatePath(String path) {
        return path != null && path.matches("^/[A-Z]{3}$");
    }

    private void validateCurrency(String fullName, String code, String sign) {
        if (isParameterEmpty(fullName)) {
            throw new IllegalArgumentException("Полное название валюты (name) обязательно.");
        }
        if (isParameterEmpty(code)) {
            throw new IllegalArgumentException("Код валюты (code) обязателен.");
        }
        if (isParameterEmpty(sign)) {
            throw new IllegalArgumentException("Знак валюты (sign) обязателен.");
        }
    }

    private boolean isParameterEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

