package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    // 更新数据库名为 courseDesign
    private static final String URL = "jdbc:mysql://localhost:3306/courseDesign";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "成功连接到数据库: {0}", URL);
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "数据库连接失败: {0}", e.getMessage());
            throw e;
        }
    }
}