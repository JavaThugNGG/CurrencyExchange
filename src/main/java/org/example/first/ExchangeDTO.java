package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
@Data
@AllArgsConstructor
public class ExchangeDTO {
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}
