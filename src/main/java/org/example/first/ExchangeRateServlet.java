package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getMethod().equals("PATCH")) {
            super.service(req, resp);
        } else {
            this.doPatch(req, resp);
        }
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();

        if (!exchangeRateService.isPathValidatedForGet(requestPath)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);       //400
            out.println(objectMapper.writeValueAsString(Map.of("message", "Указан некорректный URL запроса")));
            return;
        }

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        try {
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);          //200
            out.println(objectMapper.writeValueAsString(exchangeRate));

        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);  //404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Запрашиваемый элемент не найден")));

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка при взаимодействии с базой данных")));   //500
        }
    }

    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestPath = request.getPathInfo();

        String path = exchangeRateService.getPathWithoutSlash(requestPath);
        String baseCurrencyCode = exchangeRateService.splitBaseCurrency(path);
        String targetCurrencyCode = exchangeRateService.splitTargetCurrency(path);

        // Чтение тела запроса для извлечения параметров
        StringBuilder requestBody = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        String requestData = requestBody.toString();

        // Преобразуем строку запроса в параметры (если они в формате URL-encoded)
        String rateStr = null;
        for (String param : requestData.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                rateStr = keyValue[1];
            }
        }

        if (!exchangeRateService.isPathValidatedForPatch(requestPath) || !exchangeRateService.validateRate(rateStr)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);    // 400
            out.println(objectMapper.writeValueAsString(Map.of("message", "Отсутствует нужное поле формы или параметр rate некорректный")));
            return;
        }

        // Парсим параметр "rate"
        double rate = Double.parseDouble(rateStr);

        try {
            // Обновление обменного курса
            exchangeRateService.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO updatedRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(objectMapper.writeValueAsString(updatedRate));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            out.println(objectMapper.writeValueAsString(Map.of("message", "Ошибка при взаимодействии с базой данных")));
        } catch (ElementNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
            out.println(objectMapper.writeValueAsString(Map.of("message", "Валютная пара отсутствует в базе данных")));
        }
    }

}