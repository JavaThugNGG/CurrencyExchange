package currencyExchange.validators;

import java.sql.SQLException;

public class ExchangeValidator {
    public boolean validateAmount(String amount) {
        return amount != null & amount.matches("\\d{1,14}(\\.\\d{1,14})?");
    }
}
