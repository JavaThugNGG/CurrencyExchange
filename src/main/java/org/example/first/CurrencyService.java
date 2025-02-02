package org.example.first;


import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public Currency getCurrencyByCode(String code) {
        try {
            return currencyDAO.getByCode(code);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения валюты", e);
        }
    }

    public List<Currency> getAllCurrencies() {
        try {
            return currencyDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения списка валют", e);
        }
    }

    public Currency createCurrency(String fullName, String code, String sign) {
        validateCurrencyData(fullName, code, sign); // Проверяем входные данные

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


    private void validateCurrencyData(String fullName, String code, String sign) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Полное название валюты (name) обязательно.");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Код валюты (code) обязателен.");
        }
        if (sign == null || sign.trim().isEmpty()) {
            throw new IllegalArgumentException("Знак валюты (sign) обязателен.");
        }
    }
}

