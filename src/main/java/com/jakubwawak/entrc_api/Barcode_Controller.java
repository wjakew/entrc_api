package com.jakubwawak.entrc_api;


import com.jakubwawak.barcode.BarCodeCreator;
import com.jakubwawak.handler.Barcode_Handler;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@CrossOrigin
@RestController
@RequestMapping("/api/barcodes")
public class Barcode_Controller {

    EntrcApi_Logger eal;

    Barcode_Controller(){
        eal = new EntrcApi_Logger("test");

    }

    @GetMapping(value = "/{pin}",produces = "application/json")
    public BarCodeCreator get_all_barcodes(@PathVariable String pin) throws SQLException {
        Barcode_Handler bc = new Barcode_Handler(EntrcApi.database,pin);
        if (bc.response() != null){
            return bc.response();
        }
        return null ;
    }
}
