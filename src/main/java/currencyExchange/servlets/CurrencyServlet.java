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

        currencyValidator.validatePath(requestPath);
        String currencyCode = currencyProcessor.getCurrencyCodeWithoutSlash(requestPath);
        CurrencyDto currency = currencyService.getCurrency(currencyCode);
        utils.sendResponse(response, 200, currency);
    }
}
