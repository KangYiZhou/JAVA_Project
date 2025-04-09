module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens app to javafx.fxml;
    opens app.controller to javafx.fxml;
    opens app.entity to javafx.base;
    exports app;
    exports app.controller;
}