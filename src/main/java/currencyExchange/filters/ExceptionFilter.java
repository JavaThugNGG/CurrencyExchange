package currencyExchange.filters;

import currencyExchange.utils.ExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ExceptionFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
        String path = req.getRequestURI();

        if (path.startsWith("/api/css/") || path.startsWith("/api/js/") || path.startsWith("/api/images/")) { // Пропускаем запросы к статическим ресурсам
            chain.doFilter(req, resp);
            return;
        }

        try {
            chain.doFilter(req, resp);
        } catch (Throwable throwable) {
            ExceptionHandler.handleException(resp, throwable);
        }
    }
}

