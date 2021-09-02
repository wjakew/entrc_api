/*
Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.timemanager;
import java.time.LocalDateTime;

/**
 *Object for creating range between dates
 * @author jakubwawak
 */
public class TimeManager_DayPair {
    TimeManager_Object date_of_start;
    TimeManager_Object date_of_end;

    public long duration;

    boolean validation_flag;
    /**
     * Main constructor
     * @param date_of_start
     * @param date_of_end
     */
    public TimeManager_DayPair(TimeManager_Object date_of_start,TimeManager_Object date_of_end){

        this.date_of_start = date_of_start;
        this.date_of_end = date_of_end;
        validation_flag = validate_data();

        if (validation_flag){
            duration = count_minutes_difference();
        }
        else{
            duration = 0;
        }
    }

    /**
     * Constructor for using TimeTable functionality
     * @param date_of_start

     */
    public TimeManager_DayPair(LocalDateTime date_of_start){
        int day_start,month_start,year_start;

        day_start = date_of_start.getDayOfMonth();
        month_start = date_of_start.getMonthValue();
        year_start = date_of_start.getYear();

        this.date_of_start = new TimeManager_Object(day_start,month_start,year_start);
        this.date_of_end = new TimeManager_Object(day_start,month_start,year_start);
        validation_flag = validate_data();

        if (validation_flag){
            duration = count_minutes_difference();
        }
        else{
            duration = 0;
        }
    }

    /**
     * Function for validating given data
     * @return boolean
     */
    boolean validate_data(){
        return date_of_start.validate(date_of_end) == 1;
    }

    /**
     * Function for returing first object
     * @return TimeManager_Object
     */
    public TimeManager_Object get_start_object(){
        return date_of_start;
    }

    /**
     * Function for returning last object
     * @return TimeManager_Object
     */
    public TimeManager_Object get_end_object(){
        return date_of_end;
    }


    /**
     * Function for counting hour difference
     * @return Long
     */
    public long count_minutes_difference(){
        return date_of_start.minutes_difference(date_of_end);
    }

    /**
     * Function for counting hours from minutes
     * @return Long
     */
    public long count_hours(){
        return duration/60;
    }

    /**
     * Function for preparing glances for raport making
     * @return String
     */
    public String prepare_glance(){
        return date_of_start.raw_time_object.toString() +" - " + date_of_end.raw_time_object.toString() + "["+duration+"]";
    }

    /**
     * Function for checking if object is between DayPair
     * @param tmo
     * @return boolean
     */
    public boolean between(TimeManager_Object tmo){
        if ( date_of_start.raw_time_object.isBefore(tmo.raw_time_object)){
            if ( date_of_end.raw_time_object.isAfter(tmo.raw_time_object)){
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Function for showing data
     */
    public void show_data(){
        System.out.println("Date of start: "+date_of_start.raw_time_object.toString());
        System.out.println("Date of end: "+date_of_end.raw_time_object.toString());
        System.out.println("Validation: "+validation_flag);
        if (validation_flag){
            System.out.println("Duration: "+duration);
        }
    }
}
