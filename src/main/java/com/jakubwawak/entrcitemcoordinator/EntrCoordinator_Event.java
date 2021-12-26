/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.database.Database_Groups;
import com.jakubwawak.database.Database_Worker;
import com.jakubwawak.entrc_api.EntrcApi;
import com.jakubwawak.entrcitemcoordinator.Database_Item_Coordinator;
import com.jakubwawak.entrcitemcoordinator.EntrCoordinator_Item;

import java.sql.SQLException;

public class EntrCoordinator_Event {

    public String shelf;

    public int worker_id;
    public int item_id;
    public Database_Connector database;

    public String worker_pin;
    public String authorization;

    public String item_get_code;

    public int flag;

    /**
     * Constructor for taking the item
     * @param worker_id
     * @param item_id
     */
    public EntrCoordinator_Event(int worker_id,int item_id,String shelf){
        this.worker_id = worker_id;
        this.item_id = item_id;
        this.shelf = shelf;
        authorization = "";
        flag = 0;
    }

    /**
     * Cpnstructor for authorize worker on shelf
     * @param worker_pin
     */
    public EntrCoordinator_Event(String worker_pin,String shelf){
        this.worker_pin = worker_pin;
        this.worker_id = -1;
        this.shelf = shelf;
        this.item_id = -1;
        authorization = "";
    }

    /**
     * Function for authorization user on shelf
     * @return boolean
     */
    public void authorize(Database_Connector database) throws SQLException {
        if( database.get_worker_data(worker_pin)!= null) {
            Database_Worker dw = new Database_Worker(database);
            if ( dw.check_worker_graveyard(database.get_worker_id_bypin(worker_pin))== null) {
                EntrcApi.dac.log("WORKER id(" + database.get_worker_id_bypin(worker_pin) + ") is not blocked on database.");
                EntrcApi.eal.add("Checking group authorization...");
                Database_Item_Coordinator dic = new Database_Item_Coordinator(database);
                EntrCoordinator_Drawer ecd = new EntrCoordinator_Drawer(dic);
                int entrc_ic_drawer_id = ecd.get_drawer_id(shelf);
                if ( entrc_ic_drawer_id > 0){
                    int user_groups_id = ecd.get_authorization_group_id(shelf);
                    Database_Groups dg = new Database_Groups(database);
                    switch(dg.check_user_group(database.get_worker_id_bypin(worker_pin),user_groups_id)){
                        case 1:
                        {
                            // authorization granted by group linked with one of the drawers
                            authorization = database.get_worker_data(worker_pin);
                            EntrcApi.eal.add("USER "+authorization+" AUTHORIZED ON GROUP WITH DRAWER");
                            break;
                        }
                        default:
                        {
                            // trying to authorize on EIC group
                            if ( dg.check_user_group(database.get_worker_id_bypin(worker_pin),1) == 1){
                                authorization = database.get_worker_data(worker_pin);
                                EntrcApi.eal.add("USER "+authorization+" AUTHORIZED ON EIC GROUP");
                                break;
                            }
                            else{
                                authorization = "no_authorization";
                            }
                        }
                    }
                }
                else{
                    authorization = "no_shelf";
                }
            }
            else{
                EntrcApi.eal.add("User blocked on database. Shelf not granted");
                authorization = "account_blocked";
            }
        }
        else{
            EntrcApi.eal.add("Wrong pin. Shelf not granted - user not found");
            authorization = "no_auth";
        }
    }

    /**
     * Function for getting item
     * @return boolean
     */
    public void get_item(Database_Connector database) throws SQLException {
        Database_Item_Coordinator dic = new Database_Item_Coordinator(database);
        EntrCoordinator_Item eci=new EntrCoordinator_Item(dic);

        if ( eci.check_state(item_id) < 0 ){
            // state is correct - item in the shelf
            if ( eci.move_item_worker(item_id,worker_id) == 1){
                item_get_code = "item_taken";
            }
            else{
                item_get_code = "error";
            }
        }
        else {
            item_get_code = "no_avaiable";
        }
    }

    /**
     * Function for returning item to the shelf
     * @param database
     */
    public void return_item(Database_Connector database) throws SQLException {
        Database_Item_Coordinator dic = new Database_Item_Coordinator(database);
        EntrCoordinator_Item eci=new EntrCoordinator_Item(dic);
        if (eci.check_state(item_id) == worker_id){
            if( eci.remove_item_worker(item_id) == 1){
                item_get_code = "item_returned";
            }
            else{
                item_get_code = "error";
            }
        }
        else{
            // item not on logged on shelf worker
            item_get_code = "no_item";
        }
    }

    /**
     * Function for getting status of item
     * @param database
     */
    public void status_item(Database_Connector database) throws SQLException {
        Database_Item_Coordinator dic = new Database_Item_Coordinator(database);
        EntrCoordinator_Item eci = new EntrCoordinator_Item(dic);
        switch( eci.check_state(item_id) ){
            case -2:
                item_get_code = "item_shelf";
                break;
            case -3:
                item_get_code = "no_item";
                break;
            default:
                item_get_code = "item_taken:"+eci.check_state(item_id);
                break;
        }
    }
}
