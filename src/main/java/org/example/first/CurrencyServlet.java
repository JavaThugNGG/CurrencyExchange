package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = new CurrencyService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String currencyCode = pathInfo.substring(1);
            Currency currency = currencyService.getCurrencyByCode(currencyCode);
            if (currency != null) {
                out.println(objectMapper.writeValueAsString(currency));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(objectMapper.writeValueAsString(Map.of("error", "Currency not found")));
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("error", "No currency code provided")));
        }
    }
}
