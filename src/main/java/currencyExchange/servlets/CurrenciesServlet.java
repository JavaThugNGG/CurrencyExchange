package currencyExchange.servlets;

import currencyExchange.dto.CurrencyDto;
import currencyExchange.mappers.CurrencyMapper;
import currencyExchange.validators.CurrencyValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.exceptions.ElementAlreadyExistsException;
import currencyExchange.utils.Utils;
import currencyExchange.services.CurrencyService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private final Utils utils = new Utils();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CurrencyDto> currencies = currencyService.getAllCurrencies();
        utils.sendResponse(response, 200, currencies);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        currencyValidator.validateParameters(code, name, sign);
        CurrencyDto currency = currencyService.addCurrency(name, code, sign);
        utils.sendResponse(response, 201, currency);
    }
}
