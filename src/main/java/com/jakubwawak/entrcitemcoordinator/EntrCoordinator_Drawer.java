/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;

/**
 *Function for managing drawers in entrc
 * @author jakubwawak
 */
public class EntrCoordinator_Drawer {
    /**
     *      -- table for storing item data in ENTRC DRAWER
     CREATE TABLE ENTRC_IC_DRAWER
     (
     entrc_ic_drawer_id INT PRIMARY KEY AUTO_INCREMENT,
     admin_id INT,
     entrc_ic_drawer_code VARCHAR(10),
     entrc_ic_drawer_name VARCHAR(100),
     entrc_ic_drawer_desc VARCHAR(150),
     entrc_ic_drawer_place VARCHAR(250),

     CONSTRAINT fk_entrcidrawer FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
     );
     */

    Database_Item_Coordinator dic;
    /**
     * Constructor
     * @param dic
     */
    public EntrCoordinator_Drawer(Database_Item_Coordinator dic){
        this.dic = dic;
    }

    /**
     * Function for creating name for drawer
     * @return String
     */
    String drawer_code_generator() throws SQLException{
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        boolean done = false;
        while(!done){
            for(int i = 0; i < 10; i++) {
                int index = random.nextInt(alphabet.length());
                char randomChar = alphabet.charAt(index);
                sb.append(randomChar);
            }
            if ( check_drawer_code(sb.toString()) ){
                done = true;
            }
        }
        return sb.toString();
    }

    /**
     * Function for getting group id of drawer
     * @param drawer_code
     * @return Integer
     * @throws SQLException
     */
    public int get_authorization_group_id(String drawer_code) throws SQLException {
        String query = "SELECT user_groups_id FROM USER_GROUPS where object_connected_id = ?;";
        int entrc_ic_drawer_id = get_drawer_id(drawer_code);
        if ( entrc_ic_drawer_id > 0 ){
            try{
                PreparedStatement ppst = dic.database.con.prepareStatement(query);
                ppst.setInt(1,entrc_ic_drawer_id);
                ResultSet rs = ppst.executeQuery();
                if (rs.next()){
                    return rs.getInt("user_groups_id");
                }
                return 1;
            }catch(SQLException e){
                dic.database.log("Failed to get drawer_id ("+e.toString()+")");
                return -3;
            }
        }
        else{
            return -1;
        }
    }

