package com.jakubwawak.entrc_api;

import java.util.Date;
import java.util.ArrayList;

public class EntrcApi_Logger {

    Date start_time;
    String header = " -> ENTRCAPI ";
    ArrayList<String> data;

    int debug = 0;

    /**
     * Constructor
     * @param version
     */
    public EntrcApi_Logger(String version){
        start_time = new Date();
        header = header + version;
        header = header + " |";
        data = new ArrayList<>();
    }

    /**
     * Function for adding data to log
     * @param entrc_api_code
     * @param data
     */
    public void add(String entrc_api_code,String data){
        String content = header + "("+entrc_api_code+") "+data;
        this.data.add(content);
        if ( debug == 1){
            System.out.println(content);
        }
    }





}
