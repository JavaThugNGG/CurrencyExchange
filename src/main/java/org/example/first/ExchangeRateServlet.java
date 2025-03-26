package org.example.first;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
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

        if (!exchangeRateService.validatePathForGet(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Указан некорректный URL запроса");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        try {
            ExchangeRateDTO exchangeRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
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

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        if (!exchangeRateService.validatePathForPatch(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Отсутствует нужное поле формы");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }


        //вот это вынесу в отдельный метод!!!!
        // Чтение тела запроса для извлечения параметров
        StringBuilder requestBody = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String requestData = requestBody.toString();

        // Преобразуем строку запроса в параметры (если они в формате URL-encoded)
        String rateString = null;
        for (String param : requestData.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                rateString = keyValue[1];
            }
        }

        if (!exchangeRateService.validateRate(rateString)) {
            Map<String, String> errorResponse = Map.of("message", "параметр rate некорректный");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        BigDecimal rate = new BigDecimal(rateString);

        try {
            exchangeRateService.updateRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO updatedRate = exchangeRateService.getRate(baseCurrencyCode, targetCurrencyCode);
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