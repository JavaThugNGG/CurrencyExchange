package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
@Data
@AllArgsConstructor
public class ExchangeRateDTO {
    private long id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private BigDecimal rate;
}

