package currencyExchange.servlets;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.exceptions.ElementNotFoundException;
import currencyExchange.processors.CurrencyProcessor;
import currencyExchange.utils.Utils;
import currencyExchange.validators.CurrencyValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private final CurrencyProcessor currencyProcessor = new CurrencyProcessor();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        if (!currencyValidator.validatePath(requestPath)) {
            Map<String, String> errorResponse = Map.of("message", "Некорректный URL запроса");
            utils.sendResponse(response, 400, errorResponse);
            return;
        }

        String currencyCode = currencyProcessor.getCurrencyCodeWithoutSlash(requestPath);

        try {
            CurrencyDto currency = currencyService.getCurrency(currencyCode);
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
