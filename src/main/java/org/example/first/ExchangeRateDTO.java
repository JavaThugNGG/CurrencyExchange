package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
@Data
@AllArgsConstructor
public class ExchangeRateDTO {
    private long id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;

    static ExchangeRateDTO parseToExchangeRateDTO(ResultSet rs) throws SQLException {
        long id = rs.getLong("rateId");

        CurrencyDTO baseCurrency = new CurrencyDTO(
                rs.getLong("baseId"),
                rs.getString("baseName"),
                rs.getString("baseCode"),
                rs.getString("baseSign"));

        CurrencyDTO targetCurrency = new CurrencyDTO(
                rs.getLong("targetId"),
                rs.getString("targetName"),
                rs.getString("targetCode"),
                rs.getString("targetSign"));

        BigDecimal rate = rs.getBigDecimal("rate");

        return new ExchangeRateDTO(id, baseCurrency, targetCurrency, rate);
    }
}
