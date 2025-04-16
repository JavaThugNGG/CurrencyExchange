package currencyExchange.servlets;

import currencyExchange.dto.ExchangeRateDto;
import currencyExchange.processors.ExchangeRateProcessor;
import currencyExchange.utils.Utils;
import currencyExchange.validators.ExchangeRateValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final ExchangeRateProcessor exchangeRateProcessor = new ExchangeRateProcessor();
    private final Utils utils = new Utils();

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

        if (!exchangeRateValidator.validatePathForGet(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный URL запроса");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String path = exchangeRateProcessor.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateProcessor.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateProcessor.splitTargetCurrency(path);

        try {
            ExchangeRateDto exchangeRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
            utils.sendResponse(response, 200, exchangeRate);

        } catch (ElementNotFoundException e) {
            Map<String, String> errorResponse = Map.of("message", "Запрашиваемый элемент не найден");
            utils.sendResponse(response, 404, errorResponse);

        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка при взаимодействии с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        String path = exchangeRateProcessor.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateProcessor.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateProcessor.splitTargetCurrency(path);

        if (!exchangeRateValidator.validatePathForPatch(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Отсутствует нужное поле формы или некорректные параметры");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String rateString = exchangeRateProcessor.parseRateForPatch(request);

        if (!exchangeRateValidator.validateRate(rateString)) {
            Map<String, String> errorResponse = Map.of("message", "параметр rate некорректный");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        BigDecimal rate = exchangeRateProcessor.normalizeRate(rateString);

        try {
            exchangeRateService.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDto updatedRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
            utils.sendResponse(response, 200, updatedRate);
        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка при взаимодействии с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        } catch (ElementNotFoundException e) {
            Map<String, String> errorResponse = Map.of("message", "Валютная пара отсутствует в базе данных");
            utils.sendResponse(response, 404, errorResponse);
        }
    }
}