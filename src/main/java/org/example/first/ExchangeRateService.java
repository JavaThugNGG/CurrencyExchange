package org.example.first;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<ExchangeRateDTO> getAllRates() throws SQLException {
        return exchangeRateDAO.getAllRates();
    }

    public boolean validatePathForGet(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}$");
    }

    public boolean validatePathForPatch(String path) {
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

    public ExchangeRateDTO getRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.getRate(baseCurrencyCode, targetCurrencyCode);
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {      //проблема с race condition, но по тз нужно возвращать 404
            exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
        } else {
            throw new ElementNotFoundException();
        }
    }

    public void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            throw new ElementAlreadyExistsException();
        }
        if (!currencyDAO.isCurrencyExists(baseCurrencyCode) || !currencyDAO.isCurrencyExists(targetCurrencyCode)) {
            throw new ElementNotFoundException();
        }
        exchangeRateDAO.insertRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public boolean validateParameters(String baseCode, String targetCode, String rate) {
        return baseCode != null & targetCode != null & rate != null & baseCode.matches("([A-Za-z]{1,3})") && targetCode.matches("([A-Za-z]{1,3})") && validateRate(rate);
    }

    public boolean validateRate(String rate) {
        return rate != null && rate.matches("[1-9]\\d*(\\.\\d{1,8})?");
    }
}
