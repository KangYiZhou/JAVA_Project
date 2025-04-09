package app.controller;

import app.service.DeviceManagementService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DeviceFormController implements Initializable {
    @FXML
    private Label formTitleLabel;
    
    @FXML
    private TextField deviceNameField;
    
    @FXML
    private ComboBox<String> deviceTypeCombo;
    
    @FXML
    private TextField deviceModelField;
    
    @FXML
    private TextField manufacturerField;
    
    @FXML
    private DatePicker purchaseDatePicker;
    
    @FXML
    private ComboBox<String> deviceStatusCombo;
    
    @FXML
    private TextArea descriptionTextArea;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label messageLabel;
    
    private String mode = "add"; // 默认为添加模式，可以是"add"或"edit"
    private int deviceId = -1; // 编辑模式下的设备ID
    private Runnable refreshCallback; // 刷新回调函数
    private int currentDeviceId; // 当前设备ID
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 初始化设备类型选项
        deviceTypeCombo.setItems(FXCollections.observableArrayList(
            "计算机", "投影仪", "打印机", "实验设备", "其他"
        ));
        
        // 初始化设备状态选项
        deviceStatusCombo.setItems(FXCollections.observableArrayList(
            "空闲", "已借出", "维修中"
        ));
        deviceStatusCombo.setValue("空闲");
        
        // 设置日期选择器默认值为今天
        purchaseDatePicker.setValue(LocalDate.now());
    }
    
    public void setMode(String mode) {
        this.mode = mode;
        if ("add".equals(mode)) {
            formTitleLabel.setText("添加设备");
            saveButton.setText("添加");
        } else if ("edit".equals(mode)) {
            formTitleLabel.setText("编辑设备");
            saveButton.setText("更新");
        }
    }
    
    public void setDeviceData(int id, String name, String type, String model, 
                              String manufacturer, LocalDate purchaseDate, 
                              String status, String description) {
        this.deviceId = id;
        deviceNameField.setText(name);
        deviceTypeCombo.setValue(type);
        deviceModelField.setText(model);
        manufacturerField.setText(manufacturer);
        purchaseDatePicker.setValue(purchaseDate);
        deviceStatusCombo.setValue(status);
        descriptionTextArea.setText(description);
    }
    
    @FXML
    private void handleSave() {
        if (validateInputs()) {
            try {
                // 获取表单数据
                String name = deviceNameField.getText().trim();
                String type = deviceTypeCombo.getValue();
                String model = deviceModelField.getText().trim();
                String manufacturer = manufacturerField.getText().trim();
                LocalDate purchaseDate = purchaseDatePicker.getValue();
                String status = deviceStatusCombo.getValue();
                String description = descriptionTextArea.getText().trim();
                
                DeviceManagementService deviceService = new DeviceManagementService();
                boolean success = false;
                
                if ("add".equals(mode)) {
                    // 使用Map存储设备数据
                    Map<String, Object> deviceData = new HashMap<>();
                    deviceData.put("name", name);
                    deviceData.put("type", type);
                    deviceData.put("model", model);
                    deviceData.put("manufacturer", manufacturer);
                    deviceData.put("purchaseDate", purchaseDate);
                    deviceData.put("status", status);
                    deviceData.put("description", description);
                    
                    // 将设备数据保存到数据库
                    success = deviceService.addDeviceFromMap(deviceData);
                    
                    if (success) {
                        messageLabel.setText("设备添加成功！");
                        messageLabel.setTextFill(Color.GREEN);
                        
                        // 通知主界面刷新设备列表
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                        
                        // 清空表单，准备添加下一个设备
                        resetForm();
                    } else {
                        messageLabel.setText("添加设备失败，请重试。");
                        messageLabel.setTextFill(Color.RED);
                    }
                } else if ("edit".equals(mode)) {
                    // 编辑现有设备
                    Map<String, Object> deviceData = new HashMap<>();
                    deviceData.put("id", currentDeviceId); // 确保有设备ID
                    deviceData.put("name", name);
                    deviceData.put("type", type);
                    deviceData.put("model", model);
                    deviceData.put("manufacturer", manufacturer);
                    deviceData.put("purchaseDate", purchaseDate);
                    deviceData.put("status", status);
                    deviceData.put("description", description);
                    
                    success = deviceService.updateDevice(deviceData);
                    
                    if (success) {
                        messageLabel.setText("设备更新成功！");
                        messageLabel.setTextFill(Color.GREEN);
                        
                        // 通知主界面刷新设备列表
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                        
                        // 关闭编辑窗口
                        Stage stage = (Stage) saveButton.getScene().getWindow();
                        stage.close();
                    } else {
                        messageLabel.setText("更新设备失败，请重试。");
                        messageLabel.setTextFill(Color.RED);
                    }
                }
            } catch (Exception e) {
                messageLabel.setText("发生错误: " + e.getMessage());
                messageLabel.setTextFill(Color.RED);
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    protected void handleCancel() {
        // 关闭窗口
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
    
    private boolean validateInputs() {
        // 基本表单验证
        if (deviceNameField.getText().trim().isEmpty()) {
            messageLabel.setText("设备名称不能为空！");
            return false;
        }
        
        if (deviceTypeCombo.getValue() == null) {
            messageLabel.setText("请选择设备类型！");
            return false;
        }
        
        if (purchaseDatePicker.getValue() == null) {
            messageLabel.setText("请选择购入日期！");
            return false;
        }
        
        return true;
    }
    
    private void resetForm() {
        deviceNameField.clear();
        deviceTypeCombo.setValue(null);
        deviceModelField.clear();
        manufacturerField.clear();
        purchaseDatePicker.setValue(LocalDate.now());
        deviceStatusCombo.setValue("空闲");
        descriptionTextArea.clear();
        messageLabel.setText("");
    }
}