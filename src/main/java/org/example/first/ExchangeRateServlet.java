package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final CurrencyService currencyService = new CurrencyService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();

        if (!exchangeRateService.isPathValidated(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);       //400
            out.println(objectMapper.writeValueAsString(Map.of("error", "Некорректный запрос")));
            return;
        }

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        try {
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);          //200
            out.println(objectMapper.writeValueAsString(exchangeRate));

        } catch (SQLException e) {
            e.printStackTrace(); // для вывода стека ошибки
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(objectMapper.writeValueAsString(Map.of("error", "Ошибка в базе данных")));
        }
    }
}