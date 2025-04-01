package app.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserMainController implements Initializable {
    @FXML
    private Label userInfoLabel;
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> deviceStatusFilter;
    
    @FXML
    private TableView deviceTableView;
    
    @FXML
    private TableColumn idColumn;
    
    @FXML
    private TableColumn nameColumn;
    
    @FXML
    private TableColumn typeColumn;
    
    @FXML
    private TableColumn modelColumn;
    
    @FXML
    private TableColumn statusColumn;
    
    @FXML
    private TableColumn actionColumn;
    
    @FXML
    private ComboBox<String> borrowStatusFilter;
    
    @FXML
    private TableView borrowingTableView;
    
    private String username;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 初始化设备状态过滤选项
        deviceStatusFilter.setItems(FXCollections.observableArrayList(
            "全部", "空闲", "已借出", "维修中"
        ));
        deviceStatusFilter.setValue("全部");
        
        // 初始化借用状态过滤选项
        borrowStatusFilter.setItems(FXCollections.observableArrayList(
            "全部", "待审批", "已批准", "已拒绝", "已归还"
        ));
        borrowStatusFilter.setValue("全部");
        
        // 初始化表格列
        setupDeviceTable();
        setupBorrowingTable();
        
        // 加载数据
        loadDeviceData();
        loadBorrowingData();
    }
    
    public void setUsername(String username) {
        this.username = username;
        userInfoLabel.setText("欢迎, " + username);
    }
    
    private void setupDeviceTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        // 操作列需要单独设置，添加"申请借用"按钮
    }
    
    private void setupBorrowingTable() {
        // 设置借用记录表格的列映射
    }
    
    private void loadDeviceData() {
        // TODO: 从数据库加载设备数据，普通用户只能看到空闲设备
    }
    
    private void loadBorrowingData() {
        // TODO: 从数据库加载当前用户的借用记录
    }
    
    @FXML
    protected void handleSearch() {
        String keyword = searchField.getText().trim();
        String status = deviceStatusFilter.getValue();
        
        // TODO: 根据关键字和状态过滤设备
    }
    
    @FXML
    protected void handleReset() {
        searchField.clear();
        deviceStatusFilter.setValue("全部");
        loadDeviceData();
    }
    
    @FXML
    protected void handleRefreshBorrowings() {
        loadBorrowingData();
    }
    
    @FXML
    protected void handleSearchKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleSearch();
        }
    }
    
    @FXML
    protected void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) userInfoLabel.getScene().getWindow();
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
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}