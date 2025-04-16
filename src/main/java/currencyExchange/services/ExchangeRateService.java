package currencyExchange.services;

import currencyExchange.dao.ExchangeRateDao;
import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.exceptions.ElementNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import currencyExchange.dao.CurrencyDao;
import currencyExchange.exceptions.ElementAlreadyExistsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDAO = new ExchangeRateDao();
    private final CurrencyDao currencyDAO = new CurrencyDao();

    public ExchangeRateDto getRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.getRate(baseCurrencyCode, targetCurrencyCode);
    }

    public List<ExchangeRateDto> getAllRates() throws SQLException {
        return exchangeRateDAO.getAllRates();
    }

    public void addRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            throw new ElementAlreadyExistsException();
        }
        if (!currencyDAO.isCurrencyExists(baseCurrencyCode) || !currencyDAO.isCurrencyExists(targetCurrencyCode)) {
            throw new ElementNotFoundException();
        }
        exchangeRateDAO.insertRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
        } else {
            throw new ElementNotFoundException();
        }
    }
}
