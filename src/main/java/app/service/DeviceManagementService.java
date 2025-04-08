package app.service;

import app.dao.DeviceDAO;
import app.dao.UserDAO;
import app.dao.BorrowRequestDAO;
import app.entity.Device;
import app.entity.User;
import app.entity.BorrowRequest;
import java.time.LocalDate;
import java.util.List;

public class DeviceManagementService {
    private DeviceDAO deviceDAO = new DeviceDAO();
    private UserDAO userDAO = new UserDAO();
    private BorrowRequestDAO borrowRequestDAO = new BorrowRequestDAO();

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

    public boolean addBorrowRequest(int deviceId, int userId, LocalDate borrowDate, LocalDate returnDate, String purpose) {
        BorrowRequest request = new BorrowRequest(0, deviceId, userId, borrowDate, returnDate, purpose, "待审批");
        return borrowRequestDAO.addBorrowRequest(request);
    }

    public List<BorrowRequest> getBorrowRequestsByStatus(String status) {
        return borrowRequestDAO.getBorrowRequestsByStatus(status);
    }

    // 新增方法：获取所有用户
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // 新增方法：删除用户
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
}