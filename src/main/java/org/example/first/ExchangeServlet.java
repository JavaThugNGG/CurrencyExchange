package org.example.first;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = new ExchangeService();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");

        if (exchangeService.isSameCurrencies(from, to)) {
            Map<String, String> errorResponse = Map.of("message", "Валютная пара должна состоять из разных валют");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        if (!exchangeService.validateAmount(request.getParameter("amount"))) {
            Map<String, String> errorResponse = Map.of("message", "Некорректное значение поля amount");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        BigDecimal amount = new BigDecimal(request.getParameter("amount"));

        try {
            ExchangeDTO exchangeDTO = exchangeService.exchange(from, to, amount);
            utils.sendResponse(response, 200, exchangeDTO);

        } catch (SQLException | ElementNotFoundException e) {
            Map<String, String> errorResponse = Map.of("message", "Запрашиваемая валюта не найдена");
            utils.sendResponse(response, 500, errorResponse);
        }
    }
}
