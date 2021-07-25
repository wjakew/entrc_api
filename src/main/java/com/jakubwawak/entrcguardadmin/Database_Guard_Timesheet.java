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
import java.util.ArrayList;

/**
 *Function for managing Entrc Guard data
 * @author jakubwawak
 */
public class Database_Guard_Timesheet {

    Database_Connector database;
    Database_Guard_Log dgl;

    /**
     * Constructor
     * @param database
     */
    public Database_Guard_Timesheet(Database_Connector database){
        this.database = database;
        dgl = new Database_Guard_Log(database);
    }

    /**
     * Function for getting
     * @return ArrayList
     * @throws SQLException
     */
    ArrayList<String> get_all_timesheet() throws SQLException{
        String query = "SELECT * FROM ENTRC_GUARD_TIMESHEET;";
        ArrayList<String> data = new ArrayList<>();
        try{
            /**
             * entrc_guard_timesheet
             int AI PK
             entrc_guard_name
             varchar(100)
             entrc_guard_daycodes
             varchar(7)
             entrc_guard_starttime
             varchar(5)
             entrc_guard_endtime
             varchar(5)
             */
            PreparedStatement ppst = database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();
            while(rs.next())
                data.add(rs.getInt("entrc_guard_timesheet")+": "+rs.getString("entrc_guard_name"));

            return data;
        }catch(SQLException e){
            database.log("Failed to get all timesheet");
            return null;
        }
    }

    /**
     * Function for inserting timesheet data
     * @param name
     * @param days
     * @param timestart
     * @param timeend
     * @return
     * @throws SQLException
     */
    public int insert_timesheet(String name,String days,String timestart,String timeend) throws SQLException{
        String query = "INSERT INTO ENTRC_GUARD_TIMESHEET\n" +
                "(entrc_guard_name,entrc_guard_daycodes,entrc_guard_starttime,entrc_guard_endtime)\n" +
                "VALUES\n" +
                "(?,?,?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,name);
            ppst.setString(2,days);
            ppst.setString(3,timestart);
            ppst.setString(4,timeend);

            ppst.execute();
            dgl.log_data("TIMESHEET_ADD", "Added timesheet "+name, database.admin_id);
            return 1;
        }catch(SQLException e){
            database.log("Failed to insert timesheet ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating timesheet
     * @param name
     * @param days
     * @param timestart
     * @param timeend
     * @return Integer
     */
    public int update_timesheet(String name,String days,String timestart,String timeend) throws SQLException{
        String query = "UPDATE ENTRC_GUARD_TIMESHEET SET"
                + "entrc_guard_name =?,entrc_guard_daycodes=?,entrc_guard_starttime=?,entrc_guard_endtime=?"
                + " WHERE entrc_guard_timesheet = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,name);
            ppst.setString(2,days);
            ppst.setString(3,timestart);
            ppst.setString(4,timeend);

            ppst.execute();
            dgl.log_data("TIMESHEED_UPDATED", "Updated timesheet "+name, database.admin_id);
            return 1;
        }catch(SQLException e){
            database.log("Failed to update timesheet ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking if numberplate has timesheet
     * @param entrc_numberplates_id
     * @return
     * @throws SQLException
     */
    int check_numberplate_timesheet(int entrc_numberplates_id) throws SQLException{
        String query = "SELECT * FROM ENTRC_GUARD_ENTRANCE where entrc_guard_numberplates_id=?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,entrc_numberplates_id);

            ResultSet rs = ppst.executeQuery();

            if( rs.next() ){
                database.log("Found timesheet for given numberplate (id"+entrc_numberplates_id+")");
                return rs.getInt("entrc_guard_timesheet");
            }
            database.log("Timesheet not found for given numberplate (id"+entrc_numberplates_id+")");
            return 0;
        }catch(SQLException e){
            database.log("Failed to check numberplate timesheet ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting choosen days
     * @param entrc_guard_timesheet
     * @return String
     * @throws SQLException
     */
    String get_choosen_days(int entrc_guard_timesheet) throws SQLException{
        String query = "SELECT entrc_guard_daycodes FROM ENTRC_GUARD_TIMESHEET WHERE entrc_guard_timesheet = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_guard_timesheet);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                String days = rs.getString("entrc_guard_daycodes");
                String result = "";
                for(int i = 0 ; i < days.length() ; i++){
                    if (days.charAt(i) == '1'){
                        switch(i){
                            case 0:
                                result = result + "PON ";
                                break;
                            case 1:
                                result = result + "WT ";
                                break;
                            case 2:
                                result = result + "ŚR ";
                                break;
                            case 3:
                                result = result + "CZW ";
                                break;
                            case 4:
                                result = result + "PT ";
                                break;
                            case 5:
                                result = result + "SOB ";
                                break;
                            case 6:
                                result = result + "NIE ";
                                break;
                        }
                    }
                }
                return result;
            }
            return "pusto";
        }catch(SQLException e){
            database.log("Failed to get choosen days ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting hours from timesheet
     * @param entrc_guard_timesheet
     * @return String
     * @throws SQLException
     */
    String get_hours(int entrc_guard_timesheet) throws SQLException{
        String query = "SELECT entrc_guard_starttime, entrc_guard_endtime FROM ENTRC_GUARD_TIMESHEET WHERE entrc_guard_timesheet = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,entrc_guard_timesheet);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("entrc_guard_starttime")+" - "+rs.getString("entrc_guard_endtime");
            }
            return "pusto";
        }catch(SQLException e){
            database.log("Failed to get hours from timesheet data ("+e.toString());
            return null;
        }
    }
}
