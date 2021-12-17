/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcguardadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class Guard_Handler {

    @GetMapping("/guard-auth/{numberplates}")
    public Guard_Event authorize(@PathVariable String numberplates) throws SQLException {
        Guard_Event gh = new Guard_Event(numberplates);
        gh.authorize();
        return gh;
    }
}
