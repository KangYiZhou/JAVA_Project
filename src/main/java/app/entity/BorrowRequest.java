package app.entity;

import java.time.LocalDate;

public class BorrowRequest {
    private int id;
    private int deviceId;
    private int userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;  // 新增字段：实际归还日期
    private String purpose;
    private String status;
    
    // 用于显示的关联字段
    private String userName;    // 用户名
    private String deviceName;  // 设备名称
    
    // 无参构造方法
    public BorrowRequest() {}
    
    // 带参构造方法
    public BorrowRequest(int id, int deviceId, int userId, LocalDate borrowDate, 
                         LocalDate returnDate, String purpose, String status) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.purpose = purpose;
        this.status = status;
    }
    
    // 新增构造方法，包含全部字段
    public BorrowRequest(int id, int deviceId, int userId, LocalDate borrowDate, 
                         LocalDate returnDate, LocalDate actualReturnDate, 
                         String purpose, String status) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.purpose = purpose;
        this.status = status;
    }
    
    // Getter和Setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}