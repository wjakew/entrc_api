/*
programmer Jakub Wawak
all rights reserved
kubawawak@gmail.com
version v1.3.2
sql script that reloads ENTRCruntime database
*/
CREATE DATABASE IF NOT EXISTS entrc_database;
USE entrc_database;

-- checking if old data is on the database
drop table if exists ENTRC_IC_LOG;
drop table if exists REC_API_DATA;
drop table if exists ENTRC_IC_GRAVEYARD;
drop table if exists ENTRC_IC_ITEM_LABEL;
drop table if exists ENTRC_IC_ITEM;
drop table if exists ENTRC_IC_DRAWER;
drop table if exists ENTRC_IC_CATEGORY;
drop table if exists ENTRC_GUARD_ENTRANCE;
drop table if exists ENTRC_GUARD_LOG;
drop table if exists ENTRC_GUARD_USER;
drop table if exists ENTRC_GUARD_NUMBERPLATES;
drop table if exists ENTRC_GUARD_TIMESHEET;
drop table if exists PROGRAMCODES;
drop table if exists PROGRAM_LOG;
drop table if exists ERROR_LOG;
drop table if exists CONSOLE;
drop table if exists RUNTIME;
drop table if exists ENTRC_API_DATA;
drop table if exists CONFIGURATION;
drop table if exists USER_MESSAGE;
drop table if exists ENTRANCE_EXIT;
drop table if exists ENTRANCE;
drop table if exists USER_LOG;
drop table if exists DATA_LOG;
drop table if exists GRAVEYARD;
drop table if exists ADMIN_GRAVEYARD;
drop table if exists ADMIN_PRIVILAGES;
drop table if exists TT_GENERATED;
drop table if exists TT_TEMPLATE;
drop table if exists ANNOUNCEMENT;
drop table if exists ADMIN_DATA;
drop table if exists BARCODE_DATA;
drop table if exists PHOTO_LIB;
drop table if exists WORKER_DETAILS;
drop table if exists GROUP_MEMBERS;
drop table if exists USER_GROUPS;
drop table if exists WORKER;

-- table fro storing data for future use
CREATE TABLE PROGRAMCODES
(
programcodes_id INT PRIMARY KEY AUTO_INCREMENT,
programcodes_key VARCHAR(50),
programcodes_value VARCHAR(100)
);
-- table for stroing program log
CREATE TABLE PROGRAM_LOG
(
program_log_id INT PRIMARY KEY AUTO_INCREMENT,
program_log_desc VARCHAR(300)
);
CREATE TABLE ERROR_LOG
(
error_log_id INT PRIMARY KEY AUTO_INCREMENT,
error_log_code VARCHAR(10),
error_log_desc VARCHAR(250)
);
-- table for storing client app configuration
CREATE TABLE RUNTIME
(
runtime_id INT PRIMARY KEY AUTO_INCREMENT,
runtime_license VARCHAR(19),
runtime_macaddress VARCHAR(50)
);
-- table for storing entrc_api data
CREATE TABLE ENTRC_API_DATA
(
entrc_api_data INT PRIMARY KEY AUTO_INCREMENT,
entrc_api_appcode VARCHAR(10),
entrc_api_desc VARCHAR(100)
);
-- table for storing configuration data
CREATE TABLE CONFIGURATION
(
entrc_user_exit_pin VARCHAR(10),
entrc_user_ask_pin VARCHAR(10),
entrc_user_manage_pin VARCHAR(10),
entrc_admin_manage_pin VARCHAR(10),
entrc_admin_show_ip VARCHAR(10)
);
-- table for storing worker data (id0)
CREATE TABLE WORKER
(
worker_id INT PRIMARY KEY AUTO_INCREMENT,
worker_login VARCHAR(30),
worker_name VARCHAR(50),
worker_surname VARCHAR(100),
worker_pin VARCHAR(4),
worker_position VARCHAR(20)
);
-- table for creating worker groups
CREATE TABLE USER_GROUPS
(
user_groups_id INT PRIMARY KEY AUTO_INCREMENT,
user_groups_name VARCHAR(30),
user_groups_desc VARCHAR(250),
object_connected_id INT
);
-- table for creating connection between worker and group
CREATE TABLE GROUP_MEMBERS
(
user_groups_id INT,
worker_id INT,
CONSTRAINT fk_group_members FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
CONSTRAINT fk_group_members2 FOREIGN KEY (user_groups_id) REFERENCES USER_GROUPS(user_groups_id)
);
-- table for storing worker additional data
CREATE TABLE WORKER_DETAILS
(
worker_details_id INT PRIMARY KEY AUTO_INCREMENT,
worker_id INT,
worker_details_salary DOUBLE,
worker_hours_minimum INT,
worker_details_mail VARCHAR(100),

CONSTRAINT fk_workerdetails FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id)
);
-- table for storing photos
CREATE TABLE PHOTO_LIB
(
photo_lib_id INT PRIMARY KEY AUTO_INCREMENT,
worker_id INT,
photo_path VARCHAR(350),
photo_name VARCHAR(100),
photo_source BLOB,

CONSTRAINT fk_photolib FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id)
);
-- table for storing log for barcodes
CREATE TABLE BARCODE_DATA
(
barcode_data_id INT PRIMARY KEY AUTO_INCREMENT,
barcode_date DATE,
worker_id INT,
barcode_raw_data VARCHAR(100),
CONSTRAINT fk_barcodedata FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id)
);
-- table for storing admin data (id1)
CREATE TABLE ADMIN_DATA
(
admin_id INT PRIMARY KEY AUTO_INCREMENT,
admin_login VARCHAR(30),
admin_password VARCHAR(150),
admin_email VARCHAR(50),
admin_level INT,
admin_active INT
);
-- table for storing templates for timetable
CREATE TABLE TT_TEMPLATE
(
tt_template_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
tt_template_date TIMESTAMP,
tt_template_rawdata VARCHAR(550),
tt_template_name VARCHAR(100),
CONSTRAINT fk_tttemplate FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);

