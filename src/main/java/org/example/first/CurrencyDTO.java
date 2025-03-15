package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonPropertyOrder({"id", "name", "code", "sign"}) // Указываем порядок
@Data
@AllArgsConstructor
public class CurrencyDTO {
    private long id;
    private String name;
    private String code;
    private String sign;
}
