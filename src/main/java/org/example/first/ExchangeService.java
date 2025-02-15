package org.example.first;

import java.sql.SQLException;

public class ExchangeService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final ExchangeDAO exchangeDAO = new ExchangeDAO();


    //если есть курс A->B - взять из бд курс, вернуть его, расчитать кол-во переведенной валюты
    //если есть курс B->A - перевернуть курс, переворачиваем его и возвращаем, расчитываем кол-во переведенной валюты
    //ищем usd-A и usd-B, вычисляем из этих курсов курс AB

    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, String amount) throws SQLException {
        if(exchangeRateDAO.isExists(baseCurrencyCode, targetCurrencyCode)) {
            return exchangeDAO.getRate(baseCurrencyCode, targetCurrencyCode, amount);
        } else {
            throw new ElementNotFoundException();
        }
    }
}
