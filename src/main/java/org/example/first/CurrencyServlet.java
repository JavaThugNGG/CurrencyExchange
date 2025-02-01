package org.example.first;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Map;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        File dbFile;
        try {
            dbFile = new File(SQLiteConnection.class.getClassLoader().getResource("database.db").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection connection = DriverManager.getConnection(url)) {
            String pathInfo = request.getPathInfo();


            if (pathInfo != null && !pathInfo.equals("/")) {
                String currencyCode = pathInfo.substring(1);
                String sql = "SELECT * FROM Currencies WHERE code = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, currencyCode);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Currency currency = new Currency(
                                    resultSet.getString("id"),
                                    resultSet.getString("full_name"),
                                    resultSet.getString("code"),
                                    resultSet.getString("sign")
                            );

                            String json = objectMapper.writeValueAsString(currency);
                            out.println(json);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            out.println(objectMapper.writeValueAsString(
                                    Map.of("error", "Currency not found")));
                        }
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println(objectMapper.writeValueAsString(
                        Map.of("error", "No currency code provided")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
