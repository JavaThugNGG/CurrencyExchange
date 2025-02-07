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
            e.printStackTrace(); // для вывода стека ошибки
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);      //500
            out.println(objectMapper.writeValueAsString(Map.of("error", "Ошибка, связанная с базой данных")));
        }

    }
}
