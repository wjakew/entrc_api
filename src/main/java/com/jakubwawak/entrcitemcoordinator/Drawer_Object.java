/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *Object for storing Drawer data
 * @author jakubwawak
 */
public class Drawer_Object {
    /**
     *      -- table for storing item data in ENTRC DRAWER
     CREATE TABLE ENTRC_IC_DRAWER
     (
     entrc_ic_drawer_id INT PRIMARY KEY AUTO_INCREMENT,
     admin_id INT,
     entrc_ic_drawer_code VARCHAR(10),
     entrc_ic_drawer_name VARCHAR(100),
     entrc_ic_drawer_desc VARCHAR(150),
     entrc_ic_drawer_size INT,
     entrc_ic_drawer_place VARCHAR(250),

     CONSTRAINT fk_entrcidrawer FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
     );
     */

    int entrc_ic_drawer_id;
    int admin_id;
    int size;
    String entrc_ic_drawer_code;
    String entrc_ic_drawer_name;
    String entrc_ic_drawer_desc;
    String entrc_ic_drawer_place;

    boolean blank;

    /**
     * Constructor creating object with database data
     * @param rs
     */
    public Drawer_Object(ResultSet rs) throws SQLException{
        entrc_ic_drawer_id = rs.getInt("entrc_ic_drawer_id");
        entrc_ic_drawer_name = rs.getString("entrc_ic_drawer_name");
        admin_id = rs.getInt("admin_id");
        entrc_ic_drawer_code = rs.getString("entrc_ic_drawer_code");
        entrc_ic_drawer_desc = rs.getString("entrc_ic_drawer_desc");
        entrc_ic_drawer_place = rs.getString("entrc_ic_drawer_place");
        size = rs.getInt("entrc_ic_drawer_size");
        blank = false;
    }

    /**
     * Constructor with blank fields
     */
    public Drawer_Object(){
        entrc_ic_drawer_id = -1;
        admin_id = -1;
        entrc_ic_drawer_code = "";
        entrc_ic_drawer_desc = "";
        entrc_ic_drawer_place = "";
        entrc_ic_drawer_name = "";
        size = 0;
        blank = true;
    }

    /**
     * Function for showing raw data of the object
     * @return String
     */
    public String show_data(){
        return entrc_ic_drawer_id + "/"+size+"/"+entrc_ic_drawer_name+"/"+entrc_ic_drawer_code+"/"+entrc_ic_drawer_place+"/"+entrc_ic_drawer_desc;
    }

    /**
     * Function for loading content of the drawer
     * @return DefaultTableModel
     */
    public DefaultTableModel get_content_model(EntrCoordinator_Item eci) throws SQLException{
        String query = "SELECT * FROM ENTRC_IC_ITEM where entrc_ic_drawer_id = ?;";
        ArrayList<String[]> data = new ArrayList<>();
        try{
            PreparedStatement ppst = eci.dic.database.con.prepareStatement(query);
            ppst.setInt(1,this.entrc_ic_drawer_id);
            eci.dic.database.log("Loading content model for drawer (id"+this.entrc_ic_drawer_id+")");
            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                int entrc_ic_item_id = rs.getInt("entrc_ic_item_id");
                data.add(eci.get_item(entrc_ic_item_id).show_table_glance(new EntrCoordinator_Drawer(eci.dic)));
            }

            if (data.size() == 0){
                data.add(new String[]{"Pusto"});
            }

            DefaultTableModel dtm = new DefaultTableModel();
            dtm.addColumn("id");
            dtm.addColumn("nazwa");
            dtm.addColumn("stan/posiadanie");
            dtm.addColumn("lokalizacja");
            for( String[] element : data ){
                dtm.addRow(element);
            }

            return dtm;

        }catch(SQLException e){
            eci.dic.database.log("Failed to get drawer content ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting items from drawers
     * @return ArrayList
     */
    public ArrayList<String> get_items(Database_Item_Coordinator dic) throws SQLException{
        ArrayList<String> items = new ArrayList<>();

        String query = "SELECT * FROM ENTRC_IC_ITEMS where entrc_ic_drawer_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);
            ppst.setInt(1,this.entrc_ic_drawer_id);

            ResultSet rs = ppst.executeQuery();

            while( rs.next() ){
                items.add(rs.getInt("entrc_ic_items_id")+": "+rs.getString("entrc_ic_items_name"));
            }

            if ( items.size() == 0){
                items.add("Pusto");
            }

            return items;
        }catch(SQLException e){
            dic.database.log("Failed to get items from drawer (id"+this.entrc_ic_drawer_id+") ("+e.toString()+")");
            return null;
        }
    }



}
