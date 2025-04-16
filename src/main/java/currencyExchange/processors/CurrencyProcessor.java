package currencyExchange.processors;

public class CurrencyProcessor {
    public String getCurrencyCodeWithoutSlash(String currencyCode) {
        return currencyCode.substring(1);
    }
}
