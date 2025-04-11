package currencyExchange.mappers;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.ExchangeDto;
import currencyExchange.dto.RawExchangeDto;

import java.math.BigDecimal;
import java.sql.SQLException;

public class ExchangeMapper {
    public static ExchangeDto toDto(RawExchangeDto rawExchangeDTO, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) throws SQLException {
        CurrencyDto baseCurrency = rawExchangeDTO.getBaseCurrency();
        CurrencyDto targetCurrency = rawExchangeDTO.getTargetCurrency();
        return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, convertedAmount);
    }
}