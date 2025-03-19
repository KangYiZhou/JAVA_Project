package app.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
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
    protected void handleSave() {
        // 基本表单验证
        if (deviceNameField.getText().trim().isEmpty()) {
            messageLabel.setText("设备名称不能为空！");
            return;
        }
        
        if (deviceTypeCombo.getValue() == null) {
            messageLabel.setText("请选择设备类型！");
            return;
        }
        
        if (purchaseDatePicker.getValue() == null) {
            messageLabel.setText("请选择购入日期！");
            return;
        }
        
        // 获取表单数据
        String name = deviceNameField.getText().trim();
        String type = deviceTypeCombo.getValue();
        String model = deviceModelField.getText().trim();
        String manufacturer = manufacturerField.getText().trim();
        LocalDate purchaseDate = purchaseDatePicker.getValue();
        String status = deviceStatusCombo.getValue();
        String description = descriptionTextArea.getText().trim();
        
        // TODO: 保存到数据库
        boolean success = false;
        
        if ("add".equals(mode)) {
            // 添加新设备
            success = true; // 模拟添加成功
        } else if ("edit".equals(mode)) {
            // 更新设备信息
            success = true; // 模拟更新成功
        }
        
        if (success) {
            // 关闭窗口
            ((Stage) saveButton.getScene().getWindow()).close();
        } else {
            messageLabel.setText("保存失败，请重试！");
        }
    }
    
    @FXML
    protected void handleCancel() {
        // 关闭窗口
        ((Stage) cancelButton.getScene().getWindow()).close();
    }
}