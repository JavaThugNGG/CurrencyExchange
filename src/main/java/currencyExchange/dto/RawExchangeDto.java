package currencyExchange.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount"})
@Data
@AllArgsConstructor
public class RawExchangeDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;

    public static RawExchangeDto parseToRawExchangeDTO(ResultSet rs, BigDecimal amount) throws SQLException {
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
