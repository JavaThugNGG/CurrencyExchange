package currencyExchange.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount"})
@Data
@AllArgsConstructor
public class RawExchangeDTO {
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;

    public static RawExchangeDTO parseToRawExchangeDTO(ResultSet rs, BigDecimal amount) throws SQLException {
        CurrencyDTO baseCurrency = new CurrencyDTO(rs.getLong("baseId"),
                rs.getString("baseName"),
                rs.getString("baseCode"),
                rs.getString("baseSign"));
        CurrencyDTO targetCurrency = new CurrencyDTO(rs.getLong("targetId"),
                rs.getString("targetName"),
                rs.getString("targetCode"),
                rs.getString("targetSign"));
        BigDecimal rate = rs.getBigDecimal("rate");
        return new RawExchangeDTO(baseCurrency, targetCurrency, rate, amount);
    }
}
