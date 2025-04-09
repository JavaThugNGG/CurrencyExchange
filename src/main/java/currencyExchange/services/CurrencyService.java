package currencyExchange.services;

import currencyExchange.dao.CurrencyDao;
import currencyExchange.dto.CurrencyDto;
import currencyExchange.exceptions.ElementAlreadyExistsException;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDAO = new CurrencyDao();

    public CurrencyDto getCurrency(String code) throws SQLException {
        return currencyDAO.getCurrency(code);
    }

    public List<CurrencyDto> getAllCurrencies() throws SQLException {
        return currencyDAO.getAllCurrencies();
    }

    public CurrencyDto addCurrency(String fullName, String code, String sign) throws SQLException {
        if (currencyDAO.isCurrencyExists(code)) {
            throw new ElementAlreadyExistsException();
        }
            long id = currencyDAO.insertCurrency(fullName, code, sign);
            return new CurrencyDto(id, fullName, code, sign);
    }

    public boolean validatePath(String path) {
        return path != null && path.matches("^/[A-Z]{3}$");
    }

    public boolean validateCode(String code) {
        return code != null & code.matches("([A-Za-z]{1,3})");
    }

    public boolean validateName(String name) {
        return name != null & name.matches("([A-Za-zА]{1,8})( [A-Za-zА]{1,10}){0,2}");
    }

    public boolean validateSign(String sign) {
        return sign != null && sign.matches("\\S");
    }

    public String getCurrencyCodeWithoutSlash(String currencyCode) {
        return currencyCode.substring(1);
    }
}

