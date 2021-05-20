/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.barcode;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *Object for managing and connecting to barecode database
 * @author jakubwawak
 */
public class Database_BarcodeGenerator {
    
    Database_Connector database;
    
    /**
     * Connector
     * @param database 
     */
    public Database_BarcodeGenerator(Database_Connector database){
        this.database = database;
    }
    
    
    /**
     * Function for checking if barecode exists
     * @param worker_id
     * @return Integer
     * return codes:
     * -1 barecode not found
     * -2 database error
     * any barecode_data_id
     */
    public int check_barcode_exist(int worker_id) throws SQLException{
        String query = "SELECT * FROM BARCODE_DATA WHERE WORKER_ID = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1, worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return rs.getInt("barcode_data_id");
            }
            return -1;
            
        }catch(SQLException e){
            database.log("ERROR CHECKING BARCODE FOR WORKER (id"+worker_id+")");
            return -2;
        }
    }
    
    /**
     * Function for checking barcode
     * @param barcode_raw_data
     * @return Integer
     * @throws SQLException 
     */
    public int check_barcode_exist(String barcode_raw_data) throws SQLException{
        String query = "SELECT worker_id FROM BARCODE_DATA WHERE barcode_raw_data = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,barcode_raw_data);
            
            ResultSet rs = ppst.executeQuery();
            
            if(rs.next()){
                return rs.getInt("worker_id");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to get worker id by given barcode |"+barcode_raw_data+"| ("+e.toString()+")");
            return -1;
        }
    }
    
    /**
     * Function for inserting barecode data
     * @param worker_id
     * @param bcc
     * @return Integer
     * return codes:
     * 1 - barcode inserted successfully
     * -1 database error
     */
    public int insert_barcode(int worker_id, BarCodeCreator bcc) throws SQLException{
        LocalDateTime ldt = LocalDateTime.ofInstant(bcc.date.toInstant(),
                                             ZoneId.systemDefault());

        String query = "INSERT INTO BARCODE_DATA\n" +
                        "(barcode_date,worker_id,barcode_raw_data)\n" +
                        "VALUES\n" +
                        "(?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setObject(1,ldt);
            ppst.setInt(2,worker_id);
            ppst.setString(3, bcc.raw_barecode_data);
            database.log("Query for inserting barcode: "+ppst.toString());
            ppst.execute();
            return 1;
            
        }catch(SQLException e){
            database.log("Failed to insert barecode for worker (id"+worker_id+") ("+e.toString()+")");
            return -1;
        }
    }
    
    /**
     * Function for getting barecode for user
     * @param worker_id
     * @return BarCodeCreator
     */
    public BarCodeCreator retrive_barecode(int worker_id) throws SQLException{
        String query = "SELECT * FROM BARCODE_DATA WHERE WORKER_ID = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,worker_id);
            
            ResultSet rs = ppst.executeQuery();
            
            if ( rs.next() ){
                return new BarCodeCreator(rs);
            }
            return null;
            
        }catch(SQLException e){
            database.log("Failed to retrive barecode ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for removing barcode data from database
     * @param worker_id
     * @return Integer
     * return codes:
     * 1 - successfully removed barcode
     * -1 - database error
     */
    public int remove_barcode(int worker_id) throws SQLException{
        String query = "DELETE FROM BARCODE_DATA WHERE WORKER_ID = ?;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ppst.setInt(1,worker_id);
            
            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed ro remove barcode for worker (id"+worker_id+") ("+e.toString()+")");
            return -1;
        }
    }
    
}
