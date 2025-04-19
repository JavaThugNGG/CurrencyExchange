package currencyExchange.mappers;

import currencyExchange.dto.CurrencyDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CurrencyMapper {
    public static CurrencyDto toDto(ResultSet rs) throws SQLException {
        return new CurrencyDto(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("code"),
                rs.getString("sign")
        );
    }
}
