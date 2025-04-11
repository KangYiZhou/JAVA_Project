package app.controller;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import app.entity.BorrowRequest;
import app.entity.Device;
import app.entity.User; // 添加这行到文件顶部的导入区域
import app.util.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.List;
import javafx.collections.ObservableList;
import app.service.DeviceManagementService;
import java.time.LocalDate;

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

        // 后续可加载数据
        loadDeviceData();
        // 初始化时从SessionManager获取用户信息
        updateUIFromSession();
    }

    // 更新为公开方法，可以从其他控制器调用
    public void updateUIFromSession() {
        SessionManager session = SessionManager.getInstance();
        
        boolean loggedIn = session.isLoggedIn();
        LOGGER.info("用户是否登录: " + loggedIn);
        
        if (loggedIn) {
            String username = session.getCurrentUsername();
            boolean isAdmin = session.isAdmin();
            
            LOGGER.info("当前用户: " + username + ", 是否是管理员: " + isAdmin);
            LOGGER.info("添加设备菜单是否可见: " + addDeviceMenuItem.isVisible());
            
            currentUserLabel.setText(username);
            userRoleLabel.setText(isAdmin ? "管理员" : "普通用户");
            
            // 设置管理员特定UI元素可见性
            addDeviceMenuItem.setVisible(isAdmin);
            approvalsMenuItem.setVisible(isAdmin);
            adminButton.setVisible(isAdmin);
            
            LOGGER.info("设置后添加设备菜单是否可见: " + addDeviceMenuItem.isVisible());
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
        try {
            System.out.println("开始加载设备数据...");
            DeviceManagementService service = new DeviceManagementService();
            List<Device> devices = service.getAllDevices();
            
            if (devices.isEmpty()) {
                System.out.println("没有找到设备数据");
            } else {
                // 检查第一个设备数据
                Device firstDevice = devices.get(0);
                System.out.println("首个设备信息: ID=" + firstDevice.getId() + ", 名称=" + firstDevice.getName());
                
                // 清空并添加新数据
                deviceTableView.getItems().clear();
                deviceTableView.getItems().addAll(devices);
                
                // 强制刷新表格
                deviceTableView.refresh();
                System.out.println("表格中设备数量: " + deviceTableView.getItems().size());
            }
        } catch (Exception e) {
            System.out.println("加载设备数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleUserInfo() {
        // 检查用户是否已登录
        SessionManager sessionManager = SessionManager.getInstance();
        String username = sessionManager.getCurrentUsername();
        
        if (username == null || username.isEmpty()) {
            showAlert("提示", "您需要先登录才能查看个人信息");
            return;
        }
        
        try {
            // 获取用户详细信息
            DeviceManagementService service = new DeviceManagementService();
            User currentUser = service.getUserByUsername(username);
            
            if (currentUser == null) {
                showAlert("错误", "无法获取用户信息");
                return;
            }
            
            // 创建用户信息对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("用户信息");
            dialog.setHeaderText("用户详细信息");
            
            // 设置对话框按钮
            ButtonType changePasswordButtonType = new ButtonType("修改密码", ButtonBar.ButtonData.LEFT);
            ButtonType closeButtonType = ButtonType.CLOSE;
            dialog.getDialogPane().getButtonTypes().addAll(changePasswordButtonType, closeButtonType);
            
            // 创建内容布局
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // 添加用户信息字段
            grid.add(new Label("用户ID:"), 0, 0);
            grid.add(new Label(String.valueOf(currentUser.getId())), 1, 0);
            
            grid.add(new Label("用户名:"), 0, 1);
            grid.add(new Label(currentUser.getUsername()), 1, 1);
            
            grid.add(new Label("用户角色:"), 0, 2);
            grid.add(new Label("管理员".equals(currentUser.getRole()) ? "管理员" : "普通用户"), 1, 2);
            
            grid.add(new Label("密保问题:"), 0, 3);
            grid.add(new Label(currentUser.getQuestion()), 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            
            // 显示对话框并处理结果
            Optional<ButtonType> result = dialog.showAndWait();
            
            // 处理修改密码请求
            if (result.isPresent() && result.get() == changePasswordButtonType) {
                handleChangePassword(currentUser);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "显示用户信息失败", e);
            showAlert("错误", "无法显示用户信息：" + e.getMessage());
        }
    }

    // 处理修改密码
    private void handleChangePassword(User user) {
        // 创建修改密码对话框
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("修改密码");
        dialog.setHeaderText("请输入新密码");
        
        // 设置按钮
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("当前密码");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("新密码");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("确认新密码");
        
        grid.add(new Label("当前密码:"), 0, 0);
        grid.add(oldPasswordField, 1, 0);
        grid.add(new Label("新密码:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("确认新密码:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // 请求焦点在旧密码字段
        Platform.runLater(() -> oldPasswordField.requestFocus());
        
        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return oldPasswordField.getText();
            }
            return null;
        });
        
        // 显示对话框并处理结果
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(oldPassword -> {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            // 验证输入
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showAlert("错误", "所有密码字段都为必填项");
                return;
            }
            
            if (!oldPassword.equals(user.getPassword())) {
                showAlert("错误", "当前密码不正确");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                showAlert("错误", "两次输入的新密码不一致");
                return;
            }
            
            if (newPassword.length() < 6) {
                showAlert("错误", "新密码长度至少为6个字符");
                return;
            }
            
            // 执行密码更新
            try {
                DeviceManagementService service = new DeviceManagementService();
                user.setPassword(newPassword);
                boolean success = service.updateUser(user);
                
                if (success) {
                    showAlert("成功", "密码已成功更新");
                } else {
                    showAlert("错误", "密码更新失败，请重试");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "更新密码失败", e);
                showAlert("错误", "更新密码失败：" + e.getMessage());
            }
        });
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
        try {
            // 获取当前登录用户
            SessionManager sessionManager = SessionManager.getInstance();
            String username = sessionManager.getCurrentUsername();
            
            if (username == null || username.isEmpty()) {
                showAlert("提示", "您需要先登录才能查看借用记录");
                return;
            }
            
            // 获取用户ID
            DeviceManagementService service = new DeviceManagementService();
            User currentUser = service.getUserByUsername(username);
            
            if (currentUser == null) {
                showAlert("错误", "无法获取用户信息");
                return;
            }
            
            // 创建用于显示借用记录的弹窗
            Stage borrowingsStage = new Stage();
            borrowingsStage.setTitle("我的借用记录");
            
            // 创建表格视图
            TableView<BorrowRequest> borrowingsTable = new TableView<>();
            borrowingsTable.setPrefWidth(900);
            borrowingsTable.setPrefHeight(500);
            
            // 创建表格列
            TableColumn<BorrowRequest, Integer> idColumn = new TableColumn<>("申请ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            idColumn.setPrefWidth(60);
            
            TableColumn<BorrowRequest, String> deviceColumn = new TableColumn<>("设备名称");
            deviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
            deviceColumn.setPrefWidth(150);
            
            TableColumn<BorrowRequest, LocalDate> borrowDateColumn = new TableColumn<>("借用日期");
            borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
            borrowDateColumn.setPrefWidth(120);
            
            TableColumn<BorrowRequest, LocalDate> returnDateColumn = new TableColumn<>("预计归还日期");
            returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
            returnDateColumn.setPrefWidth(120);
            
            TableColumn<BorrowRequest, LocalDate> actualReturnDateColumn = new TableColumn<>("实际归还日期");
            actualReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
            actualReturnDateColumn.setPrefWidth(120);
            
            TableColumn<BorrowRequest, String> purposeColumn = new TableColumn<>("借用目的");
            purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
            purposeColumn.setPrefWidth(150);
            
            TableColumn<BorrowRequest, String> statusColumn = new TableColumn<>("状态");
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusColumn.setPrefWidth(100);
            
            // 添加列到表格
            borrowingsTable.getColumns().addAll(
                idColumn, deviceColumn, borrowDateColumn, returnDateColumn, 
                actualReturnDateColumn, purposeColumn, statusColumn
            );
            
            // 获取用户的借用记录
            List<BorrowRequest> userBorrowings = service.getRequestsByUserId(currentUser.getId());
            
            // 将数据添加到表格
            borrowingsTable.getItems().addAll(userBorrowings);
            
            // 创建主布局
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));
            
            // 添加标题和状态统计
            Label titleLabel = new Label("我的借用记录");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            // 统计不同状态的申请数量
            long pendingCount = userBorrowings.stream().filter(r -> "待审批".equals(r.getStatus())).count();
            long approvedCount = userBorrowings.stream().filter(r -> "已批准".equals(r.getStatus()) || "已借出".equals(r.getStatus())).count();
            long rejectedCount = userBorrowings.stream().filter(r -> "已拒绝".equals(r.getStatus())).count();
            long returnedCount = userBorrowings.stream().filter(r -> "已归还".equals(r.getStatus())).count();
            
            // 创建状态统计信息
            HBox statsBox = new HBox(20);
            statsBox.getChildren().addAll(
                new Label("总申请数: " + userBorrowings.size()),
                new Label("待审批: " + pendingCount),
                new Label("使用中: " + approvedCount),
                new Label("已拒绝: " + rejectedCount),
                new Label("已归还: " + returnedCount)
            );
            
            // 添加组件到布局
            root.getChildren().addAll(titleLabel, statsBox, borrowingsTable);
            
            // 创建刷新按钮
            Button refreshButton = new Button("刷新");
            refreshButton.setOnAction(e -> {
                List<BorrowRequest> refreshedBorrowings = service.getRequestsByUserId(currentUser.getId());
                borrowingsTable.getItems().clear();
                borrowingsTable.getItems().addAll(refreshedBorrowings);
            });
            
            // 创建关闭按钮
            Button closeButton = new Button("关闭");
            closeButton.setOnAction(e -> borrowingsStage.close());
            
            // 添加按钮到布局
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.getChildren().addAll(refreshButton, closeButton);
            root.getChildren().add(buttonBox);
            
            // 设置场景
            Scene scene = new Scene(root);
            borrowingsStage.setScene(scene);
            borrowingsStage.show();
            
            LOGGER.info("显示用户借用记录，共" + userBorrowings.size() + "条");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "显示借用记录失败", e);
            showAlert("错误", "无法显示借用记录：" + e.getMessage());
        }
    }

    @FXML
    protected void handleApprovals() {
        // 首先检查用户权限
        if (!SessionManager.getInstance().isAdmin()) {
            showAlert("权限不足", "只有管理员才能查看和处理待审批申请");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminMain.fxml"));
            Parent root = loader.load();
            
            // 获取控制器并设置管理员信息
            AdminMainController controller = loader.getController();
            controller.setAdminInfo(SessionManager.getInstance().getCurrentUsername());
            
            // 选择"借用申请审核"选项卡
            controller.selectRequestTab();
            
            // 打开新窗口显示待审批列表
            Stage stage = new Stage();
            stage.setTitle("设备借用申请审核");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();
            
            LOGGER.info("打开待审批申请列表");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "打开待审批列表失败", e);
            showAlert("错误", "打开待审批列表失败：" + e.getMessage());
        }
    }

    @FXML
    protected void handleSearch() {
        String keyword = searchField.getText().trim();
        // TODO: 根据关键字搜索设备
    }

    @FXML
    protected void handleBorrowDevice() {
        // 获取选中的设备
        Device selectedDevice = (Device) deviceTableView.getSelectionModel().getSelectedItem();
        if (selectedDevice == null) {
            showAlert("提示", "请先选择一个设备");
            return;
        }
        
        // 检查设备状态
        if (!"空闲".equals(selectedDevice.getStatus())) {
            showAlert("提示", "该设备当前不可借用，状态为：" + selectedDevice.getStatus());
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BorrowRequest.fxml"));
            Parent root = loader.load();

            // 初始化借用表单数据
            BorrowRequestController controller = loader.getController();
            controller.setDevice(selectedDevice);

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
        try {
            // 获取当前登录用户
            SessionManager sessionManager = SessionManager.getInstance();
            String username = sessionManager.getCurrentUsername();
            
            if (username == null || username.isEmpty()) {
                showAlert("提示", "您需要先登录才能归还设备");
                return;
            }
            
            // 获取用户ID
            DeviceManagementService service = new DeviceManagementService();
            User currentUser = service.getUserByUsername(username);
            
            if (currentUser == null) {
                showAlert("错误", "无法获取用户信息");
                return;
            }
            
            // 获取用户已借用的设备(状态为"已批准"或"已借出")
            List<BorrowRequest> borrowedDevices = service.getRequestsByUserId(currentUser.getId())
                .stream()
                .filter(r -> "已批准".equals(r.getStatus()) || "已借出".equals(r.getStatus()))
                .collect(Collectors.toList());
            
            if (borrowedDevices.isEmpty()) {
                showAlert("提示", "您当前没有需要归还的设备");
                return;
            }
            
            // 创建归还设备的弹窗
            Stage returnStage = new Stage();
            returnStage.setTitle("设备归还");
            
            // 创建设备表格
            TableView<BorrowRequest> deviceTableView = new TableView<>();
            deviceTableView.setPrefHeight(300);
            
            // 设置列
            TableColumn<BorrowRequest, Integer> idColumn = new TableColumn<>("申请ID");
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            idColumn.setPrefWidth(60);
            
            TableColumn<BorrowRequest, String> deviceNameColumn = new TableColumn<>("设备名称");
            deviceNameColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
            deviceNameColumn.setPrefWidth(150);
            
            TableColumn<BorrowRequest, LocalDate> borrowDateColumn = new TableColumn<>("借用日期");
            borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
            borrowDateColumn.setPrefWidth(120);
            
            TableColumn<BorrowRequest, LocalDate> returnDateColumn = new TableColumn<>("预计归还日期");
            returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
            returnDateColumn.setPrefWidth(120);
            
            // 添加列到表格
            deviceTableView.getColumns().addAll(idColumn, deviceNameColumn, borrowDateColumn, returnDateColumn);
            
            // 添加数据
            deviceTableView.getItems().addAll(borrowedDevices);
            
            // 创建按钮
            Button returnButton = new Button("申请归还");
            returnButton.setDisable(true);
            
            Button cancelButton = new Button("取消");
            
            // 设置选择监听器
            deviceTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> returnButton.setDisable(newValue == null));
            
            // 设置归还按钮动作
            returnButton.setOnAction(e -> {
                BorrowRequest selectedRequest = deviceTableView.getSelectionModel().getSelectedItem();
                if (selectedRequest != null) {
                    // 确认归还对话框
                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDialog.setTitle("确认归还");
                    confirmDialog.setHeaderText("归还设备");
                    confirmDialog.setContentText("您确定要归还设备 \"" + selectedRequest.getDeviceName() + "\" 吗？");
                    
                    confirmDialog.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            // 通知管理员用户已经准备归还设备
                            boolean success = service.returnDevice(selectedRequest.getId());
                            
                            if (success) {
                                showAlert("成功", "设备归还申请已提交，请将设备交给管理员完成最终确认");
                                returnStage.close();
                                
                                // 刷新设备列表
                                loadDeviceData();
                            } else {
                                showAlert("错误", "归还设备失败，请重试或联系管理员");
                            }
                        }
                    });
                }
            });
            
            // 设置取消按钮动作
            cancelButton.setOnAction(e -> returnStage.close());
            
            // 创建布局
            VBox root = new VBox(10);
            root.setPadding(new Insets(10));
            
            Label titleLabel = new Label("请选择要归还的设备:");
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label noteLabel = new Label("归还后请务必将设备交还给管理员，等待最终确认");
            noteLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");
            
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.getChildren().addAll(returnButton, cancelButton);
            
            root.getChildren().addAll(titleLabel, noteLabel, deviceTableView, buttonBox);
            
            // 设置场景
            Scene scene = new Scene(root, 600, 400);
            returnStage.setScene(scene);
            returnStage.showAndWait();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "归还设备操作失败", e);
            showAlert("错误", "归还设备失败：" + e.getMessage());
        }
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