/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for raport generation
 * @author kubaw
 */
public class Log_Raport_Generator {
    
    ArrayList<Integer> indexes;         // collection of indexes from log database
    String source;                      // name of the log source
    int worker_id;                      // id of the worker ( if all worker_id = -1 )
    Database_Connector database;        // object for database connection
    
    // Constructor
    Log_Raport_Generator(ArrayList<Integer> log_list,String source,int worker_id,Database_Connector database){
        indexes = log_list;
        this.worker_id = worker_id;
        this.source = source;
    }
    
    /**
     * Function for loading log 
     * @return ArrayList
     * @throws SQLException 
     */
    ArrayList<Log_Parser> load_log() throws SQLException{
        ArrayList<Log_Parser> log_list = new ArrayList<>();
        String query = "";
        switch(source){
            case "DATA_LOG":
                query = "SELECT * FROM DATA_LOG where data_log_id = ?;";
                break;
            case "USER_LOG":
                query = "SELECT * FROM USER_LOG where user_log_id = ?;";
                break;
            default:
                query = "";
                break;
        }
        if ( worker_id != -1 ){
            query = query.substring(0,query.length()-1) + " WHERE worker_id = ?;";
        }
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            if ( worker_id != -1 ){
                ppst.setInt(2,worker_id);
            }
            
            ResultSet rs = ppst.executeQuery();
            
            
            while(rs.next()){
                log_list.add(new Log_Parser(rs));
            }
            return log_list;
        
        }catch(SQLException e){
            database.log("Failed to load log ("+e.toString()+")");
            return null;
        }
    }
    
    
}
