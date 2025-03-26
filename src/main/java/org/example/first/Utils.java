package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void sendResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        try (PrintWriter out = response.getWriter()) {
            out.println(objectMapper.writeValueAsString(data));
        }
    }
}

