package org.example.first;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteConnection;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.*;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

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

            String pathInfo = request.getPathInfo();

            if (pathInfo != null && !pathInfo.isEmpty()) {      //случаи currencies и currencies/
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Currencies WHERE code = ?");
                String currencyCode = pathInfo.substring(1);  //извлекаем строку начиная с индекса 1
                preparedStatement.setString(1, currencyCode);  // Параметр "?" заменится на значение переменной currencyCode
                ResultSet resultSet = preparedStatement.executeQuery();

                JSONObject currencyObject = new JSONObject();
                currencyObject.put("id", resultSet.getString("id"));
                currencyObject.put("name", resultSet.getString("full_name"));
                currencyObject.put("code", resultSet.getString("code"));
                currencyObject.put("sign", resultSet.getString("sign"));

                out.println(currencyObject);

                statement.close();
            } else {
                response.getWriter().println("No currency code provided");
                //нужно будет выйти отсюда нахуй
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


