package org.example.first;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // Разрешение CORS для всех доменов
        res.setHeader("Access-Control-Allow-Origin", "*");

        // Разрешение методов для запросов
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");

        // Разрешение заголовков для запросов
        res.setHeader("Access-Control-Allow-Headers", "*");

        // Обработка других запросов
        chain.doFilter(request, response); // Переход к следующему фильтру или ресурсу
    }

    @Override
    public void destroy() {}
}
