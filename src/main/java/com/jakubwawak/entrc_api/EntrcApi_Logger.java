package com.jakubwawak.entrc_api;

import java.util.ArrayList;
import java.util.Date;

public class EntrcApi_Logger {

    Date actual_time;
    String HEADER = "ENTRCAPI ";
    ArrayList<String> data;

    int debug;

    /**
     * Constructor for the object
     * @param version
     * @param debug
     */
    EntrcApi_Logger(String version, int debug){
        HEADER = HEADER + version +" |";
        this.debug = debug;
        actual_time = null;
        data = new ArrayList();
    }

    /**
     * Function for adding log to object
     * @param data
     */
    void add(String data){
        actual_time = new Date();
        String content = HEADER + actual_time.toString()+"| "+data;
        this.data.add(content);
        if ( debug == 1){
            System.out.println(content);
        }
    }


}
