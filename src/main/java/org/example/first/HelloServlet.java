package org.example.first;

import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.sqlite.SQLiteConnection;

@WebServlet(value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        Connection connection;

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from Currencies");

            while (resultSet.next()) {
                out.println(resultSet.getString("id"));
                out.println(resultSet.getString("code"));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}