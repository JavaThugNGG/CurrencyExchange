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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            List<ExchangeRateDTO> exchangeRates = exchangeRateService.getAllExchangeRates();
            response.setStatus(HttpServletResponse.SC_OK);                                    //200
            out.println(objectMapper.writeValueAsString(exchangeRates));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);      //500
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка при взаимодействии с базой данных")));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rateString = request.getParameter("rate");

        if (!exchangeRateService.validateParameters(baseCurrencyCode, targetCurrencyCode, rateString)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Отсутствует обязательное поле запроса или неверный курс обмена")));    //400
            return;
        }

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Валютная пара должна состоять из разных валют")));
            return;
        }

        BigDecimal rate = new BigDecimal(rateString);

        try {
            exchangeRateService.putExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_CREATED);                     //201
            out.println(objectMapper.writeValueAsString(exchangeRate));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                                  //500
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка при взаимодействии с базой данных")));
        } catch (ElementAlreadyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);                              //409
            out.println(objectMapper.writeValueAsString(Map.of("message", "Данная валютная пара уже существует")));
        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);       //404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Одна/обе валюты из валютной пары не существуют в бд")));
        }
    }
}
