package currencyExchange.validators;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeRateValidator {
    public boolean validatePathForGet(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}$");
    }

    public boolean validatePathForPatch(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}");
    }

    public boolean validateParameters(String baseCode, String targetCode, String rate) {
        return baseCode != null & targetCode != null && baseCode.matches("([A-Za-z]{1,3})") && targetCode.matches("([A-Za-z]{1,3})") && validateRate(rate);
    }

    public boolean validateRate(String rate) {
        if (rate == null) {
            return false;
        }
        String normalizedRate = rate.replace(",", ".");
        return normalizedRate.matches("\\d+(\\.\\d{1,7})?") && new BigDecimal(normalizedRate).compareTo(BigDecimal.ZERO) > 0;
    }
}
