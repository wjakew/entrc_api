/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *Function for handling log data
 * @author jakubwawak
 * EVENTS
 * ==========
 * DATA_LOG:
 * ENTER
 * EXIT
 * ENTER_FAILED
 * LOGIN_ACCEPT
 * LOGIN_FAILED
 * LOGOUT_FAILED
 * PIN_RESET
 * INFO
 * ==========
 * USER_LOG:
 * DATA_GET
 * ADMIN_LOGIN
 * ADMIN_LOGOUT
 * DATA_DELETED
 * DATA_LOADED
 * CONFIGURATION_CH
 * CONFIGURATION_ADD
 */
public class Database_Log {
    
    /**
     * Object can handle two types of log.
     * types:
     * DATA_LOG
     * USER_LOG
     */
    String source;
    
    String query;       // variable for storing current query data
    Database_Connector database;
    
    // Constructor
    public Database_Log(String source,Database_Connector database){
        this.database = database;
        this.source = source;
    }
    
    /**
     * Function for getting query base
     * @return String
     */
    String get_query_base(){
        switch(source){
            case "DATA_LOG":
                return "SELECT * FROM DATA_LOG ";
            case "USER_LOG":
                return "SELECT * FROM USER_LOG ";
            default:
                return null;
        }
    }
    /**
     * Query for getting query based on action
     * @param action_name
     * @return String
     */
    String get_action_query(String action_name){
        /**
            * USER_LOG:
                * ENTER
                * EXIT
                * ENTER_FAILED
                * LOGIN_ACCEPT
                * LOGIN_FAILED
                * LOGOUT_FAILED
                * PIN_RESET
                * INFO
         */
        switch(action_name){
            case "ALL":
                return get_query_base() + ";";
            case "ENTER":
                return get_query_base() +" WHERE user_log_action = 'ENTER';";
            case "EXIT":
                return get_query_base() +" WHERE user_log_action = 'EXIT';";
            case "ENTER_FAILED":
                return get_query_base() +" WHERE user_log_action = 'ENTER_FAILED';";
            case "LOGIN_ACCEPT":
                return get_query_base() +" WHERE user_log_action = 'LOGIN_ACCEPT';";
            case "LOGIN_FAILED":
                return get_query_base() +" WHERE user_log_action = 'LOGIN_FAILED';";
            case "LOGOUT_FAILED":
                return get_query_base() +" WHERE user_log_action = 'LOGOUT_FAILED';";
            case "PIN_RESET":
                return get_query_base() +" WHERE user_log_action = 'PIN_RESET';";
            case "INFO":
                return get_query_base() +" WHERE user_log_action = 'INFO';";
            
            /**
             * DATA_LOG
                    * EVENTS:
                    * ADMIN_CREATED +
                    * ADMIN_ELEVATED  - ( given privilages ) +
                    * ADMIN_DELETED +
                    * ADMIN_EDITED  +
                    * ADMIN_CHANGED_PASS +
                    * WORKER_DELETED 
                    * WORKER_ADDED +
                    * WORKER_DATA_CHANGED +
                    * CHECKED_LOG +
                    * GENERATED_RAPORT
                    * MESSAGE_DELETED +
                    * ADMIN_INFO +
             */
            
            case "ADMIN_CREATED":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_CREATED';";
            case "ADMIN_ELEVATED":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_ELEVATED';";
            case "ADMIN_DELETED":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_DELETED';";
            case "ADMIN_EDITED":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_EDITED';";
            case "ADMIN_CHANGED_PASS":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_CHANGED_PASS';";
            case "WORKER_DELETED":
                return get_query_base() +" WHERE data_log_action = 'WORKER_DELETED';";
            case "WORKER_ADDED":
                return get_query_base() +" WHERE data_log_action = 'WORKER_ADDED';";
            case "WORKER_DATA_CHANGED":
                return get_query_base() +" WHERE data_log_action = 'WORKER_DATA_CHANGED';";
            case "CHECKED_LOG":
                return get_query_base() +" WHERE data_log_action = 'CHECKED_LOG';";
            case "GENERATED_RAPORT":
                return get_query_base() +" WHERE data_log_action = 'GENERATED_RAPORT';";
            case "MESSAGE_DELETED":
                return get_query_base() +" WHERE data_log_action = 'MESSAGE_DELETED';";
            case "ADMIN_INFO":
                return get_query_base() +" WHERE data_log_action = 'ADMIN_INFO';";
            default:
                return get_query_base()+";";
        }
    }
    
