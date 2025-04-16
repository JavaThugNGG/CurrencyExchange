package currencyExchange.services;

import currencyExchange.dao.CurrencyDao;
import currencyExchange.dao.ExchangeRateDao;
import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.RawExchangeDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.dao.ExchangeDao;
import currencyExchange.dto.ExchangeDto;
import currencyExchange.processors.ExchangeProcessor;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeProcessor exchangeProcessor = new ExchangeProcessor();
    private final ExchangeRateDao exchangeRateDAO = new ExchangeRateDao();
    private final ExchangeDao exchangeDAO = new ExchangeDao();
    private final CurrencyDao currencyDAO = new CurrencyDao();
    private final String INTERMEDIATE_CURRENCY_CODE = "USD";

    public ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        if(isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
            return exchangeProcessor.convertAmountFromStraightRate(rawExchangeDTO, amount);
        }

        if(isReversedRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(targetCurrencyCode, baseCurrencyCode, amount);
            return exchangeProcessor.convertAmountFromReversedRate(rawExchangeDTO, amount);
        }

        if(isCrossRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO1 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode, amount);
            RawExchangeDto rawExchangeDTO2 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode, amount);
            BigDecimal rate1 = rawExchangeDTO1.getRate();
            BigDecimal rate2 = rawExchangeDTO2.getRate();

            CurrencyDto baseCurrency = currencyDAO.getCurrency(baseCurrencyCode);
            CurrencyDto targetCurrency = currencyDAO.getCurrency(targetCurrencyCode);

            return exchangeProcessor.convertRateFromCrossRate(baseCurrency, targetCurrency, rate1, rate2, amount);
        }
        throw new ElementNotFoundException();
    }

    public boolean isRateExists(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode);
    }

    public boolean isReversedRateExists(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.isRateExists(targetCurrencyCode, baseCurrencyCode);
    }

    public boolean isCrossRateExists(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode) && exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode);
    }
}
