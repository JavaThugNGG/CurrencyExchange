package org.example.first;

import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final ExchangeDAO exchangeDAO = new ExchangeDAO();
    private final String intermediateCurrencyCode = "USD";


    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException {
        if(exchangeRateDAO.isExists(baseCurrencyCode, targetCurrencyCode)) {
            return exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
        } else {
            if(exchangeRateDAO.isExists(targetCurrencyCode, baseCurrencyCode)) {                 //наверное лучше сделать переименованный метод
                return exchangeDAO.getRateFromReverseRate(targetCurrencyCode, baseCurrencyCode, amount);
            } else {
                if(exchangeRateDAO.isExists(intermediateCurrencyCode, baseCurrencyCode) && exchangeRateDAO.isExists(intermediateCurrencyCode, targetCurrencyCode)) {
                    return exchangeDAO.getRateWithIntermediate(baseCurrencyCode, targetCurrencyCode, intermediateCurrencyCode, amount);
                }
            }
        } throw new SQLException();
    }
}
