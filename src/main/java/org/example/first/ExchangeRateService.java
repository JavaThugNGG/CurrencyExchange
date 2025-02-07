package org.example.first;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return exchangeRateDAO.getAll();
    }

    public boolean isPathValidated(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}$");
    }

    public String splitBaseCurrency(String path) {
        return path.substring(0, 3);
    }

    public String splitTargetCurrency(String path) {
        return path.substring(3, 6);
    }

    public String getPathWithoutSlash(String path) {
        return path.substring(1);
    }

    public ExchangeRateDTO getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.getRate(baseCurrencyCode, targetCurrencyCode);

    }
}
