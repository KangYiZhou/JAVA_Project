package app.service;

import app.db.DBConnection;
import app.dao.DeviceDAO;
import app.dao.UserDAO;
import app.dao.BorrowRequestDAO;
import app.entity.Device;
import app.entity.User;
import app.entity.BorrowRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeviceManagementService {
    private DeviceDAO deviceDAO = new DeviceDAO();
    private UserDAO userDAO = new UserDAO();
    private BorrowRequestDAO borrowRequestDAO = new BorrowRequestDAO();

    private static final Logger LOGGER = Logger.getLogger(DeviceManagementService.class.getName());
    private DBConnection dbConnection;

    public DeviceManagementService() {
        dbConnection = new DBConnection();
    }

    // 原有的方法保持不变
    public List<Device> getAllDevices() {
        return deviceDAO.getAllDevices();
    }

    public boolean addDevice(Device device) {
        return deviceDAO.addDevice(device);
    }

    public User getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public boolean registerUser(User user) {
        return userDAO.registerUser(user);
    }

    // 添加借用申请
    public boolean addBorrowRequest(int deviceId, int userId, LocalDate borrowDate, LocalDate returnDate, String purpose) {
        BorrowRequest request = new BorrowRequest();
        request.setDeviceId(deviceId);
        request.setUserId(userId);
        request.setBorrowDate(borrowDate);
        request.setReturnDate(returnDate);
        request.setPurpose(purpose);
        request.setStatus("待审批");
        return borrowRequestDAO.addBorrowRequest(request);
    }

    // 获取所有借用申请
    public List<BorrowRequest> getAllRequests() {
        return borrowRequestDAO.getAllRequests();
    }

    // 根据状态获取借用申请
    public List<BorrowRequest> getRequestsByStatus(String status) {
        return borrowRequestDAO.getRequestsByStatus(status);
    }

    // 更新借用申请状态
    public boolean approveRequest(int requestId) {
        return borrowRequestDAO.updateRequestStatus(requestId, "已批准");
    }

    public boolean rejectRequest(int requestId) {
        return borrowRequestDAO.updateRequestStatus(requestId, "已拒绝");
    }

    // 处理设备归还
    public boolean returnDevice(int requestId) {
        return borrowRequestDAO.returnDevice(requestId, LocalDate.now());
    }

    // 新增方法：获取所有用户
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // 新增方法：删除用户
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    // 添加删除设备的方法
    public boolean deleteDevice(int deviceId) {
        return deviceDAO.deleteDevice(deviceId);
    }

    // 如果需要使用Map方式添加设备，使用不同名称的方法
    public boolean addDeviceFromMap(Map<String, Object> deviceData) {
        try {
            // 从Map创建Device对象
            String name = (String) deviceData.get("name");
            String type = (String) deviceData.get("type");
            String model = (String) deviceData.get("model");
            String manufacturer = (String) deviceData.get("manufacturer");
            LocalDate purchaseDate = (LocalDate) deviceData.get("purchaseDate");
            String status = (String) deviceData.get("status");
            String description = (String) deviceData.get("description");
            
            // 创建新设备对象(使用0作为临时ID，数据库会生成真正的ID)
            Device device = new Device(0, name, type, model, manufacturer, 
                                      purchaseDate, status, description);
            
            // 调用DAO来保存
            return deviceDAO.addDevice(device);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "从Map数据添加设备失败", e);
            return false;
        }
    }

    public boolean updateDevice(Map<String, Object> deviceData) {
        String sql = "UPDATE devices SET name=?, type=?, model=?, manufacturer=?, " +
                     "purchase_date=?, status=?, description=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, (String) deviceData.get("name"));
            pstmt.setString(2, (String) deviceData.get("type"));
            pstmt.setString(3, (String) deviceData.get("model"));
            pstmt.setString(4, (String) deviceData.get("manufacturer"));
            
            LocalDate purchaseDate = (LocalDate) deviceData.get("purchaseDate");
            pstmt.setDate(5, purchaseDate != null ? Date.valueOf(purchaseDate) : null);
            
            pstmt.setString(6, (String) deviceData.get("status"));
            pstmt.setString(7, (String) deviceData.get("description"));
            pstmt.setInt(8, (Integer) deviceData.get("id"));
            
            int rowsAffected = pstmt.executeUpdate();
            LOGGER.info("更新设备结果: " + (rowsAffected > 0) + ", 设备ID: " + deviceData.get("id"));
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新设备失败", e);
            return false;
        }
    }

    /**
     * 根据用户ID获取该用户的所有借用申请
     * @param userId 用户ID
     * @return 借用申请列表
     */
    public List<BorrowRequest> getRequestsByUserId(int userId) {
        BorrowRequestDAO borrowRequestDAO = new BorrowRequestDAO();
        return borrowRequestDAO.getRequestsByUserId(userId);
    }

    // 添加到DeviceManagementService类中
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
}