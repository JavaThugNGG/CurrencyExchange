package org.example.first;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final ExchangeDAO exchangeDAO = new ExchangeDAO();
    private final String INTERMEDIATE_CURRENCY_CODE = "USD";


    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws SQLException {
        if(exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            RawExchangeDTO rawExchangeDTO = exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
            BigDecimal rate = rawExchangeDTO.getRate();
            BigDecimal convertedAmount = rate.multiply(amount);
            BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

            return ExchangeDTO.parseToExchangeDTO(rawExchangeDTO, rate, amount, roundedConvertedAmount);
        }
        if(exchangeRateDAO.isRateExists(targetCurrencyCode, baseCurrencyCode)) {                                  //тут подумать читаемо ли это (этот свап)
            RawExchangeDTO rawExchangeDTO = exchangeDAO.getRate(targetCurrencyCode, baseCurrencyCode, amount);
            BigDecimal rate = rawExchangeDTO.getRate();
            BigDecimal reversedRate = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = reversedRate.multiply(amount);
            BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

            return ExchangeDTO.parseToExchangeDTO(rawExchangeDTO, rate, amount, roundedConvertedAmount);
        }
        if(exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode) && exchangeRateDAO.isRateExists(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode)) {
            BigDecimal rate1 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, baseCurrencyCode);
            BigDecimal rate2 = exchangeDAO.getRate(INTERMEDIATE_CURRENCY_CODE, targetCurrencyCode);

            CurrencyDTO baseCurrency = exchangeDAO.getCurrencyDetails(baseCurrencyCode);
            CurrencyDTO targetCurrency = exchangeDAO.getCurrencyDetails(targetCurrencyCode);

            BigDecimal tempRate1 = BigDecimal.ONE.divide(rate1, 8, RoundingMode.HALF_UP);
            BigDecimal tempRate2 = BigDecimal.ONE.divide(rate2, 8, RoundingMode.HALF_UP);


            BigDecimal rate = tempRate1.divide(tempRate2, 8, RoundingMode.HALF_UP);
            BigDecimal convertedAmount = rate.multiply(amount);
            BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

            return new ExchangeDTO(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
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
