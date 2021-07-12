package com.jakubwawak.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database_APIController {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_APIController(Database_Connector database){

        this.database = database;
    }

    /**
     * Function for logging data to database
     * @param data_to_log
     */
    public void log(String data_to_log) throws SQLException {
        database.log_event(data_to_log,"ENTRC_API");
    }

    /**
     * Function for getting worker id by given login and PIN
     * @param login
     * @param pin
     * @return int
     * return codes:
     * any - worker_id
     * 0 - worker not found
     * -1 database error
     */
    public int worker_login(String login,String pin) throws SQLException {
        String query = "SELECT worker_id FROM WORKER WHERE worker_login = ? and worker_pin = ?;";

        try{
            PreparedStatement ppst = database.con.prepareStatement(query);

            ppst.setString(1,login);
            ppst.setString(2,pin);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("worker_id");
            }
            return 0;

        }catch(SQLException e){
            database.log("Failed to get worker_id by login and pin ("+e.toString());
            return -1;
        }
    }
}
