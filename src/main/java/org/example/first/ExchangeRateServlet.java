package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();

        if (!exchangeRateService.isPathValidatedForGet(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);       //400
            out.println(objectMapper.writeValueAsString(Map.of("message", "Некорректный запрос")));
            return;
        }

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        try {
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);          //200
            out.println(objectMapper.writeValueAsString(exchangeRate));

        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);  //404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Элемент не найден")));

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка в базе данных")));   //500
            e.printStackTrace();
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();
        double rate = Double.parseDouble(request.getParameter("rate"));

        if (!exchangeRateService.isPathValidatedForPatch(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);    // 400
            out.println(objectMapper.writeValueAsString(Map.of("message", "Неверный адрес запроса")));
            return;
        }

        if (rate <= 0.0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.println(objectMapper.writeValueAsString(Map.of("message", "Параметр rate не был передан или он некорректный")));
            return;
        }

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);


        try {
            exchangeRateService.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO updatedRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(objectMapper.writeValueAsString(updatedRate));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500
            e.printStackTrace();
        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Валютная пара отсутствует в базе данных")));
            e.printStackTrace();
        }

    }
}