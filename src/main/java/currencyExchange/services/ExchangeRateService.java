package currencyExchange.services;

import currencyExchange.dao.ExchangeRateDao;
import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.exceptions.ElementNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import currencyExchange.dao.CurrencyDao;
import currencyExchange.exceptions.ElementAlreadyExistsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDAO = new ExchangeRateDao();
    private final CurrencyDao currencyDAO = new CurrencyDao();

    public ExchangeRateDto getRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return exchangeRateDAO.getRate(baseCurrencyCode, targetCurrencyCode);
    }

    public List<ExchangeRateDto> getAllRates() throws SQLException {
        return exchangeRateDAO.getAllRates();
    }

    public void addRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            throw new ElementAlreadyExistsException();
        }
        if (!currencyDAO.isCurrencyExists(baseCurrencyCode) || !currencyDAO.isCurrencyExists(targetCurrencyCode)) {
            throw new ElementNotFoundException();
        }
        exchangeRateDAO.insertRate(baseCurrencyCode, targetCurrencyCode, rate);
    }

    public void updateRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws SQLException {
        if (exchangeRateDAO.isRateExists(baseCurrencyCode, targetCurrencyCode)) {
            exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
        } else {
            throw new ElementNotFoundException();
        }
    }

    public boolean validatePathForGet(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}$");
    }

    public boolean validatePathForPatch(String path) {
        return path != null && path.matches("^/[A-Z]{3}[A-Z]{3}");
    }

    public String splitBaseCurrency(String path) {
        return path.substring(0, 3);
    }

    public String splitTargetCurrency(String path) {
        return path.substring(3, 6);
    }

    public String getPathWithoutSlash(String path) {
        return path.substring(1);
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
