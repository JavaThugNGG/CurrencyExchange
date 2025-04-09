package currencyExchange.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.SQLException;

@JsonPropertyOrder({"baseCurrency", "targetCurrency", "rate", "amount", "convertedAmount"})
@Data
@AllArgsConstructor
public class ExchangeDto {
    private CurrencyDto baseCurrency;
    private CurrencyDto targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;

    public static ExchangeDto parseToExchangeDTO(RawExchangeDto rawExchangeDTO, BigDecimal rate, BigDecimal amount, BigDecimal roundedConvertedAmount) throws SQLException {
        CurrencyDto baseCurrency = rawExchangeDTO.getBaseCurrency();
        CurrencyDto targetCurrency = rawExchangeDTO.getTargetCurrency();
        return new ExchangeDto(baseCurrency, targetCurrency, rate, amount, roundedConvertedAmount);
    }
}
