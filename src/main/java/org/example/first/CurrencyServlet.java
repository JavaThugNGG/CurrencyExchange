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
    private final CurrencyService currencyService = new CurrencyService();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        if (!currencyService.isPathValidated(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный URL запроса");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String currencyCode = currencyService.getCurrencyCodeWithoutSlash(requestPath);

        try {
            CurrencyDTO currency = currencyService.getCurrencyByCode(currencyCode);
            utils.sendResponse(response, 200, currency);
        }
        catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка взаоимодействия с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        }
        catch (ElementNotFoundException e) {
            Map<String, String> errorResponse = Map.of("message", "Запрашиваемая валюта не найдена");
            utils.sendResponse(response, 404, errorResponse);
        }
    }

}
