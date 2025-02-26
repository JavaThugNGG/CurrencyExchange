package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRateDTO {
    private long id;
    CurrencyDTO baseCurrency;
    CurrencyDTO targetCurrency;
    double rate;

    public ExchangeRateDTO(long id, CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }



    public void setId(long id) {
        this.id = id;
    }

    public void setBaseCurrency(CurrencyDTO baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setTargetCurrency(CurrencyDTO targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getId() {
        return id;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }
}

