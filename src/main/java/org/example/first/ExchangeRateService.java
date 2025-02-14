package org.example.first;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<ExchangeRateDTO> getAllExchangeRates() throws SQLException {
        return exchangeRateDAO.getAll();
    }

    public boolean isPathValidatedForGet(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}$");
    }

    public boolean isPathValidatedForPatch(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}");
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

    public void updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) throws SQLException {
        if (exchangeRateDAO.isExists(baseCurrencyCode, targetCurrencyCode)) {
            throw new ElementAlreadyExistsException();
        }
        exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public void putExchangeRate(String baseCurrencyCode, String targetCurrencyCode, String rate) throws SQLException {
        if (exchangeRateDAO.isExists(baseCurrencyCode, targetCurrencyCode)) {
            throw new ElementAlreadyExistsException();
        }
        if (!currencyDAO.isExists(baseCurrencyCode) || !currencyDAO.isExists(targetCurrencyCode)) {
            throw new ElementNotFoundException();
        }
        exchangeRateDAO.insert(baseCurrencyCode, targetCurrencyCode, rate);
    }
}
