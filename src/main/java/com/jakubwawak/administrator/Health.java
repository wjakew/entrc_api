/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.entrc_api.EntrcApi;

/**
 * Function for setting info data
 */
public class Health {

    public String program_version;
    public String database_version;
    public String status;
    public String startup_time;

    public Health(){

        program_version = EntrcApi.version;
        database_version = EntrcApi.database.version;
        status = Boolean.toString(EntrcApi.database.connected);
        startup_time = EntrcApi.database.run_time.toString();
    }
}
