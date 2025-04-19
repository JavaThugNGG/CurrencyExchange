package currencyExchange.services;

import currencyExchange.dao.CurrencyDao;
import currencyExchange.dto.CurrencyDto;
import currencyExchange.exceptions.ElementAlreadyExistsException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDAO = new CurrencyDao();

    public CurrencyDto getCurrency(String code) {
        return currencyDAO.getCurrency(code);
    }

    public List<CurrencyDto> getAllCurrencies() {
        return currencyDAO.getAllCurrencies();
    }

    public CurrencyDto addCurrency(String fullName, String code, String sign) {
        if (currencyDAO.isCurrencyExists(code)) {
            throw new ElementAlreadyExistsException("Данная валюта уже существует");
        }
            long id = currencyDAO.insertCurrency(fullName, code, sign);
            return new CurrencyDto(id, fullName, code, sign);
    }
}

