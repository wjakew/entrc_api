/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrcguardadmin;

import com.jakubwawak.database.Database_Admin;
import com.jakubwawak.database.Database_Worker;
import com.jakubwawak.entrc_api.EntrcApi;
import com.jakubwawak.entrcguardadmin.Database_Guard_Timesheet;
import com.jakubwawak.entrcguardadmin.Database_Numberplate;
import com.jakubwawak.timemanager.TimeManager_DayPair;
import com.jakubwawak.timemanager.TimeManager_Object;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Guard_Event {

    public String numberplates;

    public TimeManager_Object current_time;
    public String owner;
    public int owner_id;
    /**
     * return codes for entrance:
     * -2 - authorization not executed
     * -1 - user blocked / not active
     *  0 - numberplates not found
     *  1 - time schedule not found
     *  2 - current time not in schedule (entrance not granted)
     *  3 - entrance granted
     */
    public int  entrance;

    /**
     * Constructor
     * @param numberplates
     */
    public Guard_Event(String numberplates) {
        this.numberplates = numberplates;
        current_time = new TimeManager_Object();
        entrance = -2;
    }

    /**
     * Function for entrance authorization
     * @throws SQLException
     */
    public void authorize() throws SQLException {
        TimeManager_Object current_time = new TimeManager_Object();
        EntrcApi.eal.add("RUNNING AUTHORIZATION FOR REQUEST! NUMBERPLATES("+numberplates+")");
        EntrcApi.eal.add("TIME: "+current_time.prepare_glance());
        /**
         * Check if numberplates exists
         * Get owner
         * Check if owner is active/not blocked
         * Get start hours
         * Get end hours
         * Check if day is correct (day is avaialbe to entrance
         * set entrnce to code
         */
        Database_Numberplate dn = new Database_Numberplate(EntrcApi.database);
        int entrc_guard_numberplates = dn.check_numberplate(numberplates);
        if (  entrc_guard_numberplates > 0 ){
            // numberplates exists
            ArrayList<Integer> user_data = dn.get_owner_data(entrc_guard_numberplates);
            // checking if user is block on the app
            switch(user_data.get(0)){
                case 0:
                    //admin
                    Database_Admin da = new Database_Admin(EntrcApi.database);
                    if ( da.get_activity_status(user_data.get(1)) != 1){
                        EntrcApi.eal.add("ADMIN OWNER FOR NUMBERPLATES "+numberplates+" IS BLOCKED ON DATABASE");
                        entrance = -1;
                    }
                    break;
                case 1:
                    //worker
                    Database_Worker dw = new Database_Worker(EntrcApi.database);
                    if ( dw.check_worker_graveyard(user_data.get(1)) != null){
                        EntrcApi.eal.add("WORKER OWNER FOR NUMBERPLATES "+numberplates+" IS UNACTIVE ON DATABASE");
                        entrance = -1;
                    }
                    break;
            }
            Database_Guard_Timesheet dgt = new Database_Guard_Timesheet(EntrcApi.database);
            int entrc_guard_timesheet_id = dn.get_timesheet(entrc_guard_numberplates);

            if ( entrance != -1){
                if ( entrc_guard_numberplates > 0 ){
                    // getting schedule for numberplates
                    String start_time = dgt.get_start_time(entrc_guard_timesheet_id);
                    String end_time = dgt.get_end_time(entrc_guard_timesheet_id);
                    TimeManager_Object entrance_on = new TimeManager_Object(start_time.split(":")[0],start_time.split(":")[1]);
                    TimeManager_Object entrance_off = new TimeManager_Object(end_time.split(":")[0],end_time.split(":")[1]);
                    EntrcApi.eal.add("LOADED SCHEDULE FOR REQUEST ("+numberplates+"): "+entrance_on.prepare_glance()+"-"+entrance_off.prepare_glance());
                    String daycodes = dgt.get_day_pattern(entrc_guard_timesheet_id);
                    if ( daycodes.charAt(get_current_day_number()-1) != '1'){
                        EntrcApi.eal.add("ENTRANCE NOT GRANTED! WRONG DAY");
                        entrance = 2;
                    }
                    else{
                        TimeManager_DayPair tdp = new TimeManager_DayPair(entrance_on,entrance_off);
                        if ( tdp.between(current_time)){
                            EntrcApi.eal.add("ENTRANCE GRANTED!");
                            entrance = 3;
                        }
                        else{
                            EntrcApi.eal.add("ENTRANCE NOT GRANTED! WRONG TIME");
                            entrance = 2;
                        }
                    }
                }
                else {
                    EntrcApi.eal.add("ENTRANCE NOT GRANTED! NOT IS SCHEDULE");
                    entrance = 1;
                }
            }

        }
        else{
            // numberplates not found
            EntrcApi.eal.add("REQUESTED NUMBERPLATES NOT FOUND");
            entrance = 0;
        }
    }

    /**
     * Function for getting current day number
     * @return Integer
     */
    int get_current_day_number(){
        Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        System.out.println(simpleDateformat.format(now));
        simpleDateformat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
        System.out.println(simpleDateformat.format(now));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
