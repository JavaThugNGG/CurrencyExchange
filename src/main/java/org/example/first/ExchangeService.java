package org.example.first;

import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final ExchangeDAO exchangeDAO = new ExchangeDAO();
    private final String INTERMEDIATE_CURRENCY_CODE = "USD";


    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, double amount) throws SQLException {
        if(exchangeRateDAO.isExists(baseCurrencyCode, targetCurrencyCode)) {
            return exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
        }
        if(exchangeRateDAO.isExists(targetCurrencyCode, baseCurrencyCode)) {
            return exchangeDAO.getRateFromReverseRate(targetCurrencyCode, baseCurrencyCode, amount);
        }
        if(exchangeRateDAO.isExists(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode) && exchangeRateDAO.isExists(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode)) {
            return exchangeDAO.getRateWithIntermediate(baseCurrencyCode, targetCurrencyCode, INTERMEDIATE_CURRENCY_CODE, amount);
        }
        throw new ElementNotFoundException();
    }

    public boolean validateAmount(String amount) {
        return amount != null & amount.matches("\\d{1,14}(\\.\\d{1,14})?");
    }

    public boolean isDifferentCurrencies(String from, String to) {
        return from.equals(to);
    }
}
