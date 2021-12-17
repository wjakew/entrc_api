package com.jakubwawak.maintanance;

/**
 * Object for creating class
 */
public class Menu {

    /**
     * Constructor
     */
    public Menu(){
        load_header();
    }


    /**
     * Function for loading header
     */
    public void load_header(){
        String header = "            _                       _\n" +
                "  ___ _ __ | |_ _ __ ___ __ _ _ __ (_)      _ __ ___   ___ _ __  _   _\n" +
                " / _ \\ '_ \\| __| '__/ __/ _` | '_ \\| |_____| '_ ` _ \\ / _ \\ '_ \\| | | |\n" +
                "|  __/ | | | |_| | | (_| (_| | |_) | |_____| | | | | |  __/ | | | |_| |\n" +
                " \\___|_| |_|\\__|_|  \\___\\__,_| .__/|_|     |_| |_| |_|\\___|_| |_|\\__,_|\n" +
                "                             |_|";
        System.out.println(header);
    }
}
