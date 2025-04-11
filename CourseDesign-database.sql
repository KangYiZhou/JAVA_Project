-- 创建数据库
CREATE DATABASE IF NOT EXISTS courseDesign DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE courseDesign;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    Uid INT PRIMARY KEY AUTO_INCREMENT,
    UName VARCHAR(50) NOT NULL UNIQUE,
    UPwd VARCHAR(50) NOT NULL,
    URole VARCHAR(20) NOT NULL DEFAULT '普通用户',  -- '管理员'或'普通用户'
    UQuestion VARCHAR(100),
    UAnswer VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建设备表
CREATE TABLE IF NOT EXISTS devices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    model VARCHAR(50),
    manufacturer VARCHAR(100),
    purchase_date DATE,
    status VARCHAR(20) NOT NULL DEFAULT '空闲',  -- '空闲'、'已借出'、'维修中'
    description TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建借用申请表
CREATE TABLE IF NOT EXISTS borrowrequests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    device_id INT NOT NULL,
    user_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    expected_return_date DATE NOT NULL,
    actual_return_date DATE,
    purpose VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT '待审批', -- '待审批'、'已批准'、'已拒绝'、'已借出'、'已归还'
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(id),
    FOREIGN KEY (user_id) REFERENCES users(Uid)
);

-- 添加索引以提高查询性能
CREATE INDEX idx_device_status ON devices(status);
CREATE INDEX idx_borrowrequest_status ON borrowrequests(status);
CREATE INDEX idx_borrowrequest_user ON borrowrequests(user_id);
CREATE INDEX idx_borrowrequest_device ON borrowrequests(device_id);

-- 初始化数据 - 添加默认管理员账户
INSERT INTO users (UName, UPwd, URole, UQuestion, UAnswer) 
VALUES ('admin', '123456', '管理员', '这是管理员账号的密保问题', '这是答案');

-- 初始化数据 - 添加一个测试用普通用户
INSERT INTO users (UName, UPwd, URole, UQuestion, UAnswer) 
VALUES ('test', '123456', '普通用户', '这是测试账号的密保问题', '这是答案');

-- 初始化数据 - 添加一些测试设备
INSERT INTO devices (name, type, model, manufacturer, purchase_date, status, description) VALUES
('笔记本电脑', '电脑', 'ThinkPad X1', 'Lenovo', '2023-01-15', '空闲', '高性能商务笔记本电脑'),
('投影仪', '办公设备', 'Epson EB-U05', 'Epson', '2023-02-20', '空闲', '高清投影仪，1920×1080分辨率'),
('打印机', '办公设备', 'HP LaserJet Pro M15w', 'HP', '2023-03-10', '空闲', '黑白激光打印机'),
('平板电脑', '移动设备', 'iPad Pro', 'Apple', '2023-04-05', '空闲', '12.9英寸，2021款'),
('显示器', '电脑外设', 'Dell U2720Q', 'Dell', '2023-05-12', '空闲', '27英寸4K显示器');

-- 初始化数据 - 添加一些测试借用申请记录
INSERT INTO borrowrequests (device_id, user_id, borrow_date, expected_return_date, purpose, status) VALUES
(1, 2, '2023-06-01', '2023-06-10', '项目演示需要', '待审批'),
(2, 2, '2023-06-05', '2023-06-15', '会议演示', '已批准'),
(3, 2, '2023-05-20', '2023-06-20', '文档打印', '已归还');

-- 已归还的设备添加实际归还日期
UPDATE borrowrequests SET actual_return_date = '2023-06-18' WHERE id = 3;