package org.example.first.services;

import org.example.first.DAO.CurrencyDAO;
import org.example.first.DTO.CurrencyDTO;
import org.example.first.exceptions.ElementAlreadyExistsException;

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

