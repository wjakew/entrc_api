/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcguardadmin;

import com.jakubwawak.database.Database_Admin;
import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.entrc_api.EntrcApi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 *Object for maintaining numberplate data in database
 * @author jakubwawak
 */
public class Database_Numberplate {

    Database_Connector database;
    Database_Guard_Log dgl;
    /**
     * Constructor
     * @param database
     */
    public Database_Numberplate(Database_Connector database){
        this.database = database;
        dgl = new Database_Guard_Log(database);
    }

    /**
     * Function for adding numberplates
     * @return int
     * entrc_guard_numberplates_id
    int AI PK
    entrc_guard_numberplates_data
    varchar(10)
    entrc_guard_numberplates_time
    timestamp
    entrc_guard_numberplates_desc
     */
    int add_numberplate(String data,String desc) throws SQLException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        String query = "INSERT INTO ENTRC_GUARD_NUMBERPLATES\n"
                + "(entrc_guard_numberplates_data,entrc_guard_numberplates_time,entrc_guard_numberplates_desc)\n"
                + "VALUES\n"
                + "(?,?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,data);
            ppst.setObject(2,todayLocalDate);
            ppst.setString(3,desc);

            ppst.execute();
            dgl.log_data("NUMBERPLATE_ADD", "Added numberplate ("+data+")", database.admin_id);
            return 1;

        }catch(SQLException e){
            database.log("Failed to add numberplate ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking numberplate
     * @param numberplate
     * @return Integer
     */
    public int check_numberplate(String numberplate){
        String query = "SELECT entrc_guard_numberplates_id from ENTRC_GUARD_NUMBERPLATES where entrc_guard_numberplates_data = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,numberplate);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return rs.getInt("entrc_guard_numberplates_id");
            }
            return 0;
        } catch (SQLException e) {
            EntrcApi.eal.add("Failed to get numberplate id ("+e.toString()+")");
            return -1;
        }
    }
    /**
     * Function for getting
     * @param entrc_guard_numberplates_id
     * @return
     */
    public String get_owner(int entrc_guard_numberplates_id) throws SQLException{
        String query = "SELECT entrc_guard_user_category, user_id FROM ENTRC_GUARD_USER where entrc_guard_numberplates_id=?;";
        try{

            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_guard_numberplates_id);

            ResultSet rs = ppst.executeQuery();
            /**
             *  entrc_guard_user_id INT PRIMARY KEY AUTO_INCREMENT,
             entrc_guard_numberplates_id INT,
             entrc_guard_user_category INT,
             user_id INT,
             */
            if ( rs.next() ){
                int category = rs.getInt("entrc_guard_user_category");

                switch(category){
                    case 1:
                        // admin
                        Database_Admin da = new Database_Admin(database);
                        return da.get_admin(rs.getInt("user_id")).get(1);
                    case 2:
                        return database.get_worker_data(rs.getInt("user_id"));
                }
            }
            return "brak";
        }catch(SQLException e){
            database.log("Failed to get owner data for numberplate ("+e.toString()+")");
            return "błąd";
        }
    }

    /**
     * Function for getting numberplates owner
     * @param entrc_guard_numberplates_id
     * @return ArrayList
     */
    public ArrayList get_owner_data(int entrc_guard_numberplates_id) throws SQLException {
        String query = "SELECT entrc_guard_user_category, user_id FROM ENTRC_GUARD_USER where entrc_guard_numberplates_id=?;";
        ArrayList<Integer> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,entrc_guard_numberplates_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                data.add(rs.getInt("entrc_guard_user_category"));
                data.add(rs.getInt("user_id"));
            }
            return data;
        } catch (SQLException e) {
            database.log("Failed to get numberplates owner ("+e.toString()+")");
            return null;
        }
    }


    /**
     * Function for adding owner
     * @param entrc_guard_numberplates_id
     * @param user_id
     * @param entrc_guard_user_category
     * @return Integer
     * @throws SQLException
     */
    int add_owner(int entrc_guard_numberplates_id, int user_id, int entrc_guard_user_category) throws SQLException{
        String query = "UPDATE ENTRC_GUARD_USER SET user_id = ?,entrc_guard_user_category = ? WHERE entrc_guard_numberplates_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,user_id);
            ppst.setInt(2,entrc_guard_user_category);
            ppst.setInt(3,entrc_guard_numberplates_id);

            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed to add owner to numberplate data ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading numberplates data
     * @return ArrayList
     */
    ArrayList<String> load_numberplates_data() throws SQLException{
        String query = "SELECT * FROM ENTRC_GUARD_NUMBERPLATES;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();
            /**
             *  entrc_guard_numberplates_id
             int AI PK
             entrc_guard_numberplates_data
             varchar(10)
             entrc_guard_numberplates_time
             timestamp
             entrc_guard_numberplates_desc
             varchar(250)
             */
            while( rs.next() ){
                data.add(rs.getInt("entrc_guard_numberplates_id")+": "+rs.getString("entrc_guard_numberplates_data"));
            }
            if (data.size() == 0){
                data.add("Pusto");
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to log numberplates data ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for adding timesheet data to timesheet
     * @param entrc_guard_timesheet_id
     * @param entrc_guard_numberplates_id
     * @return Integer
     * @throws SQLException
     */
    int add_timesheet(int entrc_guard_timesheet_id,int entrc_guard_numberplates_id) throws SQLException{
        String query = "INSERT INTO ENTRC_GUARD_ENTRANCE\n" +
                "(entrc_guard_numberplates_id,entrc_guard_timesheet)\n" +
                "VALUES\n" +
                "(?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1, entrc_guard_numberplates_id);
            ppst.setInt(2, entrc_guard_timesheet_id);

            ppst.execute();
            dgl.log_data("NUMBERPLATE_TIMESHEET", "Added timesheet to numberplate (id"+entrc_guard_numberplates_id+") "+entrc_guard_timesheet_id, database.admin_id);
            return 1;

        }catch(SQLException e){
            database.log("Failed to add timesheet to numberplate ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting timesheet
     * @param entrc_guard_numberplates_id
     * @return Integer
     */
    public int get_timesheet(int entrc_guard_numberplates_id) throws SQLException {
        String query = "SELECT entrc_guard_timesheet from ENTRC_GUARD_ENTRANCE where entrc_guard_numberplates_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,entrc_guard_numberplates_id);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("entrc_guard_timesheet");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to get timesheet ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for removing timesheet
     * @param entrc_guard_numberplates_id
     * @return Integer
     * @throws SQLException
     */
    int remove_timesheet(int entrc_guard_numberplates_id) throws SQLException{
        String query = "DELETE FROM ENTRC_GUARD_ENTRANCE WHERE entrc_guard_numberplates_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_guard_numberplates_id);

            ppst.execute();
            dgl.log_data("NUMBERPLATE_TIMESHEET_DELETE", "Removed timesheet from numberplate (id"+entrc_guard_numberplates_id, database.admin_id);
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove timesheet ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting
     * @param owner_id
     * @param category_id
     * @param entrc_guard_numberplate_id
     * @return
     */
    int add_owner_numberplate(int owner_id,int category_id,int entrc_guard_numberplate_id) throws SQLException{
        String query = "INSERT INTO ENTRC_GUARD_USER (entrc_guard_numberplates_id,entrc_guard_user_category,user_id) VALUES\n"
                + "(?,?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,entrc_guard_numberplate_id);
            ppst.setInt(2,category_id);
            ppst.setInt(3,owner_id);

            ppst.execute();
            dgl.log_data("NUMBERPLATE_OWNER", "Added owner to numberplate (id"+owner_id+")", database.admin_id);
            return 1;
        }catch(SQLException e){
            database.log("Failed to add owner to numberplate ("+e.toString()+")");
            return -1;
        }

    }

    /**
     * Function for updating numberplates data
     * @param data
     * @param desc
     * @param entrc_guard_numberplates_id
     * @return Integer
     * @throws SQLException
     */
    int update_numberplate(String data,String desc,int entrc_guard_numberplates_id) throws SQLException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        String query = "UPDATE ENTRC_GUARD_NUMBERPLATES set entrc_guard_numbeplates_data=?, entrc_guard_numberplates_time=?"
                + " ,entrc_guard_numberplates_desc = ? where entrc_guard_numberplates_id=?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,data);
            ppst.setObject(2,todayLocalDate);
            ppst.setString(3,desc);
            ppst.setInt(4,entrc_guard_numberplates_id);

            ppst.execute();
            dgl.log_data("NUMBERPLATE_UPDATE", "Updated numberplate ("+data+")", database.admin_id);
            return 1;
        }catch(SQLException e){
            database.log("Failed to update numberplate ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting last numberplate id
     * @return Integer
     * @throws SQLException
     */
    int get_last_numberplateid() throws SQLException{
        String query = "SELECT entrc_guard_numberplates_id FROM ENTRC_GUARD_NUMBERPLATES ORDER BY entrc_guard_numberplates_id DESC LIMIT 1;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("entrc_guard_numberplates_id");
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to get last numberplate id ("+e.toString()+")");
            return -1;
        }
    }
}
