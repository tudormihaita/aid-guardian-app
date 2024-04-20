package dot.help.persistence.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
    private final Properties jdbcProps;
    private static final Logger logger = LogManager.getLogger();
    private Connection instance = null;

    public DBUtils(Properties jdbcProps) {
        this.jdbcProps = jdbcProps;
    }

    private Connection establishConnection() {
        logger.traceEntry();

        String url = jdbcProps.getProperty("project.jdbc.url");
        String username = jdbcProps.getProperty("project.jdbc.user");
        String password = jdbcProps.getProperty("project.jdbc.password");

        logger.info("Trying to connect do Database... {}", url);
        logger.info("Username: {}", username);
        logger.info("Password: {}", password);

        Connection connection = null;
        try {
            if(username != null && password != null) {
                connection = DriverManager.getConnection(url, username, password);
            }
            else {
                connection = DriverManager.getConnection(url);
            }
        }
        catch (SQLException exception) {
            logger.error(exception);
            System.err.println("Error getting connection: " + exception);
        }

        logger.traceExit("Established new connection: {}", connection);
        return connection;
    }

    public Connection getConnection() {
        logger.traceEntry();

        try {
            if(instance == null || instance.isClosed()) {
                instance = establishConnection();
                logger.traceExit("Returning the connection: {}", instance);
                return instance;
            }
        }
        catch (SQLException exception) {
            logger.error(exception);
            System.err.println("Database error: " + exception);
        }

        logger.traceExit("Retrieved database connection: {}", instance);
        return instance;
    }
}
