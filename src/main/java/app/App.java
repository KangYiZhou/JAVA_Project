package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            String fxmlPath = "/view/Login.fxml";
            LOGGER.info("尝试加载FXML: " + fxmlPath);
            LOGGER.info("资源URL: " + getClass().getResource(fxmlPath));

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 确保控制器已经加载
            Object controller = loader.getController();
            LOGGER.info("控制器加载状态: " + (controller != null ? "成功" : "失败"));

            Scene scene = new Scene(root, 600, 400);
            primaryStage.setScene(scene);
            primaryStage.setTitle("设备管理系统 - 登录");

            // 确保窗口大小适当
            primaryStage.setMinWidth(500);
            primaryStage.setMinHeight(350);

            // 显示并居中窗口
            primaryStage.centerOnScreen();
            primaryStage.show();

            // 确保窗口获得焦点
            primaryStage.toFront();
            primaryStage.requestFocus();

            // 打印调试信息确认窗口状态
            LOGGER.info("窗口显示状态: " + primaryStage.isShowing());
            LOGGER.info("窗口大小: " + primaryStage.getWidth() + "x" + primaryStage.getHeight());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "FXML加载错误", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "启动错误", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 设置macOS相关的系统属性
        System.setProperty("apple.awt.application.name", "设备管理系统");
        System.setProperty("apple.awt.application.appearance", "system");
        System.setProperty("prism.order", "sw");
        System.setProperty("glass.platform", "Gtk");  // 绕过Mac的glass实现

        launch(args);
    }
}