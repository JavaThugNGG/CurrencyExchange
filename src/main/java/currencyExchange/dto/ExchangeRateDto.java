package currencyExchange.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
@Data
@AllArgsConstructor
public class ExchangeRateDto {
    private long id;
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;

    public static ExchangeRateDto parseToExchangeRateDTO(ResultSet rs) throws SQLException {
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
