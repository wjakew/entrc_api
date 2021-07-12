package com.jakubwawak.entrc_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;


@RestController
public class Barcode_Handler {

    @GetMapping("/barcodes/{appcode}/{pin}")
    public Barcode_Object retrive_barcode(@PathVariable String appcode, @PathVariable String pin) throws SQLException {
        EntrcApi.eal.add("REQUEST: Got data: appcode("+appcode+") pin("+pin+")");

        Barcode_Object barcode_obj = new Barcode_Object(pin,appcode);

        return barcode_obj;
    }
}
