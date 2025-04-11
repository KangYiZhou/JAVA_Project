package app.controller;

import java.util.List;
import java.util.logging.Logger;

import app.entity.Device;
import app.entity.User;
import app.entity.BorrowRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import app.service.DeviceManagementService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.application.Platform;
import javafx.scene.layout.HBox;

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
    private TableView<BorrowRequest> requestTableView;
    
    @FXML
    private TableColumn<BorrowRequest, Integer> requestIdColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestUserColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestDeviceColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestDateColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestReturnDateColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestPurposeColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestStatusColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> requestActionColumn;
    
    @FXML
    private ComboBox<String> requestStatusFilter;
    
    @FXML
    private TableView<BorrowRequest> returnTableView;
    
    @FXML
    private TableColumn<BorrowRequest, Integer> returnIdColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> returnUserColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> returnDeviceColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> borrowingDateColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> expectedReturnDateColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> actualReturnDateColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> returnStatusColumn;
    
    @FXML
    private TableColumn<BorrowRequest, String> returnActionColumn;
    
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
            "全部", "待审批", "已批准", "已拒绝","已归还"
        ));
        requestStatusFilter.setValue("待审批");
        
        // 初始化表格列
        setupDeviceTable();
        setupRequestTable();
        setupReturnTable();
        setupUserTable(); // 添加用户表格初始化
        
        // 加载数据
        loadDeviceData();
        loadRequestData();
        loadReturnData();
        loadUsers(); // 加载用户数据
    }
    
    public void setAdminInfo(String username) {
        this.adminUsername = username;
        adminInfoLabel.setText("欢迎, " + username);
        LOGGER.info("管理员信息已设置: " + username);
    }
    
    private void setupDeviceTable() {
        adminIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        adminNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        adminTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        adminModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        adminStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // 配置操作列，添加删除设备和修改状态按钮
        adminActionColumn.setCellFactory(param -> new TableCell<Device, String>() {
            private final Button deleteBtn = new Button("删除");
            private final Button statusBtn = new Button("修改状态");
            private final HBox buttonBox = new HBox(5, deleteBtn, statusBtn);
            
            {
                // 设置删除按钮点击事件
                deleteBtn.setOnAction(event -> {
                    Device device = getTableView().getItems().get(getIndex());
                    handleDeleteDevice(device);
                });
                
                // 设置修改状态按钮点击事件
                statusBtn.setOnAction(event -> {
                    Device device = getTableView().getItems().get(getIndex());
                    handleChangeDeviceStatus(device);
                });
                
                // 设置按钮样式
                deleteBtn.getStyleClass().add("delete-button");
                statusBtn.getStyleClass().add("edit-button");
                buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
    }
    
    private void setupRequestTable() {
        requestIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        requestUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        requestDeviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        requestDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        requestReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        requestPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        requestStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        requestActionColumn.setCellFactory(param -> new TableCell<BorrowRequest, String>() {
            private final Button approveBtn = new Button("批准");
            private final Button rejectBtn = new Button("拒绝");
            private final HBox buttonBox = new HBox(5, approveBtn, rejectBtn);
            
            {
                approveBtn.setOnAction(event -> {
                    BorrowRequest request = getTableView().getItems().get(getIndex());
                    handleApproveRequest(request);
                });
                
                rejectBtn.setOnAction(event -> {
                    BorrowRequest request = getTableView().getItems().get(getIndex());
                    handleRejectRequest(request);
                });
                
                buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    BorrowRequest request = getTableView().getItems().get(getIndex());
                    if ("待审批".equals(request.getStatus())) {
                        setGraphic(buttonBox);
                    } else {
                        setGraphic(new Label(request.getStatus()));
                    }
                }
            }
        });
    }

    private void setupReturnTable() {
        returnIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        returnUserColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        returnDeviceColumn.setCellValueFactory(new PropertyValueFactory<>("deviceName"));
        borrowingDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        expectedReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        actualReturnDateColumn.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));
        returnStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        returnActionColumn.setCellFactory(param -> new TableCell<BorrowRequest, String>() {
            private final Button returnBtn = new Button("确认归还");
            
            {
                returnBtn.setOnAction(event -> {
                    BorrowRequest request = getTableView().getItems().get(getIndex());
                    handleReturnDevice(request);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    BorrowRequest request = getTableView().getItems().get(getIndex());
                    if ("已批准".equals(request.getStatus()) || "已借出".equals(request.getStatus())) {
                        setGraphic(returnBtn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadDeviceData() {
        try {
            System.out.println("管理员界面：开始加载设备数据...");
            DeviceManagementService service = new DeviceManagementService();
            List<Device> devices = service.getAllDevices();
            
            if (devices.isEmpty()) {
                System.out.println("管理员界面：没有找到设备数据");
            } else {
                System.out.println("管理员界面：成功加载 " + devices.size() + " 个设备");
                adminDeviceTableView.getItems().clear();
                adminDeviceTableView.getItems().addAll(devices);
                adminDeviceTableView.refresh();
            }
        } catch (Exception e) {
            System.out.println("管理员界面：加载设备数据时发生错误: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "加载设备数据失败：" + e.getMessage());
        }
    }
    
    private void loadRequestData() {
        try {
            DeviceManagementService service = new DeviceManagementService();
            String status = requestStatusFilter.getValue();
            List<BorrowRequest> requests;
            
            if ("全部".equals(status)) {
                requests = service.getAllRequests();
            } else {
                requests = service.getRequestsByStatus(status);
            }
            
            requestTableView.getItems().clear();
            requestTableView.getItems().addAll(requests);
        } catch (Exception e) {
            showAlert("错误", "加载借用申请数据失败：" + e.getMessage());
        }
    }
    
    private void loadReturnData() {
        try {
            DeviceManagementService service = new DeviceManagementService();
            // 获取已批准或已借出的请求
            List<BorrowRequest> requests = new ArrayList<>();
            requests.addAll(service.getRequestsByStatus("已批准"));
            requests.addAll(service.getRequestsByStatus("已借出"));
            
            returnTableView.getItems().clear();
            returnTableView.getItems().addAll(requests);
        } catch (Exception e) {
            showAlert("错误", "加载设备归还数据失败：" + e.getMessage());
        }
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
        String keyword = adminSearchField.getText().trim().toLowerCase();
        String status = adminDeviceStatusFilter.getValue();
        
        try {
            // 获取所有设备
            DeviceManagementService service = new DeviceManagementService();
            List<Device> allDevices = service.getAllDevices();
            List<Device> filteredDevices = new ArrayList<>();
            
            // 根据关键字和状态进行过滤
            for (Device device : allDevices) {
                boolean matchesKeyword = keyword.isEmpty() || 
                                        device.getName().toLowerCase().contains(keyword) ||
                                        device.getType().toLowerCase().contains(keyword) ||
                                        device.getModel().toLowerCase().contains(keyword) ||
                                        (device.getManufacturer() != null && 
                                         device.getManufacturer().toLowerCase().contains(keyword));
                
                boolean matchesStatus = "全部".equals(status) || 
                                       (device.getStatus() != null && device.getStatus().equals(status));
                
                if (matchesKeyword && matchesStatus) {
                    filteredDevices.add(device);
                }
            }
            
            // 更新表格显示
            adminDeviceTableView.getItems().clear();
            adminDeviceTableView.getItems().addAll(filteredDevices);
            
            System.out.println("搜索结果: 找到 " + filteredDevices.size() + " 个设备");
            
            if (filteredDevices.isEmpty()) {
                showAlert("提示", "没有找到符合条件的设备");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "搜索过程中发生错误: " + e.getMessage());
        }
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
            // 加载主界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            Parent root = loader.load();
            
            // 不需要手动传递用户信息，会话管理器已保存
            MainController controller = loader.getController();
            // 确保UI更新
            controller.updateUIFromSession();
            
            Stage stage = (Stage) adminInfoLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("设备管理系统");
            stage.setScene(scene);
            stage.setMaximized(true);
            
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


    @FXML
    private TableView<User> userTableView; // 从userTable修改为userTableView
    @FXML
    private TableColumn<User, Integer> userIdColumn; // 保持不变
    @FXML
    private TableColumn<User, String> userNameColumn; // 从usernameColumn修改为userNameColumn
    @FXML
    private TableColumn<User, String> userRoleColumn; // 从isAdminColumn修改为userRoleColumn
    @FXML
    private TableColumn<User, String> userPasswordColumn;
    @FXML
    private TextField userSearchField; // 添加搜索字段引用

    private DeviceManagementService service = new DeviceManagementService();


    @FXML
    private void initializeuser() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = service.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        userTableView.setItems(userList);
    }

    

    // 新增用户表格设置方法
    private void setupUserTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role")); // 使用role属性而不是isAdmin
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password")); // 添加此行配置密码列
        
        // 可选：添加删除按钮列
        TableColumn<User, Void> actionColumn = new TableColumn<>("操作");
        actionColumn.setPrefWidth(100);
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("删除");
            
            {
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDeleteUserAction(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        
        userTableView.getColumns().add(actionColumn);
    }

    @FXML
    protected void handleUserSearch() {
        String keyword = userSearchField.getText().trim().toLowerCase();
        
        try {
            DeviceManagementService service = new DeviceManagementService();
            List<User> allUsers = service.getAllUsers();
            List<User> filteredUsers = new ArrayList<>();
            
            for (User user : allUsers) {
                if (user.getUsername().toLowerCase().contains(keyword)) {
                    filteredUsers.add(user);
                }
            }
            
            userTableView.getItems().clear();
            userTableView.getItems().addAll(filteredUsers);
            
            System.out.println("用户搜索结果: 找到 " + filteredUsers.size() + " 个用户");
            
            if (filteredUsers.isEmpty()) {
                showAlert("提示", "没有找到符合条件的用户");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "搜索用户过程中发生错误: " + e.getMessage());
        }
    }

    @FXML
    protected void handleUserReset() {
        userSearchField.clear();
        loadUsers(); // 重新加载所有用户
    }

    // 处理删除用户按钮点击
    @FXML
    protected void handleDeleteUser() {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();
        handleDeleteUserAction(selectedUser);
    }

    // 删除用户的实际处理逻辑
    private void handleDeleteUserAction(User selectedUser) {
        if (selectedUser == null) {
            showAlert("提示", "请先选择要删除的用户");
            return;
        }
        
        // 确认删除对话框
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认删除");
        confirmDialog.setHeaderText("删除用户");
        confirmDialog.setContentText("您确定要删除用户 " + selectedUser.getUsername() + " 吗？");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DeviceManagementService service = new DeviceManagementService();
                    boolean success = service.deleteUser(selectedUser.getId());
                    
                    if (success) {
                        loadUsers(); // 重新加载用户列表
                        showAlert("成功", "用户已成功删除");
                    } else {
                        showAlert("错误", "删除用户失败，请重试");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("错误", "删除用户时发生异常: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    protected void handleAddUser() {
        // 创建一个对话框
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("添加新用户");
        dialog.setHeaderText("请输入新用户的信息");
        
        // 设置按钮
        ButtonType addButtonType = new ButtonType("添加", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // 创建表单内容
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("用户名");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("普通用户", "管理员");
        roleComboBox.setValue("普通用户");
        
        grid.add(new Label("用户名:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("密码:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("用户身份:"), 0, 2);
        grid.add(roleComboBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // 请求焦点在用户名字段
        Platform.runLater(() -> usernameField.requestFocus());
        
        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                String role = roleComboBox.getValue();
                
                if (username.isEmpty() || password.isEmpty()) {
                    showAlert("错误", "用户名和密码不能为空");
                    return null;
                }
                
                // 创建新用户
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setRole(role);
                
                return newUser;
            }
            return null;
        });
        
        // 显示对话框并处理结果
        Optional<User> result = dialog.showAndWait();
        
        result.ifPresent(user -> {
            try {
                DeviceManagementService service = new DeviceManagementService();
                boolean success = service.registerUser(user);
                
                if (success) {
                    loadUsers(); // 重新加载用户列表
                    showAlert("成功", "用户 " + user.getUsername() + " 已成功添加");
                } else {
                    showAlert("错误", "添加用户失败，可能用户名已存在");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("错误", "添加用户时发生异常: " + e.getMessage());
            }
        });
    }

    private void handleDeleteDevice(Device device) {
        // 确认删除对话框
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认删除");
        confirmDialog.setHeaderText("删除设备");
        confirmDialog.setContentText("您确定要删除设备 '" + device.getName() + "' 吗？此操作不可撤销。");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    DeviceManagementService service = new DeviceManagementService();
                    boolean success = service.deleteDevice(device.getId());
                    
                    if (success) {
                        loadDeviceData(); // 重新加载设备列表
                        showAlert("成功", "设备已成功删除。");
                    } else {
                        showAlert("错误", "删除设备失败，请重试。");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("错误", "删除设备时发生异常: " + e.getMessage());
                }
            }
        });
    }

    private void handleChangeDeviceStatus(Device device) {
        // 创建一个对话框
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("修改设备状态");
        dialog.setHeaderText("请选择设备 '" + device.getName() + "' 的新状态:");
        
        // 添加按钮
        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // 创建状态下拉框
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("空闲", "已借出", "维修中");
        statusComboBox.setValue(device.getStatus()); // 设置当前状态为默认值
        
        // 布局
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("设备状态:"), 0, 0);
        grid.add(statusComboBox, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        // 设置对话框结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return statusComboBox.getValue();
            }
            return null;
        });
        
        // 显示对话框并处理结果
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(newStatus -> {
            try {
                DeviceManagementService service = new DeviceManagementService();
                
                // 创建设备数据更新Map
                Map<String, Object> deviceData = new HashMap<>();
                deviceData.put("id", device.getId());
                deviceData.put("name", device.getName());
                deviceData.put("type", device.getType());
                deviceData.put("model", device.getModel());
                deviceData.put("manufacturer", device.getManufacturer());
                deviceData.put("purchaseDate", device.getPurchaseDate());
                deviceData.put("status", newStatus);
                deviceData.put("description", device.getDescription());
                
                boolean success = service.updateDevice(deviceData);
                
                if (success) {
                    loadDeviceData(); // 重新加载设备列表
                    showAlert("成功", "设备状态已更新为: " + newStatus);
                } else {
                    showAlert("错误", "更新设备状态失败，请重试。");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("错误", "更新设备状态时发生异常: " + e.getMessage());
            }
        });
    }

    // 批准借用申请
    private void handleApproveRequest(BorrowRequest request) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认批准");
        confirmDialog.setHeaderText("批准借用申请");
        confirmDialog.setContentText("您确定要批准用户 " + request.getUserName() + " 借用设备 " + request.getDeviceName() + " 的申请吗？");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DeviceManagementService service = new DeviceManagementService();
                boolean success = service.approveRequest(request.getId());
                
                if (success) {
                    loadRequestData();
                    showAlert("成功", "已批准借用申请");
                } else {
                    showAlert("错误", "操作失败，请重试");
                }
            }
        });
    }

    // 拒绝借用申请
    private void handleRejectRequest(BorrowRequest request) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认拒绝");
        confirmDialog.setHeaderText("拒绝借用申请");
        confirmDialog.setContentText("您确定要拒绝用户 " + request.getUserName() + " 借用设备 " + request.getDeviceName() + " 的申请吗？");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DeviceManagementService service = new DeviceManagementService();
                boolean success = service.rejectRequest(request.getId());
                
                if (success) {
                    loadRequestData();
                    showAlert("成功", "已拒绝借用申请");
                } else {
                    showAlert("错误", "操作失败，请重试");
                }
            }
        });
    }

    // 确认设备归还
    private void handleReturnDevice(BorrowRequest request) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("确认归还");
        confirmDialog.setHeaderText("确认设备归还");
        confirmDialog.setContentText("您确定要确认用户 " + request.getUserName() + " 归还设备 " + request.getDeviceName() + " 吗？");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                DeviceManagementService service = new DeviceManagementService();
                boolean success = service.returnDevice(request.getId());
                
                if (success) {
                    loadReturnData();
                    showAlert("成功", "已确认设备归还");
                } else {
                    showAlert("错误", "操作失败，请重试");
                }
            }
        });
    }

    // 在AdminMainController.java中添加此方法
    public void selectRequestTab() {
        // 选择借用申请审核的选项卡（索引为1，因为它是第二个选项卡）
        adminTabPane.getSelectionModel().select(1);
        
        // 可选：自动筛选为只显示"待审批"状态的申请
        if (requestStatusFilter != null && requestStatusFilter.getItems() != null) {
            requestStatusFilter.setValue("待审批");
            loadRequestData();
        }
    }
}