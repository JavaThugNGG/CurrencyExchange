package currencyExchange.validators;

public class CurrencyValidator {
    public void validatePath(String path) {
        if ((path == null) || (!path.matches("^/[A-Z]{3}$"))) {
            throw new IllegalArgumentException("Некорректный URL запроса");
        }
    }

    public void validateParameters(String code, String name, String sign) {
        if (!validateCode(code)) {
            throw new IllegalArgumentException("Некорректный аргумент code при добавлении валюты");
        }
        if (!validateName(name)) {
            throw new IllegalArgumentException("Некорректный аргумент name при добавлении валюты");
        }
        if (!validateSign(sign)) {
            throw new IllegalArgumentException("Некорректный аргумент sign при добавлении валюты");
        }
    }

    private boolean validateCode(String code) {
        return code != null & code.matches("([A-Za-z]{1,3})");
    }

    private boolean validateName(String name) {
        return name != null & name.matches("([A-Za-zА]{1,8})( [A-Za-zА]{1,10}){0,2}");
    }

    private boolean validateSign(String sign) {
        return sign != null && sign.matches("\\S");
    }
}
