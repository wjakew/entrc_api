package com.jakubwawak.entrc_api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorization {

    private int entrc_api_data;
    private String entrc_api_appcode;
    /**
     * Constructor
     * @param entrc_api_appcode
     */
    public Authorization(String entrc_api_appcode){
        this.entrc_api_appcode = entrc_api_appcode;
        authorize();
    }

    /**
     * Function for getting authorization data from database
     */
    private void authorize(){
        String query = "SELECT entrc_api_data FROM ENTRC_API_DATA WHERE entrc_api_appcode = ?;";

        try{
            EntrcApi.eal.add("Got job for auth token for "+entrc_api_appcode);
            PreparedStatement ppst = EntrcApi.database.con.prepareStatement(query);
            ppst.setString(1,entrc_api_appcode);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                EntrcApi.eal.add("Found auth token!");
                entrc_api_data = rs.getInt("entrc_api_data");
            }
            else{
                entrc_api_data = -1;
                EntrcApi.eal.add("No auth token for "+entrc_api_data);
            }

        }catch(SQLException e){
            EntrcApi.eal.add("Error retriving authorization token! ("+e.toString()+")");
        }

    }

    /**
     * Function for getting entrc_api_data
     * @return Integer
     */
    public int getEntrc_api_data() {
        return entrc_api_data;
    }

    /**
     * Function for getting entrc_api_appcode
     * @return String
     */
    public String getEntrc_api_appcode() {
        return entrc_api_appcode;
    }
}
