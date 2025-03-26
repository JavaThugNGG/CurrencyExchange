package org.example.first;


import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public CurrencyDTO getCurrency(String code) throws SQLException {
        return currencyDAO.getCurrency(code);
    }

    public List<CurrencyDTO> getAllCurrencies() throws SQLException {
        return currencyDAO.getAllCurrencies();
    }

    public CurrencyDTO addCurrency(String fullName, String code, String sign) throws SQLException {
        if (currencyDAO.isCurrencyExists(code)) {
            throw new ElementAlreadyExistsException();
        }
            long id = currencyDAO.insertCurrency(fullName, code, sign);
            return new CurrencyDTO(id, fullName, code, sign);
    }

    public boolean validatePath(String path) {
        return path != null && path.matches("^/[A-Z]{3}$");
    }

    public boolean validateParameters(String code, String name, String sign) {
        return code != null && name!= null & sign != null & code.matches("([A-Za-z]{1,3})") && name.matches("([A-Za-zА]{1,8})( [A-Za-zА]{1,10}){0,2}") && sign.matches("\\S");
    }

    public String getCurrencyCodeWithoutSlash(String currencyCode) {
        return currencyCode.substring(1);
    }
}

