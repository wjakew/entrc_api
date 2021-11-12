/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for maintaining groups in Entrc Admin app
 * @author kubaw
 */
public class Database_Groups {

    /**
     *
     CREATE TABLE USER_GROUPS
     (
     user_groups_id INT PRIMARY KEY AUTO_INCREMENT,
     user_groups_name VARCHAR(30),
     user_groups_desc VARCHAR(250)
     );

     -- table for creating connection between worker and group
     CREATE TABLE GROUP_MEMBERS
     (
     user_groups_id INT,
     worker_id INT,
     CONSTRAINT fk_group_members FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
     CONSTRAINT fk_group_members2 FOREIGN KEY (user_groups_id) REFERENCES USER_GROUPS(user_groups_id)
     );
     */

    Database_Connector database;


    /**
     * Constructor
     * @param database
     */
    public Database_Groups(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for creating groups
     * @param group_name
     * @param group_desc
     * @return Integer
     */
    public int create_group(String group_name,String group_desc,int connected_object_id) throws SQLException{
        String query = "INSERT INTO USER_GROUPS (user_groups_name,user_groups_desc,object_connected_id) VALUES (?,?,?);";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,group_name);
            ppst.setString(2,group_desc);
            ppst.setInt(3,connected_object_id);

            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed to create group ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function fo removing
     * @param user_groups_id
     * @return Integer
     */
    public int remove_group(int user_groups_id) throws SQLException{
        String query = "DELETE FROM USER_GROUPS WHERE user_groups_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,user_groups_id);

            ppst.execute();
            return 1;
        }catch(SQLException e){
            database.log("Failed to remove group ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading group glances and info
     * @return String
     */
    public ArrayList<String> load_group_glances() throws SQLException{
        ArrayList<String> data = new ArrayList<>();

        String query = "SELECT * FROM USER_GROUPS;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add(rs.getInt("user_groups_id")+": "+rs.getString("user_groups_name"));
            }

            if ( data.size() == 0){
                data.add("Pusto");
            }
            return data;

        }catch(SQLException e){
            database.log("Failed to get all group glances ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for loading group
     * @param user_groups_id
     * @return ArrayList
     */
    public ArrayList<String> load_group_users(int user_groups_id) throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        /**
         * -- table for creating connection between worker and group
         CREATE TABLE GROUP_MEMBERS
         (
         user_groups_id INT,
         worker_id INT,
         CONSTRAINT fk_group_members FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
         CONSTRAINT fk_group_members2 FOREIGN KEY (user_groups_id) REFERENCES USER_GROUPS(user_groups_id)
         );
         */
        String query = "SELECT * FROM GROUP_MEMBERS WHERE user_groups_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,user_groups_id);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                int worker_id = rs.getInt("worker_id");
                Database_Worker dw = new Database_Worker(database);
                ArrayList<String> worker_data = dw.get_worker_byid(worker_id);
                data.add(worker_id+": "+worker_data.get(0)+" "+worker_data.get(1));
            }
            if ( data.size() == 0){
                data.add("Pusto");
            }
            return data;
        }catch(SQLException e){
            database.log("Failed to load group users ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for checking if user is in group
     * @param worker_id
     * @param user_groups_id
     * @return Integer
     * @throws SQLException
     */
    public int check_user_group(int worker_id,int user_groups_id) throws SQLException{
        String query = "SELECT worker_id FROM GROUP_MEMBERS WHERE user_groups_id = ? and worker_id = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setInt(1,user_groups_id);
            ppst.setInt(2,worker_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return 1;
            }
            return 0;
        }catch(SQLException e){
            database.log("Failed to check user in group ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for adding worker to group
     * @param user_groups_id
     * @param worker_id
     * @return Integer
     */
    public int add_worker_to_group(int user_groups_id,int worker_id) throws SQLException{
        if ( check_user_group(worker_id,user_groups_id) == 0){
            /**
             * -- table for creating connection between worker and group
             CREATE TABLE GROUP_MEMBERS
             (
             user_groups_id INT,
             worker_id INT,
             CONSTRAINT fk_group_members FOREIGN KEY (worker_id) REFERENCES WORKER(worker_id),
             CONSTRAINT fk_group_members2 FOREIGN KEY (user_groups_id) REFERENCES USER_GROUPS(user_groups_id)
             );
             */
            String query = "INSERT INTO GROUP_MEMBERS (user_groups_id,worker_id) VALUES (?,?);";

            try{
                PreparedStatement ppst = database.con.prepareStatement(query);

                ppst.setInt(1,user_groups_id);
                ppst.setInt(2,worker_id);

                ppst.execute();
                return 1;
            }catch(SQLException e){
                database.log("Failed to add worker to group ("+e.toString()+")");
                return -2;
            }
        }
        else{
            return 0;
        }
    }

}
