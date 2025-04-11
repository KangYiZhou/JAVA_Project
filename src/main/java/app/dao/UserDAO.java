package app.dao;

import app.db.DBConnection;
import app.entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (UName, UPwd, URole, UQuestion, UAnswer) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getQuestion());
            pstmt.setString(5, user.getAnswer());
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                LOGGER.log(Level.INFO, "用户 {0} 注册成功", user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "用户注册失败", e);
        }
        return false;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE UName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("Uid");
                    String password = rs.getString("UPwd");
                    String role = rs.getString("URole");
                    String question = rs.getString("UQuestion");
                    String answer = rs.getString("UAnswer");
                    return new User(id, username, password, role, question, answer);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "获取用户信息失败", e);
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("Uid");
                String username = rs.getString("UName");
                String password = rs.getString("UPwd");
                String role = rs.getString("URole");
                String question = rs.getString("UQuestion");
                String answer = rs.getString("UAnswer");
                User user = new User(id, username, password, role, question, answer);
                users.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "获取所有用户信息失败", e);
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE Uid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "删除用户失败", e);
            return false;
        }
    }

    // 添加到UserDAO类中
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET UPwd=?, UQuestion=?, UAnswer=? WHERE Uid=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getQuestion());
            pstmt.setString(3, user.getAnswer());
            pstmt.setInt(4, user.getId());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新用户信息失败", e);
            return false;
        }
    }
}