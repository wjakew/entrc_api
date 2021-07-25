package com.jakubwawak.entrc_api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebController {

    @RequestMapping("/admin")
    @ResponseBody
    public String admin() {
        return "Welcome on the admin page!";
    }

    @RequestMapping("/worker")
    @ResponseBody
    public String worker(){return "Welcome on the worker page!";}

}