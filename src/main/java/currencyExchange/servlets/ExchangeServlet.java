package currencyExchange.servlets;

import currencyExchange.processors.ExchangeProcessor;
import currencyExchange.validators.ExchangeValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import currencyExchange.dto.ExchangeDto;
import currencyExchange.utils.JsonResponseWriter;
import currencyExchange.services.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService exchangeService = new ExchangeService();
    private final ExchangeValidator exchangeValidator = new ExchangeValidator();
    private final ExchangeProcessor exchangeProcessor = new ExchangeProcessor();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");

        exchangeProcessor.isSameCurrencies(from, to);
        exchangeValidator.validateAmount(request.getParameter("amount"));
        BigDecimal amount = new BigDecimal(request.getParameter("amount"));

        ExchangeDto exchangeDTO = exchangeService.exchange(from, to, amount);
        JsonResponseWriter.sendResponse(response, 200, exchangeDTO);
    }
}
