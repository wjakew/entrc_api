/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.entrc_api.EntrcApi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for loading list of Drawer elements
 */
public class Drawer_Elements {

    private int entrc_ic_drawer_id;
    private ArrayList<Item_Object> drawer_elements;
    public ArrayList<String> drawer_elements_glances;
    private Database_Connector database;
    public boolean error;
    /**
     * Constructor
     * @param entrc_ic_drawer_id
     */
    public Drawer_Elements(int entrc_ic_drawer_id, Database_Connector database){
        this.entrc_ic_drawer_id = entrc_ic_drawer_id;
        drawer_elements = new ArrayList<>();
        drawer_elements_glances = new ArrayList<>();
        this.database = database;
        error = false;
    }

    /**
     * Function for loading items from drawers
     */
    public void load_drawer_items(){
        String query = "SELECT * FROM ENTRC_IC_ITEM WHERE entrc_ic_drawer_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,entrc_ic_drawer_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                drawer_elements.add(new Item_Object(rs));
            }
            EntrcApi.eal.add("Created list of "+drawer_elements.size()+" drawer elements for entrc_ic_drawer_id: "+entrc_ic_drawer_id);
        }catch(SQLException e){
            EntrcApi.eal.add("Failed to load drawer list for entrc_ic_drawer_id");
        }
    }

    /**
     * Function for loading drawer glances
     */
    public void load_drawer_glances(){
        for(Item_Object io : drawer_elements){
            drawer_elements_glances.add("id:"+io.entrc_ic_item_id+"\n"+io.entrc_ic_item_name+"\ncategory_id:"+io.entrc_ic_category_id);
        }
    }

}
