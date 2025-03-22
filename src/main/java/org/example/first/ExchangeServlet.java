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

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = new ExchangeService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String from = request.getParameter("from");
        String to = request.getParameter("to");

        if (exchangeService.isDifferentCurrencies(from, to)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "В валютной паре не могут быть две одинаковые валюты")));
            return;
        }

        if (!exchangeService.validateAmount(request.getParameter("amount"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Некорректно указано поле amount")));
            return;
        }

        double amount = Double.parseDouble(request.getParameter("amount"));

        try {
            ExchangeDTO exchangeDTO = exchangeService.exchange(from, to, amount);
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(objectMapper.writeValueAsString(exchangeDTO));

        } catch (SQLException | ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Валюта не найдена")));
        }
    }
}
