package com.jakubwawak.entrc_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Authorization_Handler {


    @GetMapping("/auth")
    public Authorization authorization(@RequestParam(value = "appcode",defaultValue = "blank") String appcode){
        return new Authorization(appcode);
    }
}
