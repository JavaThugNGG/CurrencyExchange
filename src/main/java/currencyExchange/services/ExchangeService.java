package currencyExchange.services;

import currencyExchange.dao.CurrencyDao;
import currencyExchange.dao.ExchangeRateDao;
import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.RawExchangeDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.dao.ExchangeDao;
import currencyExchange.dto.ExchangeDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeRateDao exchangeRateDAO = new ExchangeRateDao();
    private final ExchangeDao exchangeDAO = new ExchangeDao();
    private final CurrencyDao currencyDAO = new CurrencyDao();
    private final String INTERMEDIATE_CURRENCY_CODE = "USD";


    public ExchangeDto exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        if(isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
            return convertAmountFromStraightRate(rawExchangeDTO, amount);
        }

        if(isReversedRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO = exchangeDAO.getRate(targetCurrencyCode, baseCurrencyCode, amount);
            return convertAmountFromReversedRate(rawExchangeDTO, amount);
        }

        if(isCrossRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDto rawExchangeDTO1 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode, amount);
            RawExchangeDto rawExchangeDTO2 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode, amount);
            BigDecimal rate1 = rawExchangeDTO1.getRate();
            BigDecimal rate2 = rawExchangeDTO2.getRate();

            CurrencyDto baseCurrency = currencyDAO.getCurrency(baseCurrencyCode);
            CurrencyDto targetCurrency = currencyDAO.getCurrency(targetCurrencyCode);

            return convertRateFromCrossRate(baseCurrency, targetCurrency, rate1, rate2, amount);
        }
        throw new ElementNotFoundException();
    }

    public boolean validateAmount(String amount) {
        return amount != null & amount.matches("\\d{1,14}(\\.\\d{1,14})?");
    }

    public boolean isSameCurrencies(String from, String to) {
        return from.equals(to);
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

    private ExchangeDto convertAmountFromStraightRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) throws SQLException {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        return ExchangeDto.parseToExchangeDTO(rawExchangeDTO, rate, amount, roundedConvertedAmount);
    }

    private ExchangeDto convertAmountFromReversedRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) throws SQLException {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal reversedRate = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = reversedRate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        return ExchangeDto.parseToExchangeDTO(rawExchangeDTO, rate, amount, roundedConvertedAmount);
    }

    private ExchangeDto convertRateFromCrossRate(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        BigDecimal inverseRate1 = BigDecimal.ONE.divide(rate1, 8, RoundingMode.HALF_UP);
        BigDecimal inverseRate2 = BigDecimal.ONE.divide(rate2, 8, RoundingMode.HALF_UP);
        BigDecimal rate = inverseRate1.divide(inverseRate2, 8, RoundingMode.HALF_UP);

        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

        return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
    }
}
