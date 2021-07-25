/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 *Object for mantaing things on database
 * @author jakubwawak
 */
public class Database_Item_Coordinator {

    Database_Connector database;

    /**
     *      -- table for storing item data in ENTRC CATEGORY
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
     -- table for storing log for ENTRC ITEM COORDINATOR
     CREATE TABLE ENTRC_IC_LOG
     (
     entrc_ic_log_id INT PRIMARY KEY AUTO_INCREMENT,
     entrc_ic_log_code VARCHAR(10),
     entrc_ic_log_userid INT,
     entrc_ic_log_objectid INT,
     entrc_ic_log_desc VARCHAR(100),
     entrc_ic_log_time TIMESTAMP
     );
     */

    /**
     * Constructor
     * @param database
     */
    public Database_Item_Coordinator(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for adding log action into the log table
     * @param code
     * @param desc
     */
    void log_action(String code,String desc,int user_id,int object_id) throws SQLException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        String[] codes = new String[]{"NONAME","INFO","DRAWER_ADD","DRAWER_UPDATE","DRAWER_REMOVE","ITEM_ADD","ITEM_REMOVE","ITEM_UPDATE","ITEM_RETURNED","ITEM_DRAWER_EDIT","ITEM_TAKEN"};
        List<String> list_of_codes = Arrays.asList(codes);
        String action_code = code;
        /**
         * -- table for storing log for ENTRC ITEM COORDINATOR
         CREATE TABLE ENTRC_IC_LOG
         (
         entrc_ic_log_id INT PRIMARY KEY AUTO_INCREMENT,
         entrc_ic_log_code VARCHAR(10),
         entrc_ic_log_userid INT,
         entrc_ic_log_objectid INT,
         entrc_ic_log_desc VARCHAR(100),
         entrc_ic_log_time TIMESTAMP
         */
        String query = "INSERT INTO ENTRC_IC_LOG\n"
                + "(entrc_ic_log_code,entrc_ic_log_userid,entrc_ic_log_objectid,entrc_ic_log_desc,entrc_ic_log_time)\n"
                + "VALUES\n"
                + "(?,?,?,?,?);";
        if ( !list_of_codes.contains(code) ){
            action_code = "NONAME";
        }

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,action_code);
            ppst.setInt(2,user_id);
            ppst.setInt(3,object_id);
            ppst.setString(4,desc);
            ppst.setObject(5,todayLocalDate);

            ppst.execute();

        }catch(SQLException e){
            database.log("Failed to log data action ("+e.toString()+")");
        }
    }

}
