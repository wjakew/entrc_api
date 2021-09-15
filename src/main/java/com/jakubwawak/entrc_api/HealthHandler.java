package com.jakubwawak.entrc_api;

import com.jakubwawak.administrator.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthHandler {

    @GetMapping("/health")
    public Health get_service_status(){
        return new Health();
    }
}
