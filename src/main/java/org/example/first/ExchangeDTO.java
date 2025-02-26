package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
public class ExchangeDTO {
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;

    public ExchangeDTO(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, double rate, double amount, double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public void setTargetCurrency(CurrencyDTO targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setBaseCurrency(CurrencyDTO baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
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

    public double getAmount() {
        return amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }
}
