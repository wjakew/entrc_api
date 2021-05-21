/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved 
 */
package com.jakubwawak.administrator;

import com.jakubwawak.database.Database_Connector;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *Object for parsing and creating log
 * @author kubaw
 */
public class Log_Parser {
    
    int log_id;
    LocalDateTime date;
    int user_id;
    String action_code;
    String log_desc;
    String photo_src;
    
    String type;
    
    ResultSet rs;
    
    // Constructor
    Log_Parser (ResultSet rs) throws SQLException{
        this.rs = rs;
        ResultSetMetaData data = this.rs.getMetaData();
        type = data.getTableName(4);
    }
    
    /**
     * Function for parasing data from log
     * @throws SQLException 
     */
    void parse() throws SQLException{
        if ( type.equals("DATA_LOG")){
            log_id = rs.getInt("data_log_id"); 
            user_id  = rs.getInt("admin_id");
            date = rs.getObject("data_log_date",LocalDateTime.class);
            action_code = rs.getString("data_log_action");
            log_desc = rs.getString("data_log_desc");
            photo_src = "";
        }
        else{
            log_id = rs.getInt("user_log_id");
            date = rs.getObject("user_log_date",LocalDateTime.class);
            user_id = rs.getInt("worker_id");
            action_code = rs.getString("user_log_action");
            log_desc = rs.getString("user_log_desc");
            photo_src = rs.getString("user_log_photo_src");
        }
    }
    
    /**
     * Function for showing log data
     * @param database
     * @return String
     * @throws SQLException 
     */
    String show_log(Database_Connector database) throws SQLException{
        String data = "Brak danych do wyświetlenia";
        if ( type.equals("DATA_LOG")){
            data = "data_log_id: "+log_id+"\n"+
                    "admin_id: "+user_id+"("+database.get_admin_login(database.admin_id)+")\n"+
                    "data_log_date: "+date.toString()+"\n"+
                    "data_log_action: "+action_code+"\n"+
                    "log_desc: "+log_desc;
        }
        else{
            data = "user_log_id: "+log_id+"\n"+
                    "worker_id: "+user_id+"("+database.get_worker_nameusername(user_id)+")\n"+
                    "user_log_date: "+date.toString()+"\n"+
                    "user_log_action: "+action_code+"\n"+
                    "user_log_desc: "+log_desc+"\n"+
                    "user_log_photo_src:"+photo_src;
        }
        return data;
    }
    
}