-- table for storing timetables for users
CREATE TABLE TT_GENERATED
(
tt_generated_id INT PRIMARY KEY AUTO_INCREMENT,
tt_template_id INT,
admin_id INT,
worker_id INT,
tt_generated_date TIMESTAMP,
tt_generated_month INT,
tt_generated_desc VARCHAR(100),

CONSTRAINT fk_ttgenerated FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id),
CONSTRAINT fk_ttgenerated2 FOREIGN KEY (tt_template_id) REFERENCES TT_TEMPLATE(tt_template_id),
CONSTRAINT fk_ttgenerated3 FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id)
);
-- table for storing console data
CREATE TABLE CONSOLE
(
console_id INT PRIMARY KEY AUTO_INCREMENT,
console_key VARCHAR(40),
console_value VARCHAR(40),
console_time TIMESTAMP
);
-- table for setting announcments
CREATE TABLE ANNOUNCEMENT
(
announcement_id INT PRIMARY KEY AUTO_INCREMENT,
announcement_update TIMESTAMP,
announcement_data TEXT,
admin_id INT,

CONSTRAINT fk_announcment FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing privilages for admin access
CREATE TABLE ADMIN_PRIVILAGES
(
admin_privilages_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
admin_privilages_string VARCHAR(150),

CONSTRAINT fk_adminprivilages FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing fired workers
CREATE TABLE GRAVEYARD
(
graveyard_id INT PRIMARY KEY AUTO_INCREMENT,
graveyard_date DATE,
worker_id INT,
admin_id INT,

CONSTRAINT fk_graveyard FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
CONSTRAINT fk_graveyard2 FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing fired admins
CREATE TABLE ADMIN_GRAVEYARD
(
graveyard_id INT PRIMARY KEY AUTO_INCREMENT,
graveyard_date DATE,
admin_id INT,
CONSTRAINT fk_a_graveyard2 FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing log made by admin (id2)
CREATE TABLE DATA_LOG
(
data_log_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
data_log_date TIMESTAMP,
data_log_action VARCHAR(30),
data_log_desc VARCHAR(450),

CONSTRAINT fk_datalog1 FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing log made by users (id3)
CREATE TABLE USER_LOG
(
user_log_id INT PRIMARY KEY AUTO_INCREMENT,
user_log_date TIMESTAMP,
worker_id INT,
user_log_action VARCHAR(20),
user_log_desc VARCHAR(100),
user_log_photo_src VARCHAR(350),

CONSTRAINT fk_userlog1 FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id)
);
-- table for storing entrance data made by workers (id4)
CREATE TABLE ENTRANCE
(
entrance_id INT PRIMARY KEY AUTO_INCREMENT,
worker_id INT,
log_id INT,
entrance_time TIMESTAMP,
entrance_finished INT,

CONSTRAINT fk_entrance1 FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
CONSTRAINT fk_entrance2 FOREIGN KEY (log_id) REFERENCES USER_LOG(user_log_id)
);
-- table for storing entrance exit data made by workers (id5)
CREATE TABLE ENTRANCE_EXIT
(
entrance_exit_id INT PRIMARY KEY AUTO_INCREMENT,
worker_id INT,
user_log_id INT,
entrance_exit_time TIMESTAMP,

CONSTRAINT fk_entranceexit2 FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
CONSTRAINT fk_entranceexit3 FOREIGN KEY (user_log_id) REFERENCES USER_LOG(user_log_id)
);
-- table for messages between admin and users (id6)
CREATE TABLE USER_MESSAGE
(
user_message_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
worker_id INT,
user_message_content VARCHAR(250),
user_message_seen INT,

CONSTRAINT fk_usermessage1 FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
CONSTRAINT fk_usermessag2 FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- ##########################OBJECTS FOR CREATING ENTRC ITEM COORDINATOR
-- table for storing item data in ENTRC CATEGORY
CREATE TABLE ENTRC_IC_CATEGORY
(
entrc_ic_category_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
entrc_ic_category_name VARCHAR(100),
entrc_ic_category_desc VARCHAR(350),
entrc_ic_category_priority INT,

CONSTRAINT fk_entrciccategory FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing item data in ENTRC DRAWER
CREATE TABLE ENTRC_IC_DRAWER
(
entrc_ic_drawer_id INT PRIMARY KEY AUTO_INCREMENT,
admin_id INT,
entrc_ic_drawer_code VARCHAR(10),
entrc_ic_drawer_name VARCHAR(100),
entrc_ic_drawer_desc VARCHAR(150),
entrc_ic_drawer_size INT,
entrc_ic_drawer_place VARCHAR(250),

CONSTRAINT fk_entrcidrawer FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing item data in ENTRC ITEM
CREATE TABLE ENTRC_IC_ITEM
(
entrc_ic_item_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_ic_drawer_id INT,
entrc_ic_category_id int,
admin_id INT,
worker_id INT,
entrc_ic_item_name VARCHAR(100),
entrc_ic_item_desc VARCHAR(100),

CONSTRAINT fk_entrciitem FOREIGN KEY (entrc_ic_drawer_id) REFERENCES ENTRC_IC_DRAWER(entrc_ic_drawer_id),
CONSTRAINT fk_entrciitem1 FOREIGN KEY (entrc_ic_category_id ) REFERENCES ENTRC_IC_CATEGORY(entrc_ic_category_id ),
CONSTRAINT fk_entrciitem2 FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
);
-- table for storing labels for ENTRC DRAWER
CREATE TABLE ENTRC_IC_ITEM_LABEL
(
entrc_ic_item_label_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_ic_item_id INT,
entrc_ic_item_label_label VARCHAR(20),
entrc_ic_item_label_time TIMESTAMP,

CONSTRAINT fk_entrcicitemrlabel FOREIGN KEY (entrc_ic_item_id) REFERENCES ENTRC_IC_ITEM(entrc_ic_item_id)
);
-- table for storing log for ENTRC ITEM COORDINATOR
CREATE TABLE ENTRC_IC_LOG
(
entrc_ic_log_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_ic_log_code VARCHAR(20),
entrc_ic_log_userid INT,
entrc_ic_log_objectid INT,
entrc_ic_log_desc VARCHAR(300),
entrc_ic_log_time TIMESTAMP
);
-- table for locking drawers
CREATE TABLE ENTRC_IC_GRAVEYARD
(
entrc_ic_graveyard INT PRIMARY KEY auto_increment,
entrc_ic_drawer_id INT,
entrc_ic_graveyard_time TIMESTAMP,
CONSTRAINT fk_entrcicgraveyard FOREIGN KEY (entrc_ic_drawer_id) REFERENCES ENTRC_IC_DRAWER(entrc_ic_drawer_id)
);
-- ########################## ENTRC GUARD TABLES
-- table for creating timetables for entrance
CREATE TABLE ENTRC_GUARD_TIMESHEET
(
entrc_guard_timesheet INT PRIMARY KEY AUTO_INCREMENT,
entrc_guard_name VARCHAR(100),
entrc_guard_daycodes VARCHAR(7),
entrc_guard_starttime VARCHAR(5),
entrc_guard_endtime VARCHAR(5)
);
-- table for creating storage for numberplates
CREATE TABLE ENTRC_GUARD_NUMBERPLATES
(
entrc_guard_numberplates_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_guard_numberplates_data VARCHAR(10),
entrc_guard_numberplates_time TIMESTAMP,
entrc_guard_numberplates_desc VARCHAR(250)
);
-- table for creating link betweeen numberplates and users
CREATE TABLE ENTRC_GUARD_USER
(
entrc_guard_user_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_guard_numberplates_id INT,
entrc_guard_user_category INT,
user_id INT,

CONSTRAINT fk_entrcguard FOREIGN KEY (entrc_guard_numberplates_id) REFERENCES ENTRC_GUARD_NUMBERPLATES(entrc_guard_numberplates_id)
);
-- table for logging data
CREATE TABLE ENTRC_GUARD_LOG
(
entrc_guard_log_id INT PRIMARY KEY AUTO_INCREMENT,
entrc_guard_log_code VARCHAR(200),
entrc_guard_user_id INT,
entrc_guard_log_desc VARCHAR(330),
entrc_guard_log_photo BLOB,
entrc_guard_log_time TIME
);
-- table for setting entrances for cars
CREATE TABLE ENTRC_GUARD_ENTRANCE
(
entrc_guard_numberplates_id INT,
entrc_guard_timesheet INT,

CONSTRAINT fk_entrcguardentrance FOREIGN KEY (entrc_guard_numberplates_id) REFERENCES ENTRC_GUARD_NUMBERPLATES(entrc_guard_numberplates_id),
CONSTRAINT fk_entrcguardentrance2 FOREIGN KEY (entrc_guard_timesheet) REFERENCES ENTRC_GUARD_TIMESHEET(entrc_guard_timesheet)
);
-- table for recoginition api
CREATE TABLE REC_API_DATA
(
rec_api_data_id INT PRIMARY KEY auto_increment,
rec_api_data_filename VARCHAR(200),
rec_api_data_time TIMESTAMP,
rec_api_data_desc VARCHAR(100),
rec_api_data_code VARCHAR(50)
);
-- creating empty worker
INSERT INTO WORKER
(worker_login,worker_name,worker_surname,worker_pin,worker_position)
VALUES
("brak","brak","brak","brak","brak");
-- creating configuration data
INSERT INTO CONFIGURATION
(entrc_user_exit_pin,entrc_user_ask_pin,entrc_user_manage_pin,entrc_admin_manage_pin,entrc_admin_show_ip)
VALUES
("999999","666666","7777777","28450872","69696969");
-- creating admin account
INSERT INTO ADMIN_DATA
(admin_login,admin_password,admin_level,admin_email,admin_active)
VALUES
("admin","21232f297a57a5a743894a0e4a801fc3",1,"",1);
-- setting normal startup sequenceprogramcodesprogramcodes
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("CLIENTSTARTUP","NORMAL");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("PHOTOSAVE","NO");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("LOGIN","OLD");
INSERT INTO ENTRC_IC_CATEGORY
(admin_id,entrc_ic_category_name,entrc_ic_category_desc,entrc_ic_category_priority)
VALUES
(1,"no category","default category",1);
INSERT INTO ENTRC_IC_DRAWER
(admin_id,entrc_ic_drawer_code,entrc_ic_drawer_name,entrc_ic_drawer_desc,entrc_ic_drawer_place)
VALUES
(1,"OUTSIDE","no drawer","item outside any drawer","no place");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("API_ENABLED","FALSE");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("DATABASEVERSION","132");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("FACEAPI","NO");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("LOCAL_FACE_DETECT","NO");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("API_LINK","BLANK");
INSERT INTO USER_GROUPS
(user_groups_name,user_groups_desc,object_connected_id)
VALUES
("EIC","Entrc Item Coordinator users",0);
INSERT INTO USER_GROUPS
(user_groups_name,user_groups_desc,object_connected_id)
VALUES
("EG","Entrc Guard users",0);
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("MOBILE_SUPPORT","NO");
INSERT INTO PROGRAMCODES
(programcodes_key,programcodes_value)
VALUES
("SHELF_CODES","696969,121212"); -- first value contains exit code, second window reload code