/*
by Jakub Wawak 2021
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.database.Database_ProgramCodes;
import com.jakubwawak.entrc_api.EntrcApi;

import java.sql.SQLException;

/**
 * Object for maintaining function codes
 */
public class FunctionCodes {

    public String code1,code2,code3;
    public int flag;

    /**
     * Constructor
     */
    public FunctionCodes(){
        code1 = "";
        code2 = "";
        code3 = "";
        flag = 0;
    }

    /**
     * Function for loading current codes
     */
    public void load_current_codes() throws SQLException {
        Database_ProgramCodes dpc = new Database_ProgramCodes(EntrcApi.database);
        String raw_value = dpc.get_value("SHELF_CODES");
        String[] codes = raw_value.split(",");
        code1 = codes[0];
        code2 = codes[1];
    }

}
