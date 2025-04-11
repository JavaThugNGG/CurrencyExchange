package currencyExchange.mappers;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.dto.RawExchangeDto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RawExchangeMapper {
    public static RawExchangeDto toDto(ResultSet rs, BigDecimal amount) throws SQLException {
        CurrencyDto baseCurrency = new CurrencyDto(rs.getLong("baseId"),
                rs.getString("baseName"),
                rs.getString("baseCode"),
                rs.getString("baseSign"));
        CurrencyDto targetCurrency = new CurrencyDto(rs.getLong("targetId"),
                rs.getString("targetName"),
                rs.getString("targetCode"),
                rs.getString("targetSign"));
        BigDecimal rate = rs.getBigDecimal("rate");
        return new RawExchangeDto(baseCurrency, targetCurrency, rate, amount);
    }
}
