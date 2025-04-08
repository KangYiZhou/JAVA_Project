package app.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import app.service.DeviceManagementService;
import app.entity.User;

public class RegisterController implements Initializable {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private ComboBox<String> userTypeComboBox;
    
    @FXML
    private Label messageLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 初始化用户类型下拉框
        userTypeComboBox.setItems(FXCollections.observableArrayList("普通用户", "管理员"));
        userTypeComboBox.setValue("普通用户");
    }
    
    @FXML
    protected void handleSubmit() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String userType = userTypeComboBox.getValue();
        
        // 基本表单验证
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("所有字段都为必填项！");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("两次输入的密码不一致！");
            return;
        }
        
        if (username.length() < 3) {
            messageLabel.setText("用户名长度至少为3个字符！");
            return;
        }
        
        if (password.length() < 6) {
            messageLabel.setText("密码长度至少为6个字符！");
            return;
        }
        
        // 真实系统中，这里应保存用户到数据库
        DeviceManagementService service = new DeviceManagementService();
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(userType);
        // 模拟注册成功
        if (service.registerUser(user)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("注册成功");
            alert.setHeaderText(null);
            alert.setContentText("账户已创建成功，请返回登录！");
            alert.showAndWait();

            // 返回登录界面
            goToLogin();
        } else {
            messageLabel.setText("注册失败，请重试！");
        }
    }
    
    @FXML
    protected void handleCancel() {
        goToLogin();
    }
    
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("设备管理系统 - 登录");
            stage.setScene(scene);
            
        } catch (IOException e) {
            messageLabel.setText("返回登录界面失败，请重试！");
            e.printStackTrace();
        }
    }

}