package org.example.first;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;

public class DataSourceProvider {
    private static HikariDataSource dataSource;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            URI dbUri = DataSourceProvider.class.getClassLoader().getResource("database.db").toURI();
            File dbFile = new File(dbUri);
            String databaseUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(databaseUrl);  // Устанавливаем URL для базы данных
            config.setMaximumPoolSize(10);   // Максимальное количество соединений в пуле
            dataSource = new HikariDataSource(config);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Ошибка при загрузке базы данных", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
