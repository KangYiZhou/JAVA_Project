package app.dao;

import app.db.DBConnection;
import app.entity.Device;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeviceDAO {
    public List<Device> getAllDevices() {
        List<Device> devices = new ArrayList<>();
        String sql = "SELECT * FROM devices";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String model = rs.getString("model");
                String manufacturer = rs.getString("manufacturer");
                LocalDate purchaseDate = rs.getDate("purchase_date").toLocalDate();
                String status = rs.getString("status");
                String description = rs.getString("description");
                Device device = new Device(id, name, type, model, manufacturer, purchaseDate, status, description);
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public boolean addDevice(Device device) {
        String sql = "INSERT INTO devices (name, type, model, manufacturer, purchase_date, status, description) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, device.getName());
            pstmt.setString(2, device.getType());
            pstmt.setString(3, device.getModel());
            pstmt.setString(4, device.getManufacturer());
            pstmt.setObject(5, device.getPurchaseDate());
            pstmt.setString(6, device.getStatus());
            pstmt.setString(7, device.getDescription());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 其他数据库操作方法，如更新设备信息、删除设备等
}