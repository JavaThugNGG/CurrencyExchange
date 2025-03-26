package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<ExchangeRateDTO> exchangeRates = exchangeRateService.getAllExchangeRates();
            utils.sendResponse(response, 200, exchangeRates);
        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка при взаимодействии с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rateString = request.getParameter("rate");

        if (!exchangeRateService.validateParameters(baseCurrencyCode, targetCurrencyCode, rateString)) {
            Map<String, String> errorResponse = Map.of("message", "Отсутствует обязательное поле запроса или неверный курс обмена");    //400
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            Map<String, String> errorResponse = Map.of("message", "Валютная пара должна состоять из разных валют");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        BigDecimal rate = new BigDecimal(rateString);

        try {
            exchangeRateService.putExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            utils.sendResponse(response, 201, exchangeRate);
        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка при взаимодействии с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        } catch (ElementAlreadyExistsException e) {
            Map<String, String> errorResponse = Map.of("message", "Данная валютная пара уже существует");
            utils.sendResponse(response, 409, errorResponse);
        } catch (ElementNotFoundException e) {
            Map<String, String> errorResponse = Map.of("message", "Одна/обе валюты из валютной пары не существуют в бд");
            utils.sendResponse(response, 404, errorResponse);
        }
    }
}
