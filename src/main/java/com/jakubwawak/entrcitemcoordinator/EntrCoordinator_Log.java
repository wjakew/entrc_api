/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *Object for maintaining app log
 * @author jakubwawak
 */
public class EntrCoordinator_Log {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public EntrCoordinator_Log(Database_Connector database){
        this.database = database;
    }

    public ArrayList<String> get_all_log(int filter) throws SQLException{
        String query = "SELECT * FROM ENTRC_IC_LOG";
        ArrayList<String> content = new ArrayList<>();
        /**    0       1          2            3                4             5          6               7               8             9               10          11
         * {"NONAME","INFO","DRAWER_ADD","DRAWER_UPDATE","DRAWER_REMOVE","ITEM_ADD","ITEM_REMOVE","ITEM_UPDATE","ITEM_RETURNED","ITEM_DRAWER_EDIT","ITEM_TAKEN"}; ALL
         */
        String[] codes = new String[]{"NONAME","INFO","DRAWER_ADD","DRAWER_UPDATE","DRAWER_REMOVE","ITEM_ADD","ITEM_UPDATE","ITEM_DRAWER_EDIT","ITEM_TAKEN"};

        if ( filter == 11 ){
            query = "SELECT * FROM ENTRC_IC_LOG";
        }
        else{
            query = query + " WHERE ENTRC_IC_LOG_CODE = '"+codes[filter]+"';";
        }

        database.log("Filter query for EntrCoordinator_Log: "+query);

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);


            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                content.add(rs.getInt("entrc_ic_log_id")+": FOR OBJECT: "+rs.getInt("entrc_ic_log_objectid")+" "+rs.getString("entrc_ic_log_code")+" "+rs.getObject("entrc_ic_log_time", LocalDateTime.class).toString()
                        +" ("+rs.getString("entrc_ic_log_desc")+")");
            }
            return content;
        }catch(Exception e){
            database.log("Failed to get all log ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting log for given item
     * @param entrc_ic_item_id
     * @return
     */
    public ArrayList<String> get_item_log(int entrc_ic_item_id) throws SQLException{
        ArrayList<String> data = new ArrayList<>();
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
         );
         *
         *
         * "ITEM_ADD","ITEM_UPDATE","ITEM_DRAWER_EDIT","ITEM_TAKEN";
         */

        String query = "SELECT * FROM ENTRC_IC_LOG where (entrc_ic_log_code Like '%ITEM%') and entrc_ic_log_objectid =?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_item_id);

            ResultSet rs = ppst.executeQuery();

            while ( rs.next() ){
                data.add(rs.getInt("entrc_ic_log_id")+": "+rs.getString("entrc_ic_log_code")+" "+rs.getObject("entrc_ic_log_time", LocalDateTime.class).toString()
                        +" ("+rs.getString("entrc_ic_log_desc")+")");
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to get item log data ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting drawer stats
     * @param entrc_ic_drawer_id
     * @return String
     */
    public String get_drawer_stats(int entrc_ic_drawer_id) throws SQLException{
        String query = "SELECT * FROM ENTRC_IC_LOG where (entrc_ic_log_code LIKE '%DRAWER%') and entrc_ic_log_objectid = ?;";
        String data = "";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_drawer_id);

            // "DRAWER_ADD","DRAWER_UPDATE","DRAWER_REMOVE"
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                switch(rs.getString("entrc_ic_log_code")){
                    case "DRAWER_ADD":
                        data = data + "Drawer add at "+rs.getObject("entrc_ic_log_time",LocalDateTime.class).toString()+"\n";
                        break;
                    case "DRAWER_UPDATE":
                        data = data + "Drawer updated at "+rs.getObject("entrc_ic_log_time", LocalDateTime.class).toString()+" by admin (id"+rs.getInt("admin_id")+"\n";
                        break;
                    default:
                        data = data + "No more to show";
                        break;
                }
            }
            return data;
        }catch(SQLException e){
            database.log("Failed getting drawer stats ("+e.toString()+")");
            return "Wystąpił błąd";
        }
    }

}
