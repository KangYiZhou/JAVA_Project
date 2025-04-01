package app.dao;

import app.db.DBConnection;
import app.entity.BorrowRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRequestDAO {
    public boolean addBorrowRequest(BorrowRequest request) {
        String sql = "INSERT INTO borrow_requests (device_id, user_id, borrow_date, return_date, purpose, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, request.getDeviceId());
            pstmt.setInt(2, request.getUserId());
            pstmt.setObject(3, request.getBorrowDate());
            pstmt.setObject(4, request.getReturnDate());
            pstmt.setString(5, request.getPurpose());
            pstmt.setString(6, request.getStatus());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<BorrowRequest> getBorrowRequestsByStatus(String status) {
        List<BorrowRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM borrow_requests WHERE status = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int deviceId = rs.getInt("device_id");
                    int userId = rs.getInt("user_id");
                    LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();
                    LocalDate returnDate = rs.getDate("return_date").toLocalDate();
                    String purpose = rs.getString("purpose");
                    BorrowRequest request = new BorrowRequest(id, deviceId, userId, borrowDate, returnDate, purpose, status);
                    requests.add(request);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    // 其他数据库操作方法，如更新借用申请状态等
}
