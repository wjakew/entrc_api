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
        EntrcApi.eal.add("REQUEST: Got data: pin("+pin+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: AUTH_USER");
        EntrCoordinator_Event ece= new EntrCoordinator_Event(pin,shelf);
        ece.authorize(EntrcApi.database);
        return ece;
    }

    @GetMapping("/entrcoordinator-getitem/{shelf}/{item_id}/{worker_id}")
    public EntrCoordinator_Event get_item(@PathVariable String shelf,@PathVariable int item_id,@PathVariable int worker_id) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_id("+worker_id+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: GET_ITEM");
        EntrCoordinator_Event ece = new EntrCoordinator_Event(worker_id,item_id,shelf);
        ece.get_item(EntrcApi.database);
        return ece;
    }

    @GetMapping("/entrcoordinator-returnitem/{shelf}/{item_id}/{worker_id}")
    public EntrCoordinator_Event return_item(@PathVariable String shelf,@PathVariable int item_id,@PathVariable int worker_id) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: item_id("+item_id+") shelf("+shelf+") worked_id("+worker_id+")");
        EntrcApi.eal.add("REQUEST FROM SHELF: "+shelf);
        EntrcApi.eal.add("TYPE: RETURN_ITEM");
        EntrCoordinator_Event ece = new EntrCoordinator_Event(worker_id,item_id,shelf);
        ece.return_item(EntrcApi.database);
        return ece;
    }
}
