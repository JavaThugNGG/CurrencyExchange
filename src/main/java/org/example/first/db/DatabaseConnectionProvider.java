package org.example.first.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;

public class DatabaseConnectionProvider {
    private static HikariDataSource dataSource;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            URI dbUri = DatabaseConnectionProvider.class.getClassLoader().getResource("database.db").toURI();
            File dbFile = new File(dbUri);
            String databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(databaseUrl);
            config.setMaximumPoolSize(10);
            dataSource = new HikariDataSource(config);
        } catch (URISyntaxException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