    String add_personal_data(String query,int worker_id){
        return query.substring(0, query.length()-1)+" AND worker_id ="+worker_id+";";
    }

    /**
     * Function for getting raw ResultSet object
     * @param query
     * @return
     * @throws SQLException 
     */
    ResultSet get_raw_RS(String query) throws SQLException{
        try{
            database.log("Got query: ("+query+")");
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            return ppst.executeQuery();
        
        }catch(SQLException e){
            database.log("Failed to get raw RS ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting decoded data from ResultSet
     * @param rs
     * @return ArrayList
     * @throws SQLException 
     */
    ArrayList<String> decode_RS(ResultSet rs) throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        
        while ( rs.next() ){
            data.add(print_logRS(rs));
        }
        
        if ( data.isEmpty() ){
            data.add("Pusto");
        }
        return data;
        
    }
    
    /**
     * Function for printing data to list from ResultSet object
     * @param rs
     * @return
     * @throws SQLException 
     */
    String print_logRS(ResultSet rs) throws SQLException{
        if (source.equals("DATA_LOG")){
            return rs.getInt("data_log_id")+": ("+rs.getObject("data_log_date",LocalDateTime.class).toString()+") "
                    +database.get_admin_login(rs.getInt("admin_id"))+"|"+rs.getString("data_log_action")+"|"+rs.getString("data_log_desc");
        }
        else{
            return rs.getInt("user_log_id")+": ("+rs.getObject("user_log_date",LocalDateTime.class).toString()+") "
                    +database.get_worker_nameusername(rs.getInt("worker_id"))+" |"+rs.getString("user_log_action")+"|"+rs.getString("user_log_desc");
        }
    }
    
    /**
     * Function for getting JList model
     * @param data
     * @return DefaultListModel
     */
    public DefaultListModel compose_model(ArrayList<String> data){
        DefaultListModel dlm = new DefaultListModel();
        dlm.addAll(data);
        return dlm;
    }
    
    /**
     * Function for getting specified log data for user
     * @param action
     * @param worker_id
     * @return String
     */
    public ArrayList<String> get_specified_log(String action,int worker_id) throws SQLException{
        String data_query = get_action_query(action);
        data_query = add_personal_data(data_query,worker_id);
        ResultSet rs = get_raw_RS(data_query);
        return decode_RS(rs);
    }
    
    /**
     * Function for getting specified log data
     * @param action
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_specified_log(String action) throws SQLException{
        String data_query = get_action_query(action);
        ResultSet rs = get_raw_RS(data_query);
        return decode_RS(rs);
    }
    /**
     * Getting all log made by user
     * @param worker_id
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_all_log(int worker_id) throws SQLException{
        ArrayList<String> log_list = new ArrayList<>();
        
        switch (source) {
            case "DATA_LOG":
                query = "SELECT * FROM DATA_LOG where worker_id = ?;";
                break;
            case "USER_LOG":
                query = "SELECT * FROM USER_LOG where worker_id = ?";
                break;
            default:
                query = "";
                break;
        }
        
        try{
            
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            while(rs.next()){
                log_list.add(print_logRS(rs));
            }
   
            if ( log_list.isEmpty() ){
                log_list.add("Pusto...");
            }
            
            return log_list;
        
        }catch(SQLException e){
            database.log("Failed to get all log ( "+source+" ) ("+e.toString()+")");
            return null;
        }
    }
    /**
     * Function for getting all log data
     * @return
     * @throws SQLException 
     */
    public ArrayList<String> get_all_log() throws SQLException{
        
        ArrayList<String> log_list = new ArrayList<>();
        
        switch (source) {
            case "DATA_LOG":
                query = "SELECT * FROM DATA_LOG;";
                break;
            case "USER_LOG":
                query = "SELECT * FROM USER_LOG";
                break;
            default:
                query = "";
                break;
        }
        
        try{
            
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            
            while(rs.next()){
                log_list.add(print_logRS(rs));
            }
   
            if ( log_list.isEmpty() ){
                log_list.add("Pusto...");
            }
            
            return log_list;
        
        }catch(SQLException e){
            database.log("Failed to get all log ( "+source+" ) ("+e.toString()+")");
            return null;
        }
    }
    
}
