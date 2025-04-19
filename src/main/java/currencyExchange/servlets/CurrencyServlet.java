package currencyExchange.servlets;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.processors.CurrencyProcessor;
import currencyExchange.utils.JsonResponseWriter;
import currencyExchange.validators.CurrencyValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.services.CurrencyService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private final CurrencyProcessor currencyProcessor = new CurrencyProcessor();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPath = request.getPathInfo();

        currencyValidator.validatePath(requestPath);
        String currencyCode = currencyProcessor.getCurrencyCodeWithoutSlash(requestPath);
        CurrencyDto currency = currencyService.getCurrency(currencyCode);
        JsonResponseWriter.sendResponse(response, 200, currency);
    }
}
