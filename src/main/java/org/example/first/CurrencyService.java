package org.example.first;


import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public CurrencyDTO getCurrencyByCode(String code) throws SQLException {
        return currencyDAO.getByCode(code);
    }

    public List<CurrencyDTO> getAllCurrencies() throws SQLException {
        return currencyDAO.getAll();
    }

    public CurrencyDTO putCurrency(String fullName, String code, String sign) throws SQLException {
        if (currencyDAO.isExists(code)) {
            throw new ElementAlreadyExistsException();
        }
            long id = currencyDAO.insert(fullName, code, sign);
            return new CurrencyDTO(id, fullName, code, sign);
    }

    public boolean isPathValidated(String path) {
        return path != null && path.matches("^/[A-Z]{3}$");
    }

    public boolean isParametersValidated(String fullName, String code, String sign) {
        if (isParameterEmpty(fullName) || isParameterEmpty(code) || isParameterEmpty(sign)) {
            return false;
        } else {
        return true;
        }
    }

    public String getCurrencyCodeWithoutSlash(String currencyCode) {
        return currencyCode.substring(1);
    }

    private boolean isParameterEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}

