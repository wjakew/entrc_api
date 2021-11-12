package com.jakubwawak.entrcitemcoordinator;

import com.jakubwawak.entrc_api.EntrcApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class EntrCoordinator_Handler {

    @GetMapping("/entrcoordinator-auth/{shelf}/{pin}")
    public EntrCoordinator_Event authorize(@PathVariable String shelf,@PathVariable String pin) throws SQLException {
        EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - AUTHORIZATION");
        EntrcApi.eal.add("REQUEST: Got data: pin("+pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: AUTH_USER");
        EntrCoordinator_Event ece= new EntrCoordinator_Event(pin,shelf);
        ece.authorize(EntrcApi.database);
        EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - AUTHORIZATION");
        return ece;
    }

    @GetMapping("/entcoordinator-list/{shelf}")
    public Drawer_Elements list_items(@PathVariable String shelf) throws SQLException {
        EntrcApi.eal.add("ENTRCOORDINATOR - SHELF - LIST OBJECTS");
        EntrcApi.eal.add("REQUEST FOR DRAWER: "+shelf);
        Database_Item_Coordinator dic = new Database_Item_Coordinator(EntrcApi.database);
        EntrCoordinator_Drawer ecd = new EntrCoordinator_Drawer(dic);
        int entrc_ic_drawer_id = ecd.get_drawer_id(shelf);
        Drawer_Elements de = new Drawer_Elements(entrc_ic_drawer_id,EntrcApi.database);
        if ( entrc_ic_drawer_id > 0){
            de.load_drawer_items();
            de.load_drawer_glances();
            return de;
        }
        de.error = true;
        return de;
    }

    @GetMapping("/entrcoordinator-getitem/{shelf}/{item_id}/{worker_pin}")
    public EntrCoordinator_Event get_item(@PathVariable String shelf,@PathVariable int item_id,@PathVariable String worker_pin) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_pin("+worker_pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: GET_ITEM");
        if ( EntrcApi.database.get_worker_id_bypin(worker_pin) > 0){
            EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
            ece.get_item(EntrcApi.database);
            EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
            return ece;
        }
        EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
        ece.item_get_code = "worker_error";
        EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
        return ece;



    }

    @GetMapping("/entrcoordinator-returnitem/{shelf}/{item_id}/{worker_pin}")
    public EntrCoordinator_Event return_item(@PathVariable String shelf,@PathVariable int item_id,@PathVariable String worker_pin) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_pin("+worker_pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: RETURN_ITEM");
        if ( EntrcApi.database.get_worker_id_bypin(worker_pin) > 0){
            EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
            ece.return_item(EntrcApi.database);
            EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
            return ece;
        }
        EntrCoordinator_Event ece = new EntrCoordinator_Event(EntrcApi.database.get_worker_id_bypin(worker_pin),item_id,shelf);
        ece.item_get_code = "worker_error";
        EntrcApi.eal.add("ITEM_ENGINE_CODE: "+ece.item_get_code);
        return ece;
    }
}
