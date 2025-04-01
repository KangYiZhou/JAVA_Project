package app.entity;

import java.time.LocalDate;

public class BorrowRequest {
    private int id;
    private int deviceId;
    private int userId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String purpose;
    private String status;

    // 构造函数、Getter和Setter方法
    public BorrowRequest() {}

    public BorrowRequest(int id, int deviceId, int userId, LocalDate borrowDate, LocalDate returnDate, String purpose, String status) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.purpose = purpose;
        this.status = status;
    }

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
}