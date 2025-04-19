package currencyExchange.validators;

import java.sql.SQLException;

public class ExchangeValidator {
    public void validateAmount(String amount) {
        if ((amount == null) || (!amount.matches("\\d{1,14}(\\.\\d{1,14})?"))) {
            throw new IllegalArgumentException("Некорректное значение поля amount");
        }
    }
}
