package currencyExchange.validators;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeRateValidator {
    public void validatePathForGet(String path) {
        if ((path == null) || (!path.matches("^/[A-Z]{3}[A-Z]{3}$"))) {
            throw new IllegalArgumentException("Некорректный URL запроса");
        }
    }

    public void validatePathForPatch(String path) {
        if ((path == null) || (!path.matches("^/[A-Z]{3}[A-Z]{3}"))) {
            throw new IllegalArgumentException("Отсутствует нужное поле формы или некорректные параметры");
        }
    }

    public void validateParameters(String baseCode, String targetCode, String rate) {
        if ((baseCode == null) || (targetCode == null) || (!baseCode.matches("([A-Za-z]{1,3})")) || (!targetCode.matches("([A-Za-z]{1,3})"))) {
            throw new IllegalArgumentException("Отсутсвуют необходимые парамметры, либо они некорректные");
        }
        validateRate(rate);
    }

    public void validateRate(String rate) {
        if (rate == null) {
            throw new IllegalArgumentException("Параметр rate некорректный");
        }
        String normalizedRate = rate.replace(",", ".");
        if (!normalizedRate.matches("\\d+(\\.\\d{1,7})?") || !(new BigDecimal(normalizedRate).compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("Параметр rate некорректный");
        }
    }
}
