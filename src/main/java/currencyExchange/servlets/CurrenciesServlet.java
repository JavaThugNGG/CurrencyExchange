package currencyExchange.servlets;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.validators.CurrencyValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.utils.JsonResponseWriter;
import currencyExchange.services.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();//
    private final CurrencyValidator currencyValidator = new CurrencyValidator();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        JsonResponseWriter.sendResponse(response, 200, currencies);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        currencyValidator.validateParameters(code, name, sign);
        CurrencyDto currency = currencyService.addCurrency(name, code, sign);
        JsonResponseWriter.sendResponse(response, 201, currency);
    }
}
