package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;


@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
@Data
@AllArgsConstructor
public class ExchangeRateDTO {
    private long id;
    CurrencyDTO baseCurrency;
    CurrencyDTO targetCurrency;
    double rate;
}

