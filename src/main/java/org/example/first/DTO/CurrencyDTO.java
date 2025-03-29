package org.example.first.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@JsonPropertyOrder({"id", "name", "code", "sign"})
@Data
@AllArgsConstructor
public class CurrencyDTO {
    private long id;
    private String name;
    private String code;
    private String sign;

    public static CurrencyDTO parseToCurrencyDTO(ResultSet rs) throws SQLException {
        return new CurrencyDTO(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("code"),
                rs.getString("sign")
        );
    }
}

