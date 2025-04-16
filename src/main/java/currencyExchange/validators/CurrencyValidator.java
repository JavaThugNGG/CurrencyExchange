package currencyExchange.validators;

public class CurrencyValidator {
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
}
