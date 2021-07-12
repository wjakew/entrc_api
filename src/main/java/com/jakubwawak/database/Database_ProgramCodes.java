/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Class for mantaining programcodes
 * @author jakubwawak
 */
public class Database_ProgramCodes {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_ProgramCodes(Database_Connector database){
        this.database = database;
    }


    /**
     * Function for checking if key exists
     * @param key
     * @return Int
     * @throws SQLException
     */
    int key_exists(String key) throws SQLException{
        String query = "SELECT * from PROGRAMCODES WHERE programcodes_key = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,key);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() )
                return 1;
            return 0;
        }catch(SQLException e){
            database.log("Error checking if key exists ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting value from database
     * @param code
     * @return String
     */
    public String get_value(String code) throws SQLException{
        String query = "SELECT programcodes_value from PROGRAMCODES WHERE programcodes_key = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,code);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("programcodes_value");
            }
            return "null";
        }catch(SQLException e){
            database.log("Failed to get value from ProgramCode database");
            return "databaseerror";
        }
    }

    /**
     * Function for setting data on table
     * @param key
     * @param code
     * @return Integer
     * @throws SQLException
     */
    public int set_value(String key,String code) throws SQLException{
        database.log("Trying to set on PROGRAMCODES "+key+"="+code);
        if ( key_exists(key) == 1 ){
            update_value(key,code);
            return 1;
        }
        else if (key_exists(key) == 0){
            insert_value(key,code);
            return 2;
        }
        else{
            return -1;
        }
    }
    /**
     * Function for updating value on database
     * @param key
     * @param code
     * @return Int
     */
    int update_value(String key,String code) throws SQLException{
        String query = "UPDATE PROGRAMCODES SET programcodes_value = ? WHERE programcodes_key = ?;";
        try{
            database.log("Updating key "+key);
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(2,key);
            ppst.setString(1,code);

            ppst.execute();
            return 1;

        }catch(SQLException e){
            database.log("Failed to update value to PROGRAMCODES ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for inserting value to database
     * @param key
     * @param code
     * @return Int
     */
    int insert_value(String key,String code) throws SQLException{
        String query = "INSERT INTO PROGRAMCODES (programcodes_key,programcodes_value)"
                + "\nVALUES"
                + "\n(?,?);";
        try{
            database.log("Inserting key "+key);
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,key);
            ppst.setString(2,code);

            ppst.execute();
            return 1;

        }catch(SQLException e){
            database.log("Failed to insert value to PROGRAMCODES ("+e.toString()+")");
            return -1;
        }
    }

}
