package app.controller;

import app.entity.BorrowRequest;
import app.entity.Device;
import app.service.DeviceManagementService;
import app.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BorrowRequestController implements Initializable {
    @FXML
    private Label deviceNameLabel;
    
    @FXML
    private Label deviceIdLabel;
    
    @FXML
    private DatePicker borrowDatePicker;
    
    @FXML
    private DatePicker returnDatePicker;
    
    @FXML
    private TextArea purposeTextArea;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label messageLabel;
    
    private Device device;
    private DeviceManagementService service = new DeviceManagementService();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 设置日期选择器默认值为今天和未来7天
        borrowDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusDays(7));
    }
    
    public void setDevice(Device device) {
        this.device = device;
        deviceNameLabel.setText(device.getName());
        deviceIdLabel.setText(String.valueOf(device.getId()));
    }
    
    @FXML
    protected void handleSubmitRequest() {
        // 基本表单验证
        if (borrowDatePicker.getValue() == null) {
            messageLabel.setText("请选择借用日期！");
            return;
        }
        
        if (returnDatePicker.getValue() == null) {
            messageLabel.setText("请选择预计归还日期！");
            return;
        }
        
        if (purposeTextArea.getText().trim().isEmpty()) {
            messageLabel.setText("请填写借用目的！");
            return;
        }
        
        LocalDate borrowDate = borrowDatePicker.getValue();
        LocalDate returnDate = returnDatePicker.getValue();
        
        if (borrowDate.isAfter(returnDate)) {
            messageLabel.setText("借用日期不能晚于归还日期！");
            return;
        }
        
        if (borrowDate.isBefore(LocalDate.now())) {
            messageLabel.setText("借用日期不能早于今天！");
            return;
        }
        
        String purpose = purposeTextArea.getText().trim();
        
        // 获取当前登录用户
        SessionManager sessionManager = SessionManager.getInstance();
        String username = sessionManager.getCurrentUsername();
        int userId = service.getUserByUsername(username).getId();
        
        // 创建借用申请
        boolean success = service.addBorrowRequest(device.getId(), userId, borrowDate, returnDate, purpose);
        
        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("申请提交成功");
            alert.setHeaderText(null);
            alert.setContentText("您的借用申请已提交，请等待管理员审批。");
            alert.showAndWait();
            
            // 关闭窗口
            ((Stage) submitButton.getScene().getWindow()).close();
        } else {
            messageLabel.setText("申请提交失败，请重试！");
        }
    }
    
    @FXML
    protected void handleCancel() {
        // 关闭窗口
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}