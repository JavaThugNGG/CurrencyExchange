package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
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
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка, связанная с базой данных")));
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rateString  = request.getParameter("rate");

        if (!exchangeRateService.validateParameters(baseCurrencyCode, targetCurrencyCode, rateString)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Отсутствует нужное поле формы или введен некорректный курс")));    //400
            return;
        }

        double rate = Double.parseDouble(rateString);

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "В паре валют не могут быть две одинаковые валюты")));
            return;
        }

        try {
            exchangeRateService.putExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_CREATED);                     //201
            out.println(objectMapper.writeValueAsString(exchangeRate));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                                  //500
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка на уровне базы данных")));
        } catch (ElementAlreadyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);                              //409
            out.println(objectMapper.writeValueAsString(Map.of("message", "Валютная пара с таким кодом уже существует")));
        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);       //404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Одна/обе валютные пары не существуют в бд")));
        }
    }
}
