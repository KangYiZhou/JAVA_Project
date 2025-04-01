package app.controller;

import java.util.logging.Logger;
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

public class AdminMainController implements Initializable {
    
    private static final Logger LOGGER = Logger.getLogger(AdminMainController.class.getName());
    @FXML
    private Label adminInfoLabel;
    
    @FXML
    private TableView adminDeviceTableView;
    
    @FXML
    private TableColumn adminIdColumn;
    
    @FXML
    private TableColumn adminNameColumn;
    
    @FXML
    private TableColumn adminTypeColumn;
    
    @FXML
    private TableColumn adminModelColumn;
    
    @FXML
    private TableColumn adminStatusColumn;
    
    @FXML
    private TableColumn adminActionColumn;
    
    @FXML
    private TextField adminSearchField;
    
    @FXML
    private ComboBox<String> adminDeviceStatusFilter;
    
    @FXML
    private TabPane adminTabPane;
    
    @FXML
    private TableView requestTableView;
    
    @FXML
    private ComboBox<String> requestStatusFilter;
    
    @FXML
    private TableView returnTableView;
    
    private String adminUsername;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 初始化设备状态过滤选项
        adminDeviceStatusFilter.setItems(FXCollections.observableArrayList(
            "全部", "空闲", "已借出", "维修中"
        ));
        adminDeviceStatusFilter.setValue("全部");
        
        // 初始化请求状态过滤选项
        requestStatusFilter.setItems(FXCollections.observableArrayList(
            "全部", "待审批", "已批准", "已拒绝"
        ));
        requestStatusFilter.setValue("待审批");
        
        // 初始化表格列
        setupDeviceTable();
        setupRequestTable();
        setupReturnTable();
        
        // 加载数据
        loadDeviceData();
        loadRequestData();
        loadReturnData();
    }
    
    public void setAdminInfo(String username) {
        this.adminUsername = username;
        adminInfoLabel.setText("欢迎, " + username);
    }
    
    private void setupDeviceTable() {
        adminIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adminNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        adminTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        adminModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        adminStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // 操作列需要单独设置，添加编辑和删除按钮
    }
    
    private void setupRequestTable() {
        // 设置借用申请表格的列映射
    }
    
    private void setupReturnTable() {
        // 设置归还管理表格的列映射
    }
    
    private void loadDeviceData() {
        // TODO: 从数据库加载设备数据
    }
    
    private void loadRequestData() {
        // TODO: 从数据库加载借用申请数据
    }
    
    private void loadReturnData() {
        // TODO: 从数据库加载归还管理数据
    }

    @FXML
    protected void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) adminInfoLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("设备管理系统 - 登录");
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.centerOnScreen();

        } catch (IOException e) {
            showAlert("错误", "返回登录界面失败：" + e.getMessage());
        }
    }
    
    @FXML
    protected void handleAdminSearch() {
        String keyword = adminSearchField.getText().trim();
        String status = adminDeviceStatusFilter.getValue();
        
        // TODO: 根据关键字和状态过滤设备
    }
    
    @FXML
    protected void handleAdminReset() {
        adminSearchField.clear();
        adminDeviceStatusFilter.setValue("全部");
        loadDeviceData();
    }
    
    @FXML
    protected void handleAddDevice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DeviceForm.fxml"));
            Parent root = loader.load();
            
            DeviceFormController controller = loader.getController();
            controller.setMode("add");
            
            Stage deviceStage = new Stage();
            deviceStage.setTitle("添加设备");
            deviceStage.setScene(new Scene(root));
            deviceStage.showAndWait();
            
            // 重新加载设备数据
            loadDeviceData();
            
        } catch (IOException e) {
            showAlert("错误", "打开设备添加界面失败：" + e.getMessage());
        }
    }
    
    @FXML
    protected void handleRefreshRequests() {
        loadRequestData();
    }

    @FXML
    protected void handleExitAdminPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            if (adminUsername != null) {
                LOGGER.info("传递的用户名: " + adminUsername);
                controller.initUserData(adminUsername, true);
            }

            Stage stage = (Stage) adminInfoLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("设备管理系统");
            stage.setScene(scene);

            LOGGER.info("管理员已退出管理面板，返回主界面");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("返回主界面失败: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
    
    // 其他方法：编辑设备、删除设备、审批申请、处理归还等
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}