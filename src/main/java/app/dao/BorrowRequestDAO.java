package app.dao;

import app.db.DBConnection;
import app.entity.BorrowRequest;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRequestDAO {
    
    // 添加借用申请
    public boolean addBorrowRequest(BorrowRequest request) {
        String sql = "INSERT INTO borrowrequests (device_id, user_id, borrow_date, expected_return_date, purpose, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, request.getDeviceId());
            pstmt.setInt(2, request.getUserId());
            pstmt.setDate(3, Date.valueOf(request.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(request.getReturnDate()));
            pstmt.setString(5, request.getPurpose());
            pstmt.setString(6, request.getStatus());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有借用申请
    public List<BorrowRequest> getAllRequests() {
        List<BorrowRequest> requests = new ArrayList<>();
        String sql = "SELECT r.*, u.UName as user_name, d.name as device_name " +
                    "FROM borrowrequests r " +
                    "JOIN users u ON r.user_id = u.Uid " +
                    "JOIN devices d ON r.device_id = d.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                BorrowRequest request = extractRequestFromResultSet(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // 根据状态获取借用申请
    public List<BorrowRequest> getRequestsByStatus(String status) {
        List<BorrowRequest> requests = new ArrayList<>();
        String sql = "SELECT r.*, u.UName as user_name, d.name as device_name " +
                    "FROM borrowrequests r " +
                    "JOIN users u ON r.user_id = u.Uid " +
                    "JOIN devices d ON r.device_id = d.id " +
                    "WHERE r.status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRequest request = extractRequestFromResultSet(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // 根据用户ID获取借用申请
    public List<BorrowRequest> getRequestsByUserId(int userId) {
        List<BorrowRequest> requests = new ArrayList<>();
        String sql = "SELECT r.*, u.UName as user_name, d.name as device_name " +
                    "FROM borrowrequests r " +
                    "JOIN users u ON r.user_id = u.Uid " +
                    "JOIN devices d ON r.device_id = d.id " +
                    "WHERE r.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRequest request = extractRequestFromResultSet(rs);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // 更新借用申请状态
    public boolean updateRequestStatus(int requestId, String newStatus) {
        String sql = "UPDATE borrowrequests SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            int rows = pstmt.executeUpdate();
            
            // 如果状态更新为"已批准"，同时更新设备状态为"已借出"
            if (rows > 0 && "已批准".equals(newStatus)) {
                updateDeviceStatus(conn, requestId, "已借出");
            }
            
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 设备归还处理
    public boolean returnDevice(int requestId, LocalDate actualReturnDate) {
        String sql = "UPDATE borrowrequests SET status = '已归还', actual_return_date = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(actualReturnDate));
            pstmt.setInt(2, requestId);
            int rows = pstmt.executeUpdate();
            
            // 更新设备状态为"空闲"
            if (rows > 0) {
                updateDeviceStatus(conn, requestId, "空闲");
            }
            
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 更新设备状态的辅助方法
    private void updateDeviceStatus(Connection conn, int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE devices d JOIN borrowrequests r ON d.id = r.device_id SET d.status = ? WHERE r.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }
    
    // 从ResultSet提取BorrowRequest对象的辅助方法
    private BorrowRequest extractRequestFromResultSet(ResultSet rs) throws SQLException {
        BorrowRequest request = new BorrowRequest();
        request.setId(rs.getInt("id"));
        request.setDeviceId(rs.getInt("device_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        request.setReturnDate(rs.getDate("expected_return_date").toLocalDate());
        
        Date actualReturnDate = rs.getDate("actual_return_date");
        if (actualReturnDate != null) {
            request.setActualReturnDate(actualReturnDate.toLocalDate());
        }
        
        request.setPurpose(rs.getString("purpose"));
        request.setStatus(rs.getString("status"));
        
        // 设置关联信息
        request.setUserName(rs.getString("user_name"));
        request.setDeviceName(rs.getString("device_name"));
        
        return request;
    }
    
    // 根据ID获取借用申请
    public BorrowRequest getRequestById(int requestId) {
        String sql = "SELECT r.*, u.UName as user_name, d.name as device_name " +
                    "FROM borrowrequests r " +
                    "JOIN users u ON r.user_id = u.Uid " +
                    "JOIN devices d ON r.device_id = d.id " +
                    "WHERE r.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractRequestFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
