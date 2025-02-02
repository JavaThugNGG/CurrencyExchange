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


@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {

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

        // Устанавливаем тип контента для ответа
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Currencies");

            while (resultSet.next()) {
                Currency currency = new Currency(
                    resultSet.getString("id"),
                    resultSet.getString("full_name"),
                    resultSet.getString("code"),
                    resultSet.getString("sign")
                );
                String json = objectMapper.writeValueAsString(currency);
                out.println(json);
            }


            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

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
        String generatedId;

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql1 = "INSERT INTO Currencies (full_name, code, sign) " +
                         "VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql1)) {
                stmt.setString(1, name);
                stmt.setString(2, code);
                stmt.setString(3, sign);
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = String.valueOf(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Не удалось получить сгенерированный id.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Currency currency = new Currency(
            generatedId,
            name,
            code,
            sign
        );
        String json = objectMapper.writeValueAsString(currency);
        out.println(json);
    }
}
