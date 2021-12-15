package com.jakubwawak.maintanance;

import com.jakubwawak.database.Database_APIController;
import com.jakubwawak.entrc_api.EntrcApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class FunctionCodes_Handler {

    @GetMapping("/entrc-function/{api_token}")
    public FunctionCodes get_codes(@PathVariable String api_token) throws SQLException {
        FunctionCodes codes = new FunctionCodes();
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        if (dac.check_apicode(api_token)){
            codes.load_current_codes();
            codes.flag = 1;
        }
        else{
            codes.flag = -1;
        }
        return codes;
    }
}
