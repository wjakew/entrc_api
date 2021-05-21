package com.jakubwawak.handler;


import com.jakubwawak.barcode.BarCodeCreator;
import com.jakubwawak.barcode.Database_BarcodeGenerator;
import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.database.Database_Worker;

import java.sql.SQLException;

/**
 * Object for setting data from and to Barcode
 */
public class Barcode_Handler {

    Database_Connector database;
    String worker_pin;


    /**
     * Constructor
     * @param database
     * @param worker_pin
     */
    public Barcode_Handler(Database_Connector database, String worker_pin){
        this.database = database;
        this.worker_pin = worker_pin;
    }

    /**
     * Function for creating a response for the API
     * @return BarCodeCreator
     * @throws SQLException
     */
    public BarCodeCreator response() throws SQLException {
        Database_BarcodeGenerator dbg = new Database_BarcodeGenerator(database);
        Database_Worker dw = new Database_Worker(database);
        int worker_id = dw.get_worker_id_bypin(worker_pin);

        if ( worker_id > 0 ){
            if ( dbg.check_barcode_exist(worker_id) == 1){
                return dbg.retrive_barecode(worker_id);
            }
            else{
                // no barcode for user
                return null;
            }
        }
        else{
            // response should be empty
            return null;
        }
    }

}
