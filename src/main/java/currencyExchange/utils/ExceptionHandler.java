package currencyExchange.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;


public class ExceptionHandler {
    public static void handleException(HttpServletResponse resp, Throwable throwable) throws IOException {
        int status = getStatusCode(throwable);
        String error = throwable.getMessage();
        Map<String, String> errorResponse = Map.of("message", error);
        JsonResponseWriter.sendResponse(resp, status, errorResponse);
    }

    private static int getStatusCode(Throwable throwable) {
        return switch (throwable.getClass().getSimpleName()) {
            case "IllegalArgumentException" -> HttpServletResponse.SC_BAD_REQUEST;
            case "ElementNotFoundException" -> HttpServletResponse.SC_NOT_FOUND;
            case "ElementAlreadyExistsException" -> HttpServletResponse.SC_CONFLICT;
            case "DatabaseException" -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        };
    }
}
