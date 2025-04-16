package currencyExchange.processors;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.ExchangeDto;
import currencyExchange.dto.RawExchangeDto;
import currencyExchange.mappers.ExchangeMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ExchangeProcessor {
    public ExchangeDto convertAmountFromStraightRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) throws SQLException {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        return ExchangeMapper.toDto(rawExchangeDTO, rate, amount, roundedConvertedAmount);
    }

    public ExchangeDto convertAmountFromReversedRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) throws SQLException {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal reversedRate = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = reversedRate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        return ExchangeMapper.toDto(rawExchangeDTO, rate, amount, roundedConvertedAmount);
    }

    public ExchangeDto convertRateFromCrossRate(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal rate1, BigDecimal rate2, BigDecimal amount) {
        BigDecimal inverseRate1 = BigDecimal.ONE.divide(rate1, 8, RoundingMode.HALF_UP);
        BigDecimal inverseRate2 = BigDecimal.ONE.divide(rate2, 8, RoundingMode.HALF_UP);
        BigDecimal rate = inverseRate1.divide(inverseRate2, 8, RoundingMode.HALF_UP);

        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

        return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
    }

    public boolean isSameCurrencies(String from, String to) {
        return from.equals(to);
    }
}
