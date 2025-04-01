package app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import app.service.DeviceManagementService;
import app.entity.User;


public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    // 假设的用户数据 - 实际项目中会从数据库获取
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";
    private final String NORMAL_USER = "user";
    private final String NORMAL_PASS = "user123";

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("用户名和密码不能为空！");
            return;
        }

        // 这里应该连接数据库验证用户信息
        DeviceManagementService service = new DeviceManagementService();
        User user = service.getUserByUsername(username);

        boolean loginSuccess = false;
        boolean isAdmin = false;

        if (user != null && user.getPassword().equals(password)) {
            loginSuccess = true;
            isAdmin = "管理员".equals(user.getRole());
        }

        if (loginSuccess) {
            try {
                // 登录成功，加载主界面
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
                Parent root = loader.load();

                // 获取Controller并传递用户信息
                MainController mainController = loader.getController();
                mainController.initUserData(username, isAdmin);

                // 显示主界面
                Stage stage = (Stage) usernameField.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("设备管理系统");
                stage.setScene(scene);
                stage.setMaximized(true);

            } catch (IOException e) {
                messageLabel.setText("加载主界面失败，请重试！");
                e.printStackTrace();
            }
        } else {
            messageLabel.setText("用户名或密码错误！");
        }
    }

    @FXML
    protected void handleRegister() {
        try {
            // 加载注册界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("用户注册");
            stage.setScene(scene);

        } catch (IOException e) {
            messageLabel.setText("加载注册界面失败，请重试！");
            e.printStackTrace();
        }
    }
}