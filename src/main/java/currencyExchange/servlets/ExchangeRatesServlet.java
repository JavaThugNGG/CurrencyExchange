package currencyExchange.servlets;

import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.processors.ExchangeProcessor;
import currencyExchange.processors.ExchangeRateProcessor;
import currencyExchange.utils.Utils;
import currencyExchange.validators.ExchangeRateValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.exceptions.ElementAlreadyExistsException;
import currencyExchange.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final ExchangeRateProcessor exchangeRateProcessor = new ExchangeRateProcessor();
    private final ExchangeProcessor exchangeProcessor = new ExchangeProcessor();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<ExchangeRateDto> exchangeRates = exchangeRateService.getAllRates();
        utils.sendResponse(response, 200, exchangeRates);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rateString = request.getParameter("rate");

        exchangeRateValidator.validateParameters(baseCurrencyCode, targetCurrencyCode, rateString);

        exchangeProcessor.isSameCurrencies(baseCurrencyCode, targetCurrencyCode);

        BigDecimal rate = exchangeRateProcessor.normalizeRate(rateString);

        exchangeRateService.addRate(baseCurrencyCode, targetCurrencyCode, rate);
        ExchangeRateDto exchangeRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
        utils.sendResponse(response, 201, exchangeRate);
    }
}
