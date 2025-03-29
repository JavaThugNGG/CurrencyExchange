package org.example.first.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.SQLException;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
@Data
@AllArgsConstructor
public class ExchangeDTO {
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public static ExchangeDTO parseToExchangeDTO(RawExchangeDTO rawExchangeDTO, BigDecimal rate, BigDecimal amount, BigDecimal roundedConvertedAmount) throws SQLException {
        CurrencyDTO baseCurrency = rawExchangeDTO.getBaseCurrency();
        CurrencyDTO targetCurrency = rawExchangeDTO.getTargetCurrency();
        return new ExchangeDTO(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
    }
}
