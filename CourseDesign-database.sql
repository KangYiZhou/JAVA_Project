
# 创建数据库


create database if not exists courseDesign;

use courseDesign;


# 普通用户表
drop table if exists user;

create table if not exists user(
                                   Uid int primary key auto_increment,
                                   UName varchar(50) not null unique ,
                                   UPwd varchar(50) not null,
                                   URole varchar(20) not null default'student',
                                   UQuestion varchar(50),
                                   UAnswer varchar(50)
);

# 管理员用户表，不对外开放,只支持在数据库中增删改查

drop table if exists administrator;

create table if not exists administrator(
                                            id int primary key auto_increment,
                                            name varchar(10) not null unique ,
                                            password varchar(10) not null
);

# 设备表

drop table if exists equipment;

create table if not exists equipment(
                                        EId int primary key auto_increment,
                                        EName varchar(50) not null ,
                                        EStatus varchar(50) not null default'avaiable',
                                        EDescription varchar(99)
);


# 借用记录表

drop table if exists equipmentBorrow;

CREATE TABLE Borrow_records (
                                BId INT AUTO_INCREMENT PRIMARY KEY,
                                UId INT NOT NULL,
                                EId INT NOT NULL,
                                BBorrowTime DATE NOT NULL,
                                BReturnTime DATE,
                                BStatus VARCHAR(50) DEFAULT 'pending',
    -- 定义外键约束，UId引用用户表中的用户编号字段（假设用户表名为Users，字段名为UId）
                                FOREIGN KEY (UId) REFERENCES user (UId),
    -- 定义外键约束，EId引用设备表中的设备编号字段（假设设备表名为Equipment，字段名为EId）
                                FOREIGN KEY (EId) REFERENCES Equipment(EId)
);

# 设备信息表

drop table if exists equipmentType;

create table if not exists equipmentType(
                                            Lid integer primary key  auto_increment,
                                            LName varchar(50) not null,
                                            Ltype varchar(50),
                                            LQuantity integer not null,
                                            LManufacturer varchar(50),
                                            LPurchasedate date,
                                            LWarrantyperiod integer,
                                            LLocation varchar(50),
                                            LRemarks varchar(50)
);

# 用户信息表

drop table if exists BorrowPeopel;

create table if not exists BorrowPeopel(
                                           UId integer primary key ,
                                           UGender varchar(4) not null default'male',
                                           UProvince varchar(20) not null,
                                           Ucity varchar(20) not null,
                                           UContact varchar(20) not null,
                                           UName varchar(20) not null,
                                           UAddress varchar(50) not null,
                                           UStudentId varchar(20) not null,
                                           UEmail varchar(20) not null,
                                           UCredit varchar(20)
);
