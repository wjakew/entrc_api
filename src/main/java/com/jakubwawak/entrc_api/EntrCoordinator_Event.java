package com.jakubwawak.entrc_api;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.database.Database_Worker;
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
    }

    /**
     * Function for authorization user on shelf
     * @return boolean
     */
    public void authorize(Database_Connector database) throws SQLException {
        if( database.get_worker_data(worker_pin)!= null) {
            Database_Worker dw = new Database_Worker(database);
            if ( dw.check_worker_graveyard(database.get_worker_id_bypin(worker_pin))!= null) {
                EntrcApi.dac.log("WORKER id(" + database.get_worker_id_bypin(worker_pin) + ") authorized on shelf");
                authorization = database.get_worker_data(worker_pin);
            }
            else{
                authorization = "account_blocked";
            }
        }
        else{
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
}
