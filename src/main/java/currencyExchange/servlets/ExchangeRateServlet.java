package currencyExchange.servlets;

import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.processors.ExchangeRateProcessor;
import currencyExchange.utils.JsonResponseWriter;
import currencyExchange.validators.ExchangeRateValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final ExchangeRateProcessor exchangeRateProcessor = new ExchangeRateProcessor();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getMethod().equals("PATCH")) {
            super.service(req, resp);
        } else {
            this.doPatch(req, resp);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        exchangeRateValidator.validatePathForGet(requestPath);

        String path = exchangeRateProcessor.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateProcessor.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateProcessor.splitTargetCurrency(path);

        ExchangeRateDto exchangeRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
        JsonResponseWriter.sendResponse(response, 200, exchangeRate);   //не забудь вот это везде позасылать
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        String path = exchangeRateProcessor.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateProcessor.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateProcessor.splitTargetCurrency(path);

        exchangeRateValidator.validatePathForPatch(requestPath);

        String rateString = exchangeRateProcessor.parseRateForPatch(request);

        exchangeRateValidator.validateRate(rateString);

        BigDecimal rate = exchangeRateProcessor.normalizeRate(rateString);

        exchangeRateService.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
        ExchangeRateDto updatedRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
        JsonResponseWriter.sendResponse(response, 200, updatedRate);
    }
}