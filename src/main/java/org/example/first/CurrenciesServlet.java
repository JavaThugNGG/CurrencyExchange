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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = new CurrencyService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            response.setStatus(HttpServletResponse.SC_OK); // 200
            out.println(objectMapper.writeValueAsString(currencies));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            Map<String, String> errorResponse = Map.of("message", "Ошибка при получении валюты из базы данных");
            out.println(objectMapper.writeValueAsString(errorResponse));
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        if (!currencyService.isParametersValidated(name, code, sign)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("error", "Некорректные аргументы для добавления валюты"))); //400
            return;
        }

        try {
            CurrencyDTO currency = currencyService.createCurrency(name, code, sign);
            response.setStatus(HttpServletResponse.SC_CREATED);                      // 201 Created
            out.println(objectMapper.writeValueAsString(currency));
        } catch (ElementAlreadyExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);                      //409 already exists
            out.println(objectMapper.writeValueAsString(Map.of("error", "Данная валюте уже существует в базе данных.")));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);        // 500 Internal Server Error
            out.println(objectMapper.writeValueAsString(Map.of("error","Ошибка взаимодействия с базой данных")));
        }
    }
}
