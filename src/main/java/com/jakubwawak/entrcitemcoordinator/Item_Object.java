/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_Worker;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *Object for storing item data
 * @author jakubwawak
 */
public class Item_Object {


    /**
     *  entrc_ic_item_id INT PRIMARY KEY AUTO_INCREMENT,
     entrc_ic_drawer_id INT,
     entrc_ic_category_id int,
     admin_id INT,
     worker_id INT,
     entrc_ic_item_name VARCHAR(100),
     entrc_ic_item_desc VARCHAR(100),
     */

    int entrc_ic_item_id;
    int entrc_ic_drawer_id;
    int entrc_ic_category_id;
    int admin_id;
    int worker_id;
    String entrc_ic_item_name;
    String entrc_ic_item_desc;
    boolean new_object;
    /**
     * Constructor with database
     * @param rs
     */
    public Item_Object(ResultSet rs) throws SQLException{
        entrc_ic_item_id = rs.getInt("entrc_ic_item_id");
        entrc_ic_drawer_id = rs.getInt("entrc_ic_drawer_id");
        entrc_ic_category_id = rs.getInt("entrc_ic_category_id");
        admin_id = rs.getInt("admin_id");
        worker_id = rs.getInt("worker_id");
        entrc_ic_item_name = rs.getString("entrc_ic_item_name");
        entrc_ic_item_desc = rs.getString("entrc_ic_item_desc");
        new_object = false;
    }
    /**
     * Constructor
     */
    public Item_Object(){
        entrc_ic_item_id = -1;
        entrc_ic_drawer_id = 1;
        entrc_ic_category_id = 1;
        admin_id = -1;
        worker_id = -1;
        entrc_ic_item_name = "";
        entrc_ic_item_desc = "";
        new_object = true;
    }

    /**
     * Function for showing item data for table
     * @param ecd
     * @return String
     * @throws SQLException
     */
    public String[] show_table_glance(EntrCoordinator_Drawer ecd) throws SQLException{
        // id name WORKER DRAWER
        String[] data = new String[4];

        data[0] = Integer.toString(entrc_ic_item_id);
        data[1] = entrc_ic_item_name;

        if ( worker_id == -1){
            data[2] = "W SZAFCE";
        }
        else{
            data[2] = ecd.dic.database.get_worker_data(worker_id);
        }

        data[3] = ecd.get_drawer_name(entrc_ic_drawer_id);

        return data;
    }
}