    /**
     * Function for checking drawer code if avaiable
     * @param code
     * @return boolean
     * @throws SQLException
     */
    boolean check_drawer_code(String code) throws SQLException{
        String query = "SELECT * FROM ENTRC_IC_DRAWER where entrc_ic_drawer_code = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setString(1,code);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return false;
            }
            return true;

        }catch(SQLException e){
            dic.database.log("Failed to check drawer code ("+e.toString()+")");
            return false;
        }
    }

    /**
     * Function for getting drawer id by given drawer code
     * @param drawer_code
     * @return Integer
     * @throws SQLException
     */
    int get_drawer_id(String drawer_code) throws SQLException {
        String query = "SELECT entrc_ic_drawer_id FROM ENTRC_IC_DRAWER where entrc_ic_drawer_code = ?;";
        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);
            ppst.setString(1,drawer_code);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("entrc_ic_drawer_id");
            }
            return -1;
        } catch (SQLException throwables) {
            dic.database.log("Failed to get drawer id ("+throwables.toString());
            return -2;
        }
    }

    /**
     * Function for creating drawer
     * @param name
     * @param desc
     * @param place
     * @return String (drawer code)
     */
    public String create_drawer(String name,String drawer_code,String desc,String place) throws SQLException{

        String query = "INSERT INTO ENTRC_IC_DRAWER\n" +
                "(admin_id,entrc_ic_drawer_code,entrc_ic_drawer_name,entrc_drawer_desc,entrc_ic_drawer_place)\n" +
                "VALUES\n" +
                "(?,?,?,?,?);";
        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,dic.database.admin_id);
            ppst.setString(2,drawer_code);
            ppst.setString(3,name);
            ppst.setString(4,desc);
            ppst.setString(5,place);


            ppst.execute();

            dic.log_action("DRAWER_CREATED","Code:"+drawer_code+" created.", dic.database.admin_id, 0);

            create_label();
            return drawer_code;
        }catch(SQLException e){
            dic.database.log("Failed to create drawer (name:"+name+") ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for creating label for drawer
     */
    void create_label() throws SQLException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        /**
         *  entrc_ic_drawer_label_id INT PRIMARY KEY AUTO_INCREMENT,
         entrc_ic_drawer_id INT,
         entrc_ic_drawer_label_label VARCHAR(10),
         entrc_ic_drawer_label_time TIMESTAMP,
         */
        String label = "LBL_"+drawer_code_generator();

        String query = " INSERT INTO ENTRC_IC_DRAWER_LABEL\n"
                + "(entrc_ic_drawer_id,entrc_ic_drawer_label_label,entrc_ic_drawer_label_time)\n"
                + "VALUES\n"
                + "(?,?,?);";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);
            ppst.setInt(1,get_last_drawer_id());
            ppst.setString(2,label);
            ppst.setObject(3,todayLocalDate);
            dic.log_action("INFO","Label created",dic.database.admin_id,get_last_drawer_id());
            ppst.execute();
        }catch(SQLException e){
            dic.database.log("Failed to create label ("+e.toString()+")");
        }
    }

    /**
     * Function for getting drawer data from database
     * @param entrc_ic_drawer_id
     * @return Drawer_Object
     */
    public Drawer_Object get_drawer(int entrc_ic_drawer_id) throws SQLException{
        String query = "SELECT * from ENTRC_IC_DRAWER where entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return new Drawer_Object(rs);
            }
            return null;
        }catch(SQLException e){
            dic.database.log("Failed to get drawer (id"+entrc_ic_drawer_id+") ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting drawer code
     * @param entrc_ic_drawer_id
     * @return String
     */
    public String get_drawer_code(int entrc_ic_drawer_id) throws SQLException{
        String query = "SELECT entrc_ic_drawer_code from ENTRC_IC_DRAWER where entrc_ic_drawer_id=?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1, entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("entrc_ic_drawer_code");
            }
            return null;
        }catch(SQLException e){
            dic.database.log("Failed to get drawer code ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting drawer label
     * @param entrc_ic_drawer_id
     * @return String
     */
    String get_drawer_label(int entrc_ic_drawer_id) throws SQLException{
        String query = "SELECT entrc_ic_drawer_label_label from ENTRC_IC_DRAWER_LABEL where entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);
            ppst.setInt(1,entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("entrc_ic_drawer_label_label");
            }
            return null;
        }catch(SQLException e){
            dic.database.log("Failed to get drawer label ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting last drawer id
     * @return Integer
     * @throws SQLException
     */
    public int get_last_drawer_id() throws SQLException{
        String query = "SELECT entrc_ic_drawer_id FROM ENTRC_IC_DRAWER ORDER BY entrc_ic_drawer_id DESC LIMIT 1;";
        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            if(rs.next()){
                return rs.getInt("entrc_ic_drawer_id");
            }
            return 0;
        }catch(SQLException e){
            dic.database.log("Failed to get last drawer id ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for adding drawer to database
     * @param to_add
     * @return Integer
     */
    public int add_drawer(Drawer_Object to_add) throws SQLException{
        String query = "INSERT INTO ENTRC_IC_DRAWER\n" +
                "(entrc_ic_drawer_code,admin_id,entrc_ic_drawer_name,entrc_ic_drawer_desc,entrc_ic_drawer_place,entrc_ic_drawer_size)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?);";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setString(1,to_add.entrc_ic_drawer_code);
            ppst.setInt(2,dic.database.admin_id);
            ppst.setString(3,to_add.entrc_ic_drawer_name);
            ppst.setString(4,to_add.entrc_ic_drawer_desc);
            ppst.setString(5,to_add.entrc_ic_drawer_place);
            ppst.setInt(6,to_add.size);
            ppst.execute();
            dic.database.log("Added new drawer to the database ("+to_add.entrc_ic_drawer_code+")");
            create_label();
            dic.log_action("DRAWER_ADD", "Item added!", dic.database.admin_id,get_last_drawer_id() );
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to add drawer to database ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating drawer on database
     * @param to_update
     * @return Integer
     * @throws SQLException
     */
    public int update_drawer(Drawer_Object to_update) throws SQLException{
        String query = "UPDATE ENTRC_IC_DRAWER\n" +
                "SET entrc_ic_drawer_name = ?, entrc_ic_drawer_desc = ?, entrc_ic_drawer_size = ?, entrc_ic_drawer_place  = ? WHERE entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setString(1, to_update.entrc_ic_drawer_name);
            ppst.setString(2,to_update.entrc_ic_drawer_desc);
            ppst.setInt(3,to_update.size);
            ppst.setString(4,to_update.entrc_ic_drawer_place);
            ppst.setInt(5,to_update.entrc_ic_drawer_id);


            ppst.execute();
            dic.log_action("DRAWER_UPDATE", "Item added!", dic.database.admin_id,get_last_drawer_id() );
            dic.database.log("Updated drawer on database ("+to_update.entrc_ic_drawer_code+")");
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to update drawer on database ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting drawer name
     * @param entrc_ic_drawer_id
     * @return String
     */
    public String get_drawer_name(int entrc_ic_drawer_id) throws SQLException{
        String query = "SELECT entrc_ic_drawer_name FROM ENTRC_IC_DRAWER WHERE entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1, entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("entrc_ic_drawer_name");
            }
            return null;

        }catch(SQLException e){
            dic.database.log("Failed to get drawer name ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for removing drawer
     * @param entrc_ic_drawer_id
     * @return int
     */
    public int remove_drawer(int entrc_ic_drawer_id) throws SQLException{
        // removing object from drawer
        String query = "SELECT entrc_ic_item_id FROM ENTRC_IC_ITEM WHERE entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                EntrCoordinator_Item eci = new EntrCoordinator_Item(dic);
                int counter = 0;
                if ( eci.remove_from_drawer(rs.getInt("entrc_ic_item_id")) != -1 ){
                    counter++;
                }
                dic.log_action("INFO", "CHANGED "+counter+" ITEMS DATA DUE TO DRAWER REMOVE", dic.database.admin_id, entrc_ic_drawer_id);
                dic.log_action("DRAWER_REMOVE", "Drawer (id"+entrc_ic_drawer_id+") removed.", dic.database.admin_id, entrc_ic_drawer_id);
            }

            query = "DELETE FROM ENTRC_IC_DRAWER where entrc_ic_drawer_id = ?;";
            ppst = dic.database.con.prepareStatement(query);
            ppst.setInt(1,entrc_ic_drawer_id);
            ppst.execute();
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to remove drawer ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading data to JList object
     * @return ArrayList
     */
    public ArrayList<String> load_glances() throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM ENTRC_IC_DRAWER;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while( rs.next() ){
                if ( rs.getInt("entrc_ic_drawer_id") != 1 )
                    data.add(rs.getInt("entrc_ic_drawer_id")+": "+rs.getString("entrc_ic_drawer_name"));
            }

            if ( data.size() == 0 ){
                data.add("Pusto");
            }
            return data;

        }catch(SQLException e){
            dic.database.log("Failed to load drawer glances ("+e.toString()+")");
            return null;
        }
    }
}
