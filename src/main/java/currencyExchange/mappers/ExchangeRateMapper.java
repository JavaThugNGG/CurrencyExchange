package currencyExchange.mappers;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.ExchangeRateDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRateMapper {
    public static ExchangeRateDto toDto(ResultSet rs) throws SQLException {
        long id = rs.getLong("rateId");

        CurrencyDto baseCurrency = new CurrencyDto(
                rs.getLong("baseId"),
                rs.getString("baseName"),
                rs.getString("baseCode"),
                rs.getString("baseSign"));

        CurrencyDto targetCurrency = new CurrencyDto(
                rs.getLong("targetId"),
                rs.getString("targetName"),
                rs.getString("targetCode"),
                rs.getString("targetSign"));

        BigDecimal rate = rs.getBigDecimal("rate");

        return new ExchangeRateDto(id, baseCurrency, targetCurrency, rate);
    }
}
