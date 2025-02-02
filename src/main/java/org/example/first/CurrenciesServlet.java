package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = new CurrencyService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        List<Currency> currencies = currencyService.getAllCurrencies();
        out.println(objectMapper.writeValueAsString(currencies));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        try {
            String name = request.getParameter("name");
            String code = request.getParameter("code");
            String sign = request.getParameter("sign");

            // Используем сервис для создания валюты
            Currency currency = currencyService.createCurrency(name, code, sign);

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            PrintWriter out = response.getWriter();
            out.println(objectMapper.writeValueAsString(currency));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (CurrencyConflictException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"error\": \"Ошибка сервера: " + e.getMessage() + "\"}");
        }
    }

}
