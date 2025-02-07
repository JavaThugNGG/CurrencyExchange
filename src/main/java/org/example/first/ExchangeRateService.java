package org.example.first;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return exchangeRateDAO.getAll();
    }
}
