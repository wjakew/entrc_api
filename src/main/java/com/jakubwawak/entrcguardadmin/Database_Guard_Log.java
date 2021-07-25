/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcguardadmin;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 *Object for maintaing Database_Guard
 * @author jakubwawak
 */
public class Database_Guard_Log {
    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    Database_Guard_Log(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for getting all log
     * @return ArrayList
     */
    ArrayList<String> get_all_log() throws SQLException{
        String query = "SELECT * FROM ENTRC_GUARD_LOG;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while (rs.next()){
                data.add(rs.getInt("entrc_guard_log_id")+": "+rs.getString("entrc_guard_log_desc"));
            }

            if ( data.size() == 0 ){
                data.add("Pusto");
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to get all Entrc Guard log ("+e.toString()+")");
            return null;
        }

    }

    /**
     * Function for adding log
     */
    int log_data(String log_code,String desc,int user_id) throws SQLException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        /**
         *  CREATE TABLE ENTRC_GUARD_LOG
         (
         entrc_guard_log_id INT PRIMARY KEY AUTO_INCREMENT,
         entrc_guard_log_code VARCHAR(20),
         entrc_guard_user_id INT,
         entrc_guard_log_desc INT,
         entrc_guard_log_photo BLOB,
         entrc_guard_log_time TIME
         );
         */
        String query = "INSERT INTO ENTRC_GUARD_LOG\n" +
                "(entrc_guard_log_code,entrc_guard_user_id,entrc_guard_log_desc,entrc_guard_log_photo,entrc_guard_log_time)\n" +
                "VALUES\n" +
                "(?,?,?,?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,log_code);
            ppst.setInt(2,user_id);
            ppst.setString(3,desc);
            ppst.setObject(4,null);
            ppst.setObject(5, todayLocalDate);

            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed to log data from Entrc Guard ("+e.toString()+")");
            return -1;
        }
    }
}
