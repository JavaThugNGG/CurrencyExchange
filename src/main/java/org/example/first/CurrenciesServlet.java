package org.example.first;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sqlite.SQLiteConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.*;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
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

            JSONArray currenciesArray = new JSONArray();

            while (resultSet.next()) {
                JSONObject currencyObject = new JSONObject();
                currencyObject.put("id", resultSet.getString("id"));
                currencyObject.put("name", resultSet.getString("full_name"));
                currencyObject.put("code", resultSet.getString("code"));
                currencyObject.put("sign", resultSet.getString("sign"));
                currenciesArray.put(currencyObject);
            }

            out.println(currenciesArray);

            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
