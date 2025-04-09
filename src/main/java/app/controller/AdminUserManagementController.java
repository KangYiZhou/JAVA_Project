package app.controller;

import app.service.DeviceManagementService;
import app.entity.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class AdminUserManagementController {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> userIdColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, Boolean> isAdminColumn;

    private DeviceManagementService service = new DeviceManagementService();

    @FXML
    private void initializeuser() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        isAdminColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = service.getAllUsers();
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        userTable.setItems(userList);
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (service.deleteUser(selectedUser.getId())) {
                loadUsers();
            }
        }
    }
}