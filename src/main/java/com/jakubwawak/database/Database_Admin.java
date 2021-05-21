/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *Object for managing admin data
 * @author jakubwawak
 */
public class Database_Admin {
    
    Database_Connector database;
    int admin_id;
    
    // Constructor
    public Database_Admin(Database_Connector database) throws SQLException{
        this.database = database;
        database.log_event("Init Database_Admin object","ADMIN_INFO");
    }
    
    /**
     * Function for getting admin data
     * @return ArrayList
     */
    public ArrayList<String> get_admindata_glances() throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM ADMIN_DATA;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            database.log_event("Gathered admin data", "ADMIN_INFO");
            while (rs.next()){
                data.add(rs.getInt("admin_id")+":"+rs.getString("admin_login"));
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to get admin data ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for composing model from given collection
     * @param data
     * @return DefaultListModel
     */
    public DefaultListModel compose_model(ArrayList<String> data){
        DefaultListModel model = new DefaultListModel();
        
        model.addAll(data);
        return model;
    }
    
    /**
     * Function for getting activity status
     * @param admin_id
     * @return Integer
     * @throws SQLException 
     */
    public int get_activity_status(int admin_id) throws SQLException{
        String query = "SELECT * FROM ADMIN_DATA WHERE admin_id = ? and admin_active = 1;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,admin_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if (rs.next()){
                return 1;
            }
            return 0;
        
        }catch(SQLException e){
            database.log("Failed to check activity status ("+e.toString()+")");
            return -1;
        }
    }
    
    /**
     * Function for getting admin email
     * @param admin_id
     * @return String
     * @throws SQLException 
     */
    public String get_email(int admin_id) throws SQLException{
        String query = "SELECT admin_email FROM ADMIN_DATA WHERE admin_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,admin_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                if (rs.getString("admin_email").equals("")){
                    return "empty";
                }
                return rs.getString("admin_email");
            }
            else{
                return "empty";
            }
            
        }catch(SQLException e){
            database.log("Failed to get admin email ("+e.toString()+")");
            return "error";
        }
    }
    
    /**
     * Function for reseting password for selected admin
     * @param admin_id
     * @return String
     * @throws SQLException
     * @throws NoSuchAlgorithmException 
     */
    public String reset_password(int admin_id) throws SQLException, NoSuchAlgorithmException{
        String query = "UPDATE ADMIN_DATA SET admin_password = ? WHERE admin_id = ?;";
        
        String password = database.create_randomPassword();
        database.log_event("Trying to change admin password admin(id:"+admin_id+")","ADMIN_CHANGED_PASS");
        try{
            Password_Validator pv = new Password_Validator(password);
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setString(1,pv.hash());
            ppst.setInt(2,admin_id);
            
            ppst.execute();
            database.log_event("Updated succesfully admin(id"+admin_id+") machne: "+database.get_local_MACAddress(),"ADMIN_CHANGED_PASS");
            return password;
        
        }catch(Exception e){
            database.log("Failed to reset password ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for setting admin activity data
     * @param admin_id
     * @param activity_state
     * @return boolean
     * @throws SQLException 
     */
    public boolean set_admin_activity(int admin_id,int activity_state) throws SQLException{
        
        String query = "UPDATE ADMIN_DATA SET admin_active = ? where admin_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,activity_state);
            ppst.setInt(2,admin_id);
            database.log_event("Administrator status changed (id:"+admin_id+")", "ADMIN_EDITED");
            ppst.execute();
            return true;
            
        }catch(SQLException e){
            database.log("Failed to set admin activity. ("+e.toString()+")");
            return false;
        }
    }
    
        /**
     * Function for getting admin data in simple collection
     * @param admin_id
     * @return ArrayList
     * Returns collection with:
     * [admin_id,admin_login,admin_email,admin_level]
     */
    public ArrayList<String> get_admin(int admin_id) throws SQLException{
        String query = "SELECT * FROM ADMIN_DATA WHERE admin_id = ?;";
        ArrayList<String> data_toRet = new ArrayList<>();
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,admin_id);
            
            ResultSet rs = ppst.executeQuery();
            database.log_event("Loaded admin data (id:"+admin_id+")", "ADMIN_INFO");
            if ( rs.next() ){
                data_toRet.add(Integer.toString(rs.getInt("admin_id")));
                data_toRet.add(rs.getString("admin_login"));
                data_toRet.add(rs.getString("admin_email"));
                data_toRet.add(Integer.toString(rs.getInt("admin_level")));
                return data_toRet;
            }
            return null;
        }catch(SQLException e){
            database.log("Failed to get admin data ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting admin id by given login
     * @param admin_login
     * @return int
     * @throws SQLException 
     */
    public int get_admin_id(String admin_login) throws SQLException{
        String query = "SELECT admin_id FROM ADMIN_DATA where admin_login = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setString(1,admin_login);
            
            ResultSet rs = ppst.executeQuery();
            
            
            if ( rs.next() ){
                return rs.getInt("admin_id");
            }
            return 0;
        
        }catch(SQLException e){
            database.log("Failed to get admin id ("+e.toString()+")");
            return -1;
        }
        
    }
    
    /**
     * Function for checking if admin exists
     * @param admin_id
     * @return boolean
     */
    public boolean check_admin_exists(int admin_id) throws SQLException{
        String query = "SELECT admin_id FROM ADMIN_DATA where admin_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,admin_id);
            
            ResultSet rs = ppst.executeQuery();
            
            return rs.next();
        
        }catch(SQLException e){
            database.log("Failed to check if admin exists ("+e.toString()+")");
            return false;
        }
    }
    
    /**
     * Function for checking if admin with given login exists
     * @param admin_login
     * @return Integer
     * @throws SQLException
     * return codes:
     *  0 -  admin not found
     * -1 -  database error
     * any - admin id to given login
     */
    public int check_admin_exists(String admin_login) throws SQLException{
        String query = "SELECT admin_id FROM ADMIN_DATA where admin_login = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setString(1,admin_login);
            
            ResultSet rs =ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("admin_id");
            }
            else{
                return 0;
            }
        }catch(SQLException e){
            database.log("Failed to check if admin exists ("+e.toString()+")");
            return -1;
        }
    }
    
}
