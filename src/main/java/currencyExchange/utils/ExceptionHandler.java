package currencyExchange.utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;


public class ExceptionHandler {
    private static final Utils utils = new Utils();

    public static void handleException(HttpServletResponse resp, Throwable throwable) throws IOException {
        int status = getStatusCode(throwable);
        String error = throwable.getMessage();
        Map<String, String> errorResponse = Map.of("message", error);
        utils.sendResponse(resp, status, errorResponse);
    }

    private static int getStatusCode(Throwable throwable) {
        if (throwable instanceof SQLException ||
                (throwable.getCause() != null && throwable.getCause() instanceof SQLException)) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        return switch (throwable.getClass().getSimpleName()) {
            case "IllegalArgumentException" -> HttpServletResponse.SC_BAD_REQUEST;         // 400
            case "ElementNotFoundException" -> HttpServletResponse.SC_NOT_FOUND;           // 404
            case "ElementAlreadyExistsException" -> HttpServletResponse.SC_CONFLICT;       // 409
            default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;                       // 500
        };
    }
}
