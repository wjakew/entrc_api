/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *Objet for managing files with worker data
 * @author kubaw
 */
public final class Data_Worker_Connector {
    /**
     * File pattern:
     * 
     * no initzlized                - read_code = -1
     * no pattern                   - read_code =  0
     * name,surname,position,pin    - read_code =  1
     * 
     * Reading content from file    - mode 1
     * Loading content to file      - mode 0
     */
    
    int read_code;          // flag for file identity 
    int mode;               // mode of the program
    String file_path;       // path to the file
    
    // variables
    File file;                          // for storing file data
    BufferedReader buffer;              // buffer for reading
    FileWriter f_writter;               // buffer for writting
    boolean exists;                     // flag for checking if file is new or not
    public ArrayList<ArrayList<String>> lines; // collection for storing parsed lines
    int given_lines;                    // amount of read lines
    
    // Constuctor
    public Data_Worker_Connector(String path, int mode) throws IOException{
        file_path = path;
        this.mode = mode;
        
        file = null;
        buffer = null;
        lines = new ArrayList<>();
        given_lines = 0;
        read_code = -1;
        load_file();
        
        if (mode == 1){
            read_file();
        }
    }
    
    /**
     * Writting to the file
     * @param lines
     * @throws IOException 
     */
    public void write_file(ArrayList<String> lines) throws IOException{
        f_writter = new FileWriter(file_path);
        for(String line : lines){
            if(validate(line)){
                f_writter.write(line+"\n");
            }
        }
        f_writter.close();
        read_file();
    }
    
    /**
     * Function for reading files
     * @throws FileNotFoundException
     * @throws IOException 
     */
    void read_file() throws FileNotFoundException, IOException{
        buffer = new BufferedReader(new FileReader(file));
        
        String line;
        while ((line = buffer.readLine()) != null){
            given_lines++;
            if ( validate(line)){
                lines.add(load_data(line));
            }
        }
        if (lines.size() > 0 ){
            read_code = 1;
        }
        else{
            read_code = 0;
        }
    }
    
    /**
     * Function for parasing line of data
     * @param line
     * @return ArrayList
     */
    ArrayList<String> load_data(String line){
        return new ArrayList<>(Arrays.asList(line.split(",")));
    }
    
    /**
     * Function for validating lines of file
     * @param line
     * @return boolean
     */
    boolean validate(String line){
        int iterate = 0;
        char[] signs = line.toCharArray();
        for(int i = 0; i<line.length();i++){
            if (signs[i] == ','){
                iterate++;
            }
        }
        return iterate >= 3;
    }
    
    /**
     * Function for loading file from given path
     */
    void load_file(){
         file = new File(file_path);
         exists = file.exists();
    }
    
    
}
