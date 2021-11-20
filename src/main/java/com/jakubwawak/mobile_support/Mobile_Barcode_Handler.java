package com.jakubwawak.mobile_support;

import com.jakubwawak.entrc_api.EntrcApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Mobile_Barcode_Handler {

    @GetMapping("/mobile-barcode/{worker_pin}")
    public Mobile_Barcode mobile_barcode_request(@PathVariable String worker_pin) throws SQLException {
        EntrcApi.eal.add("Got job for mobile - barcode for worker_pin("+worker_pin+")");
        Mobile_Barcode mobile_barcode = new Mobile_Barcode(worker_pin);
        mobile_barcode.retrive();
        EntrcApi.eal.add("barcode - found:"+mobile_barcode.found);
        return mobile_barcode;
    }

}
