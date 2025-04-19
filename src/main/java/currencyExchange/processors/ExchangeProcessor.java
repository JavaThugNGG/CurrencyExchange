package currencyExchange.processors;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.ExchangeDto;
import currencyExchange.dto.RawExchangeDto;
import currencyExchange.mappers.ExchangeMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class ExchangeProcessor {
    public ExchangeDto convertAmountFromStraightRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        try {
            return ExchangeMapper.toDto(rawExchangeDTO, rate, amount, roundedConvertedAmount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeDto convertAmountFromReversedRate(RawExchangeDto rawExchangeDTO, BigDecimal amount) {
        BigDecimal rate = rawExchangeDTO.getRate();
        BigDecimal reversedRate = BigDecimal.ONE.divide(rate, 8, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = reversedRate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);
        try {
            return ExchangeMapper.toDto(rawExchangeDTO, rate, amount, roundedConvertedAmount);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ExchangeDto convertRateFromCrossRate(CurrencyDto baseCurrency, CurrencyDto targetCurrency, BigDecimal baseRate, BigDecimal targetRate, BigDecimal amount) {
        BigDecimal inverseBaseRate = BigDecimal.ONE.divide(baseRate, 8, RoundingMode.HALF_UP);
        BigDecimal inverseTargetRate = BigDecimal.ONE.divide(targetRate, 8, RoundingMode.HALF_UP);
        BigDecimal rate = inverseBaseRate.divide(inverseTargetRate, 8, RoundingMode.HALF_UP);

        BigDecimal convertedAmount = rate.multiply(amount);
        BigDecimal roundedConvertedAmount = convertedAmount.setScale(2, RoundingMode.HALF_UP);

        return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
    }

    public void isSameCurrencies(String from, String to) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Валютная пара должна состоять из разных валют");
        }
    }
}
