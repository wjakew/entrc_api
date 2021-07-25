/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 *Object for managing item on database
 * @author jakubwawak
 */
public class EntrCoordinator_Item {

    Database_Item_Coordinator dic;

    /**
     * Construcor
     * @param dic
     */
    public EntrCoordinator_Item(Database_Item_Coordinator dic){
        this.dic = dic;
    }

    /**
     * Function for getting last item id
     * @return Integer
     */
    public int get_last_item_id() throws SQLException{
        String query = "SELECT entrc_ic_item_id FROM ENTRC_IC_ITEM ORDER BY entrc_ic_item_id DESC LIMIT 1;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("entrc_ic_item_id");
            }
            return 0;

        }catch(SQLException e){
            dic.database.log("Failed to get last item id ("+e.toString()+")");
            return -1;
        }

    }

    /**
     * Function for getting item from database
     * @param entrc_ic_item_id
     * @return Item_Object
     */
    public Item_Object get_item(int entrc_ic_item_id) throws SQLException{
        String query = "SELECT * from ENTRC_IC_ITEM where entrc_ic_item_id=?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_item_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return new Item_Object(rs);
            }
            return null;
        }catch(SQLException e){
            dic.database.log("Failed to get item ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for adding item to database
     * @param to_add
     */
    public int add_item(Item_Object to_add) throws SQLException{
        String query = "INSERT INTO ENTRC_IC_ITEM\n" +
                "(entrc_ic_drawer_id,entrc_ic_category_id,admin_id,worker_id,entrc_ic_item_name,entrc_ic_item_desc)\n" +
                "VALUES\n" +
                "(?,?,?,?,?,?);";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,to_add.entrc_ic_drawer_id);
            ppst.setInt(2,to_add.entrc_ic_category_id);
            ppst.setInt(3,dic.database.admin_id);
            ppst.setInt(4,to_add.worker_id);
            ppst.setString(5,to_add.entrc_ic_item_name);
            ppst.setString(6,to_add.entrc_ic_item_desc);

            ppst.execute();
            dic.log_action("ITEM_ADD", "Item added by admin (id+"+dic.database.admin_id+")!", dic.database.admin_id,get_last_item_id() );
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to add item ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking state of the item
     * @param entrc_ic_item_id
     * @return Integer
     */
    public int check_state(int entrc_ic_item_id) throws SQLException{
        String query = "SELECT worker_id FROM ENTRC_IC_ITEM where entrc_ic_item_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1, entrc_ic_item_id);

            ResultSet rs = ppst.executeQuery();

            if(rs.next()){
                return rs.getInt("worker_id");
            }
            return -2;
        }catch(SQLException e){
            dic.database.log("Failed to check state of an item ("+e.toString()+")");
            return -3;
        }
    }

    /**
     * Function for
     * @param entrc_ic_item
     * @param worker_id
     * @return
     */
    public int move_item_worker(int entrc_ic_item,int worker_id) throws SQLException{
        String query = "UPDATE ENTRC_IC_ITEM SET worker_id = ? WHERE entrc_ic_item_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,worker_id);
            ppst.setInt(2,entrc_ic_item);

            ppst.execute();
            dic.log_action("ITEM_TAKEN", "Item was taken by worker "+dic.database.get_worker_data(worker_id)+".", worker_id, entrc_ic_item);
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to move item to worker ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for removing item worker
     * @param entrc_ic_item
     * @return
     * @throws SQLException
     */
    int remove_item_worker(int entrc_ic_item) throws SQLException{
        String query = "UPDATE ENTRC_IC_ITEM SET worker_id = -1 WHERE entrc_ic_item_id = ?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_item);

            ppst.execute();
            dic.log_action("ITEM_RETURNED", "Item was set to drawer by admin", dic.database.admin_id, entrc_ic_item);
            return 1;

        }catch(SQLException e){
            dic.database.log("Failed to remove item from worker ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for removing item from database
     * @param entrc_ic_item_id
     * @return
     */
    public int remove_item(int entrc_ic_item_id) throws SQLException{
        if ( check_state(entrc_ic_item_id) < 0 ){
            String query = "DELETE FROM ENTRC_IC_ITEM WHERE entrc_ic_item_id = ?;";
            try{
                PreparedStatement ppst = dic.database.con.prepareStatement(query);
                ppst.setInt(1, entrc_ic_item_id);

                ppst.execute();
                dic.log_action("ITEM_REMOVED", "Item was removed by admin", dic.database.admin_id, entrc_ic_item_id);
                return 1;
            }catch(SQLException e){
                dic.database.log("Failed to remove item ("+e.toString()+")");
                return -2;
            }
        }
        return check_state(entrc_ic_item_id);
    }

    /**
     * Function for removing item from drawer
     * @param entrc_ic_item_id
     * @return
     */
    public int remove_from_drawer(int entrc_ic_item_id) throws SQLException{
        String query = "UPDATE ENTRC_IC_ITEM SET entrc_ic_drawer_id = 1 where entrc_ic_item_id =?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,entrc_ic_item_id);

            ppst.execute();
            dic.log_action("ITEM_DRAWER_EDIT", "Removed item from drawer by admin (id"+dic.database.admin_id+")", dic.database.admin_id, entrc_ic_item_id);
            return entrc_ic_item_id;

        }catch(SQLException e){
            dic.database.log("Failed to remove item from drawer ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating item od database
     * @param to_add
     * @return Integer
     */
    public int update_item(Item_Object to_add) throws SQLException{
        String query = "UPDATE ENTRC_IC_ITEM\n" +
                "SET entrc_ic_drawer_id = ?,entrc_ic_category_id = ?,worker_id = ?,entrc_ic_item_name = ?,"
                + "entrc_ic_item_desc=? WHERE entrc_ic_item_id=?;";

        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ppst.setInt(1,to_add.entrc_ic_drawer_id);
            ppst.setInt(2,to_add.entrc_ic_category_id);
            ppst.setInt(3,to_add.worker_id);
            ppst.setString(4,to_add.entrc_ic_item_name);
            ppst.setString(5,to_add.entrc_ic_item_desc);

            ppst.setInt(6,to_add.entrc_ic_item_id);

            ppst.execute();
            dic.log_action("ITEM_UPDATE", "Item updated by admin (id"+dic.database.admin_id+")!", dic.database.admin_id,get_last_item_id() );
            return 1;
        }catch(SQLException e){
            dic.database.log("Failed to update item ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting all items to table model
     * @return DefaultTableModel
     */
    public DefaultTableModel get_all_items_model() throws SQLException{
        String query = "SELECT * FROM ENTRC_IC_ITEM;";
        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("id");
        dtm.addColumn("nazwa");
        dtm.addColumn("stan");
        dtm.addColumn("lokalizacja");
        try{
            PreparedStatement ppst = dic.database.con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while( rs.next() ){
                int entrc_ic_item_id = rs.getInt("entrc_ic_item_id");

                Item_Object item = get_item(entrc_ic_item_id);
                dtm.addRow(item.show_table_glance(new EntrCoordinator_Drawer(dic)));
            }
            return dtm;
        }catch(SQLException e){
            dic.database.log("Failed to get items for model ("+e.toString()+")");
            return null;
        }
    }
}
