package org.example.first;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setHeader("Access-Control-Allow-Origin", "*"); //разрешен доступ с любого домена
        res.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");  //какие методы разрешены
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");  //какие заголовки может отправить нам клиент

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

