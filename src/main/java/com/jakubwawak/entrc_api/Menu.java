/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrc_api;


import java.sql.SQLException;
import java.util.Scanner;

/**
 * Class for creating admin menu
 */
public class Menu {

    // flag for setting run
    boolean run;

    /**
     * Constructor
     */
    public Menu(){
        show_header();
        run = true;
    }

    /**
     * Function for running
     */
    public void run() throws SQLException {
        Scanner user_input = new Scanner(System.in);
        while(run){
            System.out.print("entrc_api"+EntrcApi.version+">");
            String user_ = user_input.nextLine();
            int index = 0;
            for(String word : user_.split(" ")){
                switch(word){
                    case "exit":
                        System.out.println("entrcAPI exiting...");
                        System.exit(0);
                        break;
                    case "info":
                        System.out.println("by Jakub Wawak 2021");
                        System.out.println(EntrcApi.version+" "+EntrcApi.build_number);
                        System.out.println(EntrcApi.database.database_stats());
                        break;
                    case "log":
                        try{
                            if ( user_.split(" ")[index+1].equals("off") ){
                                System.out.println("Log printing off");
                                EntrcApi.database.debug = 0;
                            }
                            else if (user_.split(" ")[index+1].equals("on")){
                                System.out.println("Log printing on");
                                EntrcApi.database.debug = 1;
                            }
                        }catch(Exception e){}
                        break;
                    case "help":
                        System.out.println("exit [exit]           - exits the api");
                        System.out.println("info [info]           - shows current app state");
                        System.out.println("log [log on, log off] - turn on and off logging on screen");
                }
                index++;
            }
        }
    }

    /**
     * Function for showing welcoming header
     */
    void show_header(){
        String header = " _ __ ___   ___ _ __  _   _ \n" +
                        "| '_ ` _ \\ / _ \\ '_ \\| | | |\n" +
                        "| | | | | |  __/ | | | |_| |\n" +
                        "|_| |_| |_|\\___|_| |_|\\__,_|\n";
        System.out.println(header);
    }
}
