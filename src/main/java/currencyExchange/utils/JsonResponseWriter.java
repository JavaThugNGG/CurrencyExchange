package currencyExchange.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;

import java.io.IOException;
import java.io.PrintWriter;

public class JsonResponseWriter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        @Cleanup PrintWriter out = response.getWriter();
        out.println(objectMapper.writeValueAsString(data));
    }
}

