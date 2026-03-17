package configDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import configDB.DatabaseConfig;

public class DatabaseConnection {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    DatabaseConfig.getUrl(),
                    DatabaseConfig.getUser(),
                    DatabaseConfig.getPassword());

        } catch (SQLException e){
            throw new RuntimeException("Database connection failed", e);
        }
    }
}