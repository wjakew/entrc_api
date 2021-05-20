/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *Object for managing workers on database
 * @author jakubwawak
 */
public class Database_Worker {
    
    
    Database_Connector database;
    
    //Constructor
    public Database_Worker(Database_Connector database){
        this.database = database;
    }
    
    /**
     * Function for getting active workers list
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_all_active_workers() throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        
        String query = "SELECT * FROM WORKER";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            
            while(rs.next()){
                String return_date = worker_active_check(rs.getInt("worker_id"));
                
                if ( return_date != null){
                    data.add(rs.getInt("worker_id")+ ": "+rs.getString("worker_name")+" "+rs.getString("worker_surname"));
                }
            }
            
            if (data.isEmpty()){
                data.add("Brak aktywnych pracownikow");
            }
            
            return data;
        }catch(SQLException e){
            database.log("Failed to get all active workers ("+e.toString()+")");
            return null;
        }
    }
    /**
     * Function for getting list of all workers
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_all_workers() throws SQLException{
        String query = "SELECT * FROM WORKER;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            
            
            while(rs.next()){
                if ( rs.getInt("worker_id") != 1){
                    data.add(rs.getInt("worker_id")+": "+rs.getString("worker_name")+" "+rs.getString("worker_surname"));
                }
            }
            
            if( data.isEmpty() ){
                data.add("Brak pracowników");
            }
            return data;
        
        }catch(SQLException e){
            database.log("Failed to get all workers ( "+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting all worker ids
     * @return ArrayList
     */
    public ArrayList<Integer> get_all_worker_ids() throws SQLException{
        String query = "SELECT * FROM WORKER;";
        ArrayList<Integer> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            
            
            while(rs.next()){
                if ( rs.getInt("worker_id") != 1){
                    data.add(rs.getInt("worker_id"));
                }
            }
            
            if( data.isEmpty() ){
                return null;
            }
            return data;
        
        }catch(SQLException e){
            database.log("Failed to get all worker ids ( "+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting all not fired or disactivated workers
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_all_notgraveyard_workers() throws SQLException{
        String query = "SELECT * FROM WORKER;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            
            while(rs.next()){
                if ( rs.getInt("worker_id") != 1){
                    if ( check_worker_graveyard(rs.getInt("worker_id")) == null ){
                        data.add(rs.getInt("worker_id")+": "+rs.getString("worker_name")+" "+rs.getString("worker_surname"));
                    }
                }
            }
            
            if( data.isEmpty() ){
                data.add("Brak pracowników");
            }
            return data;
        
        }catch(SQLException e){
            database.log("Failed to get all workers ( "+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting active check on workers
     * @param worker_id
     * @return String
     * return values
     * hour - hour and date since in work
     * null - worker not active
     */
    public String worker_active_check(int worker_id) throws SQLException{
        String query = "SELECT * FROM ENTRANCE where worker_id = ? and entrance_finished = 0 "
                + "ORDER BY entrance_id DESC LIMIT 1;";
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        try{
            ppst.setInt(1,worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                database.log("Found active user(id:"+worker_id+")");
                return rs.getObject("entrance_time",LocalDateTime.class).toString();
            }
            //database.log("user(id:"+worker_id+") is not active");
            return null;
        }catch(SQLException e){
            database.log("Failed to check real time activity ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for enrolling new pin for the user
     * @return String
     * @throws SQLException 
     */
    public String enroll_pin() throws SQLException{
        String query = "SELECT worker_pin from WORKER";
        ArrayList<String> pin_collection = new ArrayList<>();
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        try{
            
            ResultSet rs = ppst.executeQuery();
            
            while( rs.next() ){
                pin_collection.add(rs.getString("worker_pin"));
            }
            
            String pin = "";
            
            while(pin_collection.contains(pin) || pin.equals("")){
                pin = random_pin_generator();
            }
            
            return pin;
        }catch(SQLException e){
            database.log("Failed to enroll pin ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for getting worker pin
     * @param worker_id
     * @return String
     */
    public String get_worker_pin(int worker_id) throws SQLException{
        String query = "SELECT worker_pin FROM WORKER WHERE WORKER_ID = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return rs.getString("worker_pin");
            }
            return "blank";
       
        }catch(SQLException e){
            database.log("Failed to get worker pin ("+e.toString()+")");
            return null;
        }
    }
        /**
     * Function for getting worker id by pin
     * @param pin
     * @return Integer
     * @throws SQLException 
     * return codes:
     * any - worker id
     * 0 - no worker with given id
     * -1 - database connector failed
     */
    public int get_worker_id_bypin(String pin) throws SQLException{
        String query = "SELECT worker_id FROM WORKER WHERE worker_pin=?";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        ppst.setString(1,pin);
        try{
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return rs.getInt("worker_id");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to get worker_id ("+e.toString());
            return -1;
        }
    }
    
    /**
     * Function for getting worker data by id
     * @param worker_id
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> get_worker_byid(int worker_id) throws SQLException{
        String query = "SELECT * FROM WORKER WHERE worker_id = ?;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1, worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if (rs.next()){
                data.add(rs.getString("worker_name"));
                data.add(rs.getString("worker_surname"));
                data.add(rs.getString("worker_position"));
                data.add(rs.getString("worker_pin"));
                return data;
            }
            return null;
        }catch(SQLException e){
            database.log("Failed to get worker by id (id:"+worker_id+") ("+e.toString()+")");
            return null;
        }
    }
    
        /**
     * Function for checking if worker is in graveyard (is fired or disactivated)
     * @param worker_id
     * @return String
     */
    public String check_worker_graveyard(int worker_id) throws SQLException{
        String query = "SELECT * FROM GRAVEYARD WHERE worker_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                
                LocalDateTime ltd = rs.getObject("graveyard_date",LocalDateTime.class);
                return ltd.toString();
            }
            return null;
        }catch(SQLException e){
            database.log("Failed to check worker status ("+e.toString()+")");
            return null;
        }
    }
    /**
     * Function for setting user in graveyard ( removing from enroll)
     * @param worker_id
     * @return Integer
     * @throws SQLException e
     * return codes:
     *  1 - worker succesfully set to graveyard
     * -1 - database error
     */
    public int set_worker_graveyard(int worker_id) throws SQLException{
        LocalDateTime graveyard_time = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        String query = "INSERT INTO GRAVEYARD\n" +
                        "(graveyard_date,worker_id)\n" +
                        "VALUES\n" +
                        "(?,?);";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setObject(1,graveyard_time);
            ppst.setInt(2,worker_id);
            
            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed to set worker graveyard ("+e.toString()+")");
            return -1;
        }
    }
    
    /**
     * Function for deleting data from graveyard
     * @param worker_id
     * @return int
     * return codes:
     * 1  - successfully removed worker from graveyard
     * -1 - database error
     */
    public int unset_worker_graveyard(int worker_id) throws SQLException{
        String query = "DELETE FROM GRAVEYARD where worker_id=?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,worker_id);
            
            ppst.execute();
            return 1;
            
        }catch(SQLException e){
            database.log("Failed to remove worker from graveyard ("+e.toString()+")");
            return -1;
        }
        
    }
    /**
     * Function to generate user login
     * @return 
     */
    public String login_generator(String name,String surname) throws SQLException{
        int numbers[] = {1,2,3,4,5,6,7,8,9,10,11};
        String login = "";
        if ( surname.length() >=5 ){
            login = surname.substring(0, 5);
            login = login + name.charAt(0);
        }
        else{
            int size = surname.length();
            login = surname;
            login = login + name.substring(0,5-size);
        }
        
        String query = "SELECT worker_login FROM WORKER;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();
            int i = 0;
            while(rs.next()){
                if ( rs.getString("worker_login").equals(login)){
                    login = login + Integer.toString(numbers[i]);
                }
            }
            return login;
        }catch(SQLException e){
            database.log("Failed to generate login ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for generating random pin
     * @return String
     */
   public String random_pin_generator(){
       
       String data = "";
       
       for (int i = 0 ; i < 4 ; i++){
           int new_int = ThreadLocalRandom.current().nextInt(0, 9);
           
           data = data + Integer.toString(new_int);
       }
       return data;
   }
    
    /**
     * Function for adding worker
     * @param name
     * @param surname
     * @param pin
     * @param position
     * @return String
     */
    public String add_worker(String name,String surname,String pin,String position) throws SQLException{
        String query = "INSERT INTO WORKER (worker_login,worker_name,worker_surname,worker_pin,worker_position)\n"
                + "VALUES\n"
                + "(?,?,?,?,?);";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1, login_generator(name,surname));
            ppst.setString(2, name);
            ppst.setString(3,surname);
            
            if ( pin.equals("") ){
                pin = enroll_pin();
            }
            ppst.setString(4, pin);
            ppst.setString(5,position);
            
            ppst.execute();
            return pin;
        
        }catch(SQLException e){
            database.log("Failed to add worker ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for reset pin for user
     * @param worker_id
     * @return String
     * @throws SQLException 
     */
    public String reset_pin(int worker_id) throws SQLException{
        String new_pin = enroll_pin();
        
        String query = "UPDATE WORKER SET worker_pin = ? WHERE worker_id = ?;";
        
        PreparedStatement ppst = database.con.prepareStatement(query);
        
        ppst.setString(1,new_pin);
        ppst.setInt(2,worker_id);
        
        try{
            ppst.execute();
            return new_pin;
        }catch(SQLException e){
            database.log("Failed to reset pin for user(id:"+worker_id+") ("+e.toString()+")");
            return null;
        }
        
    }
    
    /**
     * Function for updating worker data
     * @param worker_id
     * @param name
     * @param surname
     * @param position
     * @return boolean
     * @throws SQLException 
     */
    public boolean update_worker(int worker_id,String name,String surname,String position) throws SQLException{
        String query = "UPDATE WORKER SET worker_name = ?, "
                + "worker_surname = ?, worker_position = ? WHERE worker_id = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,name);
            ppst.setString(2,surname);
            ppst.setString(3,position);
            ppst.setInt(4,worker_id);
            
            ppst.execute();
            return true;
        }catch(SQLException e){
            database.log("Failed to update user (id:"+worker_id+") ("+e.toString()+")");
            return false;
        }
    }
}
