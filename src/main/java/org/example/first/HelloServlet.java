package org.example.first;

import java.io.*;
import java.sql.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        Connection connection;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/java_ee_db", "postgres", "Qwerty123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from books");

            while (resultSet.next()) {
                out.println(resultSet.getString("title"));
                out.println(resultSet.getString("author"));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}