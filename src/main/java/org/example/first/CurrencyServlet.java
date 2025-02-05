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

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = new CurrencyService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();

        if (!currencyService.validatePath(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println(objectMapper.writeValueAsString(Map.of("error", "Код валюты отсутствует в адресе. Пример запроса: currency/RUB"))); //400
            return;
        }

        String currencyCode = getCurrencyCodeWithoutSlash(requestPath);
        try {
            Currency currency = currencyService.getCurrencyByCode(currencyCode);
            response.setStatus(HttpServletResponse.SC_OK);                                                          //200
            out.println(objectMapper.writeValueAsString(currency));
        }
        catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(objectMapper.writeValueAsString(Map.of("error", "Ошибка на уровне базы данных")));   //500
        }
        catch (CurrencyNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(objectMapper.writeValueAsString(Map.of("error", "Запрашиваемая валюта не найдена")));  //404
        }
    }

    private String getCurrencyCodeWithoutSlash(String currencyCode) {
        return currencyCode.substring(1);
    }
}
