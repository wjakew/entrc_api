package com.jakubwawak.maintanance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

    @RequestMapping("/admin")
    public ModelAndView admin(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        return modelAndView;
    }

    @RequestMapping("/worker")
    public ModelAndView worker(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("worker");
        return modelAndView;
    }

}