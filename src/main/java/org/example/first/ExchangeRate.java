package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "baseCurrencyId", "targetCurrencyId", "rate"}) // Указываем порядок
public class ExchangeRate {
    private String id;
    private String baseCurrencyId;
    private String targetCurrencyId;
    private String rate;

    public ExchangeRate(String id, String baseCurrencyId, String targetCurrencyId, String rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBaseCurrencyId(String baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setTargetCurrencyId(String targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public String getId() {
        return id;
    }

    public String getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public String getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public String getRate() {
        return rate;
    }
}




