/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.database.Database_Connector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for dumping data from database
 * @author kubaw
 */
public class Database_Data_Dump {
    
    /**
     * Main goals:
     * 
     * 1. dump in unified way:
     * - version for admin
     *      entrance data
     *      exit data
     *      user log data
     *      admin log data
     *   - version for client app
     *      worker data
     *      progam log
     */
    
    Database_Connector database;
    
    public Database_Data_Dump(Database_Connector database){
        this.database = database;
    }
    
    /**
     * Function for dumping data to file
     * @param dump
     * @param src
     * @return
     * @throws IOException 
     */
    public String dump_to_file(ArrayList<String> dump, String src) throws IOException, SQLException{
        try {
                FileWriter writer = new FileWriter(src);
                for( String line : dump){
                    writer.write(line + "\n");
                }
                writer.close();
                File writer_f = new File(src);
                return writer_f.getAbsolutePath();
          } catch (IOException e) {
                database.log("Failed to save dump ("+e.toString()+")");
                return null;
        }
    }
    
    /**
     * Function for dumping worker data from database;
     * @return ArrayList
     * @throws SQLException 
     */
    public ArrayList<String> dump_worker_data() throws SQLException{
        ArrayList<String> dump = new ArrayList<>();
        
        String query = "SELECT * FROM WORKER;";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            
            ResultSet rs = ppst.executeQuery();

            while ( rs.next()){
                if ( rs.getInt("worker_id") != 1){
                    dump.add(rs.getString("worker_name")+","+rs.getString("worker_surname")
                        +","+rs.getString("worker_position")+","+rs.getString("worker_pin"));
                }
                
            }
            return dump;
        }catch(SQLException e){
            database.log("Failed to prepare dump (worker) ("+e.toString()+")");
            return null;
        }
    }
    
    /**
     * Function for dumping program log
     * @return ArrayList
     */
    ArrayList<String> dump_program_log() throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM PROGRAM_LOG";
        
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            
            while ( rs.next() ){
                data.add(rs.getInt("program_log_id")+" "+rs.getString("program_log_desc"));
            }
            
            return data;
        }catch(SQLException e){
            database.log("Failed to dump program log ("+e.toString()+")");
            return null;
        }
    }
    
}
