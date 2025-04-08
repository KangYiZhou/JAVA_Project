package app.util;

import java.util.logging.Logger;

public class SessionManager {
    private static final Logger LOGGER = Logger.getLogger(SessionManager.class.getName());
    private static SessionManager instance;
    
    private String currentUsername;
    private boolean isAdmin;
    private boolean isLoggedIn;
    
    private SessionManager() {
        // 私有构造函数
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void login(String username, boolean isAdmin) {
        this.currentUsername = username;
        this.isAdmin = isAdmin;
        this.isLoggedIn = true;
        LOGGER.info("用户登录状态已保存: " + username + ", 管理员: " + isAdmin);
    }
    
    public void logout() {
        this.currentUsername = null;
        this.isAdmin = false;
        this.isLoggedIn = false;
        LOGGER.info("用户已登出");
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
