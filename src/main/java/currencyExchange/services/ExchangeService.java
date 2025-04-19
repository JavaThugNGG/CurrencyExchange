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

public class ExchangeService {
    private final ExchangeProcessor exchangeProcessor = new ExchangeProcessor();
    private final ExchangeRateDao exchangeRateDAO = new ExchangeRateDao();
    private final ExchangeDao exchangeDAO = new ExchangeDao();
    private static final String INTERMEDIATE_CURRENCY_CODE = "USD";

    public ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        if(isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
            return exchangeProcessor.convertAmountFromStraightRate(rawExchangeDTO, amount);
        }

        if(isReversedRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(targetCurrencyCode, baseCurrencyCode, amount);
            return exchangeProcessor.convertAmountFromReversedRate(rawExchangeDTO, amount);
        }

        if(isCrossRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto base = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode, amount);
            RawExchangeDto target = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode, amount);
            BigDecimal baseRate = base.getRate();
            BigDecimal targetRate = target.getRate();
            CurrencyDto baseCurrency = base.getBaseCurrency();
            CurrencyDto targetCurrency = target.getBaseCurrency();
            return exchangeProcessor.convertRateFromCrossRate(baseCurrency, targetCurrency, baseRate, targetRate, amount);
        }
        throw new ElementNotFoundException("Запрашиваемая валюта не найдена");
    }

    public boolean isRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode);
    }

    public boolean isReversedRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDAO.isRateExists(targetCurrencyCode, baseCurrencyCode);
    }

    public boolean isCrossRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode) && exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode);
    }
}
