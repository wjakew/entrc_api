package com.jakubwawak.entrc_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;


@RestController
public class Barcode_Handler {

    @GetMapping("/barcodes/{appcode}/{pin}")
    public Barcode_Object retrive_barcode(@RequestParam(value = "appcode",defaultValue = "blank") String appcode
    ,@RequestParam(value = "pin", defaultValue = "nopin") String pin) throws SQLException {
        return new Barcode_Object(appcode,pin);
    }
}
