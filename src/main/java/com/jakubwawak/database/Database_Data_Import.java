/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.administrator.Data_Worker_Connector;
import com.jakubwawak.database.Database_Connector;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *Object for importing data to database
 * @author kubaw
 */
public class Database_Data_Import {
  
    Database_Connector database;
      
    Database_Data_Import(Database_Connector database){
        this.database = database;
    }
    
    /**
     * Function for importing worker data
     * @param dwc
     * @throws SQLException 
     */
    void import_workers(Data_Worker_Connector dwc) throws SQLException{
        database.log("Importing workers from Data_Worker_Connector");
        Database_Worker dw = new Database_Worker(database);
        String query = "INSERT INTO WORKER\n" +
                        "(worker_login,worker_name,worker_surname,worker_pin,worker_position)\n" +
                        "VALUES\n" +
                        "(?,?,?,?,?);";
        if ( validate(dwc) ){
            try{
                for(ArrayList<String> data : dwc.lines){
                    // looping on data
                    if ( database.get_worker_id_bypin(data.get(3)) != 1){
                        // checking if pin already on database
                        PreparedStatement ppst = database.con.prepareStatement(query);
                        ppst.setString(1,dw.login_generator(data.get(0), data.get(1)));
                        ppst.setString(2,data.get(0));
                        ppst.setString(3,data.get(1));
                        ppst.setString(4,data.get(3));
                        ppst.setString(5,data.get(2));
                        ppst.execute();
                    }
                }
            }catch(SQLException e){
                database.log("Failed to import workers ("+e.toString()+")");
            }
        }
    }
    /**
     * Function for data validation
     * @param dwc
     * @return 
     */
    boolean validate(Data_Worker_Connector dwc){
        return dwc.lines.size() > 0;
    }
}
