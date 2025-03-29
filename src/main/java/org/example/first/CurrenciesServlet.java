package org.example.first;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            utils.sendResponse(response, 200, currencies);
        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message", "Ошибка при взаимодействии с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        if (!currencyService.validateCode(code)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный аргумент code для добавления валюты");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        if (!currencyService.validateName(name)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный аргумент name для добавления валюты");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        if (!currencyService.validateSign(sign)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный аргумент sign для добавления валюты");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        try {
            CurrencyDTO currency = currencyService.addCurrency(name, code, sign);
            utils.sendResponse(response, 201, currency);
        } catch (ElementAlreadyExistsException e) {
            Map<String, String> errorResponse = Map.of("message", "Данная валюта уже существует");
            utils.sendResponse(response, 409, errorResponse);
        } catch (SQLException e) {
            Map<String, String> errorResponse = Map.of("message","Ошибка взаимодействия с базой данных");
            utils.sendResponse(response, 500, errorResponse);
        }
    }
}
