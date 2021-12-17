/*
Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.timemanager;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 *Object that describes time
 * @author jakubwawak
 */
public class TimeManager_Object {

    public LocalDateTime raw_time_object;     // object for storing raw time data
    String source_info;                       // flag for checking

    DateTimeFormatter date_format;
    String raw_date;
    int day,month,year;                       // variables for storing data

    String raw_time;
    int hours,minutes,seconds,miliseconds;    // variables for storing hours

    public boolean error;

    /**
     * Main constructor for creating TimeManager_Object from LocalDateTime
     * @param time_object
     * null - time now
     */
    public TimeManager_Object(LocalDateTime time_object){
        if ( time_object == null ){
            source_info = "LocalDateTime";
            raw_time_object = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
            date_format = null;
            parse_object();
            error = false;
        }
        else{
            source_info = "LocalDateTime";
            raw_time_object = time_object;
            date_format = null;
            parse_object();
            error = false;
        }
    }

    /**
     * Constructor
     */
    public TimeManager_Object(){
        source_info = "LocalDateTime_Current";
        raw_time_object = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        date_format = null;
        parse_object();
        error = false;
    }

    /**
     * Constructor
     * @param _time_hour
     * @param _time_minutes
     */
    public TimeManager_Object(String _time_hour,String _time_minutes) {
        source_info = "hour_date";
        date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);
        try {
            String month_text = date.split(" ")[0].split("/")[1];
            String day_text = date.split(" ")[0].split("/")[2];
            year = Integer.parseInt(date.split(" ")[0].split("/")[0]);
            raw_time_object = LocalDateTime.parse(year + "-" + month_text + "-" + day_text + " " + _time_hour+":"+_time_minutes, date_format);
            if ( raw_time_object != null){
                parse_object();
            }
        }catch(Exception e){
            System.out.println(e.toString());
            raw_time_object = null;
            error = true;
        }
    }

    /**
     * Constructor for parsing simple date
     * Used in combobox
     * @param day
     * @param month
     * @param year
     */
    public TimeManager_Object(int day, int month,int year){
        source_info = "raw_date";
        date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try{
            if ( month <10){
                String month_text = "0" + Integer.toString(month);
                if ( day < 10){
                    String day_text = "0" + Integer.toString(day);
                    raw_time_object = LocalDateTime.parse(year+"-"+month_text+"-"+day_text+" "+"23:59",date_format);
                }
                else{
                    raw_time_object = LocalDateTime.parse(year+"-"+month_text+"-"+day+" "+"23:59",date_format);
                }
            }
            else{
                if ( day < 10){
                    String day_text = "0" + Integer.toString(day);
                    raw_time_object = LocalDateTime.parse(year+"-"+month+"-"+day_text+" "+"23:59",date_format);
                }
                else{
                    raw_time_object = LocalDateTime.parse(year+"-"+month+"-"+day+" "+"23:59",date_format);
                }
            }

            if ( raw_time_object != null){
                parse_object();
            }
        }catch(Exception e){
            System.out.println(e.toString());
            raw_time_object = null;
            error = true;
        }

    }

    /**
     * Constructor for parsing string data to LoacDateTimeobject
     * @param str
     */
    public TimeManager_Object(String str){
        source_info = "String";
        date_format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        raw_time_object = LocalDateTime.parse(str,date_format);

        if ( raw_time_object != null ){
            parse_object();
        }

    }

    /**
     * Function for parsing raw data from LocalDateTime object
     */
    void parse_object(){

        String raw = raw_time_object.toString();

        // eq: 2021-02-02T18:02:28.714353
        String[] elements = raw.split("T");

        raw_date = elements[0];
        raw_time = elements[1];

        // parsing date
        day = convert_time(raw_date.split("-")[2]);
        month = convert_time(raw_date.split("-")[1]);
        year = convert_time(raw_date.split("-")[0]);


        //parsing time
        hours = convert_time(raw_time.split(":")[0]);
        minutes = convert_time(raw_time.split(":")[1]);

        if ( source_info.equals("String")){
            seconds = 0;
            miliseconds = 0;
        }
        else{
            if ( raw_time.split(":").length == 3){
                String raw_seconds = raw_time.split(":")[2];
                seconds = convert_time(raw_seconds.substring(0, 2));
                if ( raw_seconds.length() < 3 ){
                    miliseconds = -1;
                }
                else{
                    miliseconds = convert_time(raw_seconds.substring(3));
                }
            }
            else{
                miliseconds = 0;
            }

        }
    }

    /**
     * Function for calculating hour difference
     * @param another_date
     * @return Long
     */
    long minutes_difference(TimeManager_Object another_date){
        Duration duration = Duration.between(raw_time_object,another_date.raw_time_object);
        return duration.toMinutes();
    }

    /**
     * Function for calculating day difference
     * @param number_of_days
     * @return LocalDateTime
     */
    public LocalDateTime day_difference(int number_of_days){
        return raw_time_object.minusDays(number_of_days);
    }

    /**
     * Function for calculating day difference
     * @param number_of_days
     * @return TimeManager_Object
     */
    public TimeManager_Object day_differance(int number_of_days){
        return new TimeManager_Object(raw_time_object.minusDays(number_of_days));
    }

    /**
     * Function for counting days between dates
     * @param compare_to
     * @return long
     */
    long count_days_to(TimeManager_Object compare_to){
        LocalDate date_obj = LocalDate.of(year, return_month_obj(), day);
        LocalDate compare_obj = LocalDate.of(compare_to.year, compare_to.return_month_obj(), compare_to.day);
        return ChronoUnit.DAYS.between(date_obj, compare_obj);
    }

    /**
     * Function for getting month
     * @return
     */
    Month return_month_obj(){
        return Month.of(this.month);
    }
    /**
     * Function for getting name of the month
     * @return String
     */
    String get_month_name(){
        DateFormatSymbols dfs = new DateFormatSymbols();
        return dfs.getMonths()[month-1];
    }

    /**
     * Function for getting date glance
     * @return String
     */
    public String get_date_glance(){
        if ( month < 10){
            if ( day < 10 ){
                return Integer.toString(year)+"-0"+Integer.toString(this.month)+"-0"+Integer.toString(this.day);
            }
            return Integer.toString(year)+"-0"+Integer.toString(this.month)+"-"+Integer.toString(this.day);
        }
        if ( day < 10)
            return Integer.toString(year)+"-"+Integer.toString(this.month)+"-0"+Integer.toString(this.day);

        return Integer.toString(year)+"-"+Integer.toString(this.month)+"-"+Integer.toString(this.day);
    }

    /**
     * Function for getting name of the day
     * @return String
     * @throws ParseException
     */
    String get_day_name() throws ParseException{
        // First convert to Date. This is one of the many ways.
        String dateString = String.format("%d-%d-%d", year, month, day);
        Date date = new SimpleDateFormat("yyyy-M-d").parse(dateString);

        // Then get the day of week from the Date based on specific locale.
        return new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
    }

    /**
     * Function for simpli showing data stored in the object
     * @return String
     */
    public String prepare_glance(){
        return raw_time_object.toString();
    }
    /**
     * Function for setting dates in time
     * @param date_object
     * @return Integer
     * return codes:
     *  1 - given date is after this object
     * -1 - given date is before this object
     *  0 - dates are equal
     * -2 - logic failure
     */
    int validate(TimeManager_Object date_object){
        if ( date_object.raw_time_object.isAfter(raw_time_object)){
            return 1;
        }
        else if ( date_object.raw_time_object.isBefore(raw_time_object)){
            return -1;
        }
        else if ( date_object.raw_time_object.isEqual(raw_time_object)){
            return 0;
        }
        else{
            return -2;
        }
    }


    /**
     * Function for converting String to integer
     * @param raw
     * @return Integer
     */
    int convert_time(String raw){
        try{
            return Integer.parseInt(raw);

        }catch(NumberFormatException e){
            System.out.println("Failed to convert data: "+raw+" ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for showing raw data object
     */
    public void show_object_data() throws ParseException{
        System.out.println("Source : "+source_info);
        System.out.println("raw data: "+raw_time_object.toString());
        System.out.println("            hours minutes seconds miliseconds");
        System.out.println("parsed time: "+hours+" "+minutes+" "+seconds+" "+miliseconds);
        System.out.println("            day month year");
        System.out.println("parsed date: "+day+" "+month+" "+year);
        System.out.println("Additional info: ");
        System.out.println("Day name: "+get_day_name());
        System.out.println("Month name: "+get_month_name());
        System.out.println("Date 30 days before: "+day_difference(30).toString());
    }


}
