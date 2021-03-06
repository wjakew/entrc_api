/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.database.Database_APIController;
import com.jakubwawak.entrc_api.EntrcApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class EntrCoordinator_Handler {

    /**
     * Function for authorization on shelfs
     * @param shelf
     * @param pin
     * @return EntrCoordinator_Event
     * @throws SQLException
     * --modes:
     * field: authorization
     * possible codes:
     * 1. name surname - authorization granted
     * 2. no_authorization - user not authorized on the shelf
     * 3. no_shelf         - wrong shelf code
     * 4. account_blocked  - user account blocked
     * 5. no_auth          - no authorization, wrong pin
     */
    @GetMapping("/entrcoordinator-auth/{app_code}/{shelf}/{pin}")
    public EntrCoordinator_Event authorize(@PathVariable String shelf,@PathVariable String app_code,@PathVariable String pin) throws SQLException {
        EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - AUTHORIZATION");
        EntrcApi.eal.add("REQUEST: Got data: pin("+pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: AUTH_USER");
        EntrCoordinator_Event ece= new EntrCoordinator_Event(pin,shelf);
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        if ( dac.check_apicode(app_code) ){
            EntrcApi.eal.add("APPCODE - CHECK - CORRECT");
            ece.authorize(EntrcApi.database);
            EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - AUTHORIZATION");
            ece.flag = 1;
        }
        else{
            EntrcApi.eal.add("APPCODE - CHECK - FAILED");
            ece.flag = -1;

        }
        return ece;
    }

    /**
     * Function for listing object on shelf
     * @param shelf
     * @return Drawer_Elements
     * @throws SQLException
     */
    @GetMapping("/entrcoordinator-list/{shelf}")
    public Drawer_Elements list_items(@PathVariable String shelf) throws SQLException {
        EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - LIST OBJECTS");
        EntrcApi.eal.add("REQUEST FOR DRAWER: "+shelf);
        Database_Item_Coordinator dic = new Database_Item_Coordinator(EntrcApi.database);
        EntrCoordinator_Drawer ecd = new EntrCoordinator_Drawer(dic);
        int entrc_ic_drawer_id = ecd.get_drawer_id(shelf);
        Drawer_Elements de = new Drawer_Elements(entrc_ic_drawer_id,EntrcApi.database);
        if ( entrc_ic_drawer_id > 0){
            de.exists = true;
            de.load_drawer_items();
            de.load_drawer_glances();
            return de;
        }
        else{
            de.exists = false;
        }
        return de;
    }

    @GetMapping("/entrcoordinator-item-status/{shelf}/{app_code}/{item_id}")
    public EntrCoordinator_Event item_status(@PathVariable String shelf,@PathVariable String app_code,@PathVariable int item_id) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: STATUS_ITEM");
        EntrCoordinator_Event ece = new EntrCoordinator_Event(0,item_id,shelf);
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        if ( dac.check_apicode(app_code) ){
            EntrcApi.eal.add("APPCODE - CHECK - SUCCESS");
            ece.status_item(EntrcApi.database);
            EntrcApi.eal.add("Returned item status");
        }
        else{
            EntrcApi.eal.add("APPCODE - CHECK - FAILED");
            ece = new EntrCoordinator_Event(0,item_id,shelf);
            ece.flag = -1;
            ece.item_get_code = "worker_error";
            EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
        }
        return ece;

    }

    @GetMapping("/entrcoordinator-item-details/{shelf}/{app_code}/{item_id}")
    public Item_Object get_item_details(@PathVariable String shelf,@PathVariable String app_code,@PathVariable int item_id) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: DETAILS_ITEM");
        Item_Object io = new Item_Object();
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        Database_Item_Coordinator dic = new Database_Item_Coordinator(EntrcApi.database);
        if (dac.check_apicode(app_code)){
            EntrcApi.eal.add("APPCODE - CHECK - SUCCESS");
            EntrCoordinator_Item eci = new EntrCoordinator_Item(dic);
            io = eci.get_item(item_id);
            if ( io == null ){
                EntrcApi.eal.add("Item not found");
                io = new Item_Object();
                io.flag = -3;
            }
            else{
                EntrcApi.eal.add("Item found, data loaded");
                io.flag = 1;
            }
        }
        else{
            EntrcApi.eal.add("APPCODE - CHECK - FAILED");
            io.flag = -1;
        }
        return io;
    }

    /**
     * Function for getting item from database
     * @param shelf
     * @param item_id
     * @param worker_pin
     * @return EntrCoordinatorEvent
     * @throws SQLException
     * field: item_get_code
     * possible codes:
     * 1. no_avaiable - item not in the shelf
     * 2. item_taken  - item taken by other user
     * 3. error       - error getting item
     */
    @GetMapping("/entrcoordinator-getitem/{shelf}/{app_code}/{item_id}/{worker_pin}")
    public EntrCoordinator_Event get_item(@PathVariable String shelf,@PathVariable String app_code,@PathVariable int item_id,@PathVariable String worker_pin) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_pin("+worker_pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: GET_ITEM");
        EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        if ( dac.check_apicode(app_code)){
            if ( EntrcApi.database.get_worker_id_bypin(worker_pin) > 0){
                EntrcApi.eal.add("APPCODE - CHECK - CORRECT");
                ece.get_item(EntrcApi.database);
                EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
                ece.flag = 1;
                return ece;
            }
        }
        else{
            EntrcApi.eal.add("APPCODE - CHECK - FAILED");
            ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
            ece.flag = -1;
            ece.item_get_code = "worker_error";
            EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
        }
        return ece;
    }

    /**
     * Function for returining the item
     * @param shelf
     * @param item_id
     * @param worker_pin
     * @return EntrCoordinator_Event
     * @throws SQLException
     * field: item_get_code
     * possible codes:
     * 1. item_returned - item returned by user
     * 2. error - error returning the item
     * 3. no_item - item not found / no in state to be returned
     */
    @GetMapping("/entrcoordinator-returnitem/{shelf}/{app_code}/{item_id}/{worker_pin}")
    public EntrCoordinator_Event return_item(@PathVariable String shelf,@PathVariable String app_code, @PathVariable int item_id,@PathVariable String worker_pin) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_pin("+worker_pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: RETURN_ITEM");
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
        if ( dac.check_apicode(app_code)){
            EntrcApi.eal.add("APPCODE - CHECK - CORRECT");
            if ( EntrcApi.database.get_worker_id_bypin(worker_pin) > 0){
                ece.return_item(EntrcApi.database);
                EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
                ece.flag = 1;
                return ece;
            }
        }
        else{
            EntrcApi.eal.add("APPCODE - CHECK - FAILED");
            ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
            ece.item_get_code = "worker_error";
            EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
            ece.flag = -1;

        }
        return ece;
    }
}
