package app.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import app.entity.Device;
import app.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label currentUserLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView deviceTableView;

    @FXML
    private TableColumn deviceIdColumn;

    @FXML
    private TableColumn deviceNameColumn;

    @FXML
    private TableColumn deviceTypeColumn;

    @FXML
    private TableColumn statusColumn;

    @FXML
    private TableColumn currentUserColumn;

    @FXML
    private TableColumn operationColumn;

    @FXML
    private MenuItem addDeviceMenuItem;

    @FXML
    private MenuItem approvalsMenuItem;

    @FXML
    private Button adminButton;

    private String username;
    private boolean isAdmin;

    // 定义并初始化 LOGGER
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deviceIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        deviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        deviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        currentUserColumn.setCellValueFactory(new PropertyValueFactory<>("currentUser"));
        // 后续可加载数据
        loadDeviceData();
        // 初始化时从SessionManager获取用户信息
        updateUIFromSession();
    }

    // 更新为公开方法，可以从其他控制器调用
    public void updateUIFromSession() {
        SessionManager session = SessionManager.getInstance();

        if (session.isLoggedIn()) {
            String username = session.getCurrentUsername();
            boolean isAdmin = session.isAdmin();

            LOGGER.info("从会话加载用户信息: " + username + ", 管理员: " + isAdmin);

            currentUserLabel.setText(username);
            userRoleLabel.setText(isAdmin ? "管理员" : "普通用户");

            // 设置管理员特定UI元素可见性
            addDeviceMenuItem.setVisible(isAdmin);
            approvalsMenuItem.setVisible(isAdmin);
            adminButton.setVisible(isAdmin);
        } else {
            currentUserLabel.setText("未登录");
            userRoleLabel.setText("");

            // 隐藏管理员特定UI元素
            addDeviceMenuItem.setVisible(false);
            approvalsMenuItem.setVisible(false);
            adminButton.setVisible(false);
        }
    }

    // 旧方法保留用于兼容性
    public void initUserData(String username, boolean isAdmin) {
        SessionManager.getInstance().login(username, isAdmin);
        updateUIFromSession();
    }

    private void loadDeviceData() {

    }

    @FXML
    protected void handleUserInfo() {
        // 显示用户信息
    }

    @FXML
    protected void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) currentUserLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("设备管理系统 - 登录");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.centerOnScreen();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "返回登录界面失败", e);
            showAlert("错误", "返回登录界面失败：" + e.getMessage());
        }
    }

    @FXML
    protected void handleAddDevice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeviceForm.fxml"));
            Parent root = loader.load();

            Stage deviceStage = new Stage();
            deviceStage.setTitle("添加设备");
            deviceStage.setScene(new Scene(root));
            deviceStage.showAndWait();

            // 重新加载设备数据
            loadDeviceData();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "打开设备添加界面失败", e);
            showAlert("错误", "打开设备添加界面失败：" + e.getMessage());
        }
    }

    @FXML
    protected void handleRefreshDevices() {
        loadDeviceData();
    }

    @FXML
    protected void handleMyBorrowings() {
        // 显示用户借用记录
    }

    @FXML
    protected void handleApprovals() {
        // 显示待审批列表
    }

    @FXML
    protected void handleSearch() {
        String keyword = searchField.getText().trim();
        // TODO: 根据关键字搜索设备
    }

    @FXML
    protected void handleBorrowDevice() {
        // 获取选中的设备
        Object selectedDevice = deviceTableView.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            showAlert("提示", "请先选择一个设备");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BorrowRequest.fxml"));
            Parent root = loader.load();

            // TODO: 初始化借用表单数据

            Stage borrowStage = new Stage();
            borrowStage.setTitle("设备借用申请");
            borrowStage.setScene(new Scene(root));
            borrowStage.showAndWait();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "打开借用申请界面失败", e);
            showAlert("错误", "打开借用申请界面失败：" + e.getMessage());
        }
    }

    @FXML
    protected void handleReturnDevice() {
        // 归还设备逻辑
    }

    @FXML
    private void handleAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/adminMain.fxml"));
            Parent root = loader.load();
            
            AdminMainController controller = loader.getController();
            // 设置管理员信息（以显示欢迎信息）
            controller.setAdminInfo(SessionManager.getInstance().getCurrentUsername());
            
            Stage stage = (Stage) adminButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("管理员控制面板");
            stage.setScene(scene);
            stage.setMaximized(true);
            
            LOGGER.info("进入管理员面板");
        } catch (IOException e) {
            // 错误处理
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}