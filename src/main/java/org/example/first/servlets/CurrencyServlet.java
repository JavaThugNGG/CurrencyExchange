package org.example.first.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.first.DTO.CurrencyDTO;
import org.example.first.exceptions.ElementNotFoundException;
import org.example.first.utils.Utils;
import org.example.first.services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        if (!currencyService.validatePath(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный URL запроса");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String currencyCode = currencyService.getCurrencyCodeWithoutSlash(requestPath);

        try {
            CurrencyDTO currency = currencyService.getCurrency(currencyCode);
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
