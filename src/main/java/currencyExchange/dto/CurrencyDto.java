package currencyExchange.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonPropertyOrder({"id", "name", "code", "sign"})
@Data
@AllArgsConstructor
public class CurrencyDto {
    private long id;
    private String name;
    private String code;
    private String sign;
}

