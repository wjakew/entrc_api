package com.jakubwawak.entrc_api;

import com.jakubwawak.barcode.Database_BarcodeGenerator;
import com.jakubwawak.database.Database_Worker;

import java.sql.SQLException;

public class Barcode_Object {

    public String pin;
    boolean auth;
    public String barcode;
    /**
     * Constructor
     * @param given_pin
     */
    public Barcode_Object(String given_pin,String appcode) throws SQLException {
        this.pin = given_pin;
        this.auth = check_auth(appcode);
        load_barcode();
    }

    boolean check_auth(String appcode){
        Authorization auth = new Authorization(appcode);
        return auth.getEntrc_api_data() > 0;
    }

    /**
     * Function for loading object
     */
    void load_barcode() throws SQLException {
        if ( auth ){
            try{
                Database_BarcodeGenerator dbg = new Database_BarcodeGenerator(EntrcApi.database);
                Database_Worker dw = new Database_Worker(EntrcApi.database);
                int worker_id = dw.get_worker_id_bypin(pin);

                if ( worker_id > 0 ){
                    barcode = dbg.retrive_barecode(worker_id).raw_barecode_data;
                }
                else if ( worker_id == -1 ){
                    barcode = "error";
                }
                else{
                    barcode = "noworker";
                }

            }catch(SQLException e){
                EntrcApi.eal.add("Failed to load barcode to database ("+e.toString()+")");
            }
        }
        else{
            barcode = "blank";
        }

    }
}
