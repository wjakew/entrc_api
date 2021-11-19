/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.mobile_support;

import com.jakubwawak.barcode.BarCodeCreator;
import com.jakubwawak.barcode.Database_BarcodeGenerator;
import com.jakubwawak.database.Database_ProgramCodes;
import com.jakubwawak.database.Database_Worker;
import com.jakubwawak.entrc_api.EntrcApi;

import java.util.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Object for retriving data from database for mobile app
 */
public class Mobile_Barcode {

    public boolean mobile_enabled;
    public Date barcode_date;
    public int worker_id;
    public String barcode_raw_data;
    public boolean found;
    public boolean error;
    String worker_pin;

    /**
     * Constructor
     * @param worker_pin
     */
    public Mobile_Barcode(String worker_pin) throws SQLException {
        this.worker_pin = worker_pin;
        barcode_date = null;
        worker_id = -1;
        barcode_raw_data = "";
        found = false;
        error = false;
        check_enabled();
    }

    /**
     * Function for checking if mobile support is enabled on database
     */
    void check_enabled() throws SQLException {
        try{
            Database_ProgramCodes dpc = new Database_ProgramCodes(EntrcApi.database);
            mobile_enabled = dpc.get_value("MOBILE_SUPPORT").equals("YES");
        }catch(Exception e){
            EntrcApi.eal.add("Error checking enabled ("+e.toString()+")");
            error = true;
        }

    }

    /**
     * Function for retriving data from database
     */
    public void retrive() throws SQLException {
        if ( found && !error ) {
            try{
                Database_BarcodeGenerator dbg = new Database_BarcodeGenerator(EntrcApi.database);
                Database_Worker dw = new Database_Worker(EntrcApi.database);
                worker_id = dw.get_worker_id_bypin(worker_pin);
                if (worker_id > 0) {
                    found = true;
                    BarCodeCreator barcode_object = dbg.retrive_barecode(worker_id);
                    barcode_date = barcode_object.date;
                    barcode_raw_data = barcode_object.raw_barecode_data;
                } else {
                    found = false;
                }
            }catch(Exception e){
                EntrcApi.eal.add("Error retriving barcode ("+e.toString()+")");
                error = true;
            }

        }
    }


}
