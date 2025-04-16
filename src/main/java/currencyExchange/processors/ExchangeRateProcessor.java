package currencyExchange.processors;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;

public class ExchangeRateProcessor {
    public String splitBaseCurrency(String path) {
        return path.substring(0, 3);
    }

    public String splitTargetCurrency(String path) {
        return path.substring(3, 6);
    }

    public String getPathWithoutSlash(String path) {
        return path.substring(1);
    }

    public BigDecimal normalizeRate(String rate) {
        String rateWithDot = rate.replace(",", ".");
        return new BigDecimal(rateWithDot);
    }

    public String parseRateForPatch(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String requestData = requestBody.toString();

        String rateString = null;
        for (String param : requestData.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                rateString = keyValue[1];
            }
        }
        return rateString;
    }
}
