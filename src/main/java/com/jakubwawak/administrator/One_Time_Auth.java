/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import com.jakubwawak.database.Database_APIController;
import com.jakubwawak.entrc_api.EntrcApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

/**
 * Object for checking One_Time_Auth codes
 */
@RestController
public class One_Time_Auth {

    @GetMapping("/entrc-onetime/{apptoken}/{one_time_code}")
    public One_Time_Entrc authorize_code(@PathVariable String apptoken,@PathVariable String one_time_code) throws SQLException {
        One_Time_Entrc ote = new One_Time_Entrc();
        EntrcApi.eal.add("REQUEST: Got data: apptoken("+apptoken+")");
        EntrcApi.eal.add("TYPE: ENTRC-ONETIME");
        Database_APIController dac = new Database_APIController(EntrcApi.database);
        if (dac.check_apicode(apptoken)){
            ote.flag = 1;
            ote.admin_code_data = one_time_code;
            if ( ote.validate() ){
                Random rand = new Random();
                ote.flag = rand.nextInt(100000/2)*2;
            }
            else{
                ote.flag = -99;
            }
        }
        else{
            ote.flag = -2;
        }
        return ote;
    }


}


class One_Time_Entrc{
    /**
     * CREATE TABLE ADMIN_CODES
     * (
     *     admin_codes_id INT PRIMARY KEY AUTO_INCREMENT,
     *     admin_id INT,
     *     admin_code_data VARCHAR(10),
     *     admin_code_time TIMESTAMP,
     *
     *     CONSTRAINT fk_admincodes FOREIGN KEY (admin_id) REFERENCES ADMIN_DATA(admin_id)
     * );
     */
    public int flag;
    public int admin_codes_id;
    public int admin_id;
    public String admin_code_data;
    public LocalDateTime admin_code_time;
    boolean exist;

    /**
     * Constructor
     * @throws SQLException
     */
    One_Time_Entrc() throws SQLException {
        flag = 0;
        admin_codes_id = -1;
        admin_id = -1;
        admin_code_data = "";
        admin_code_time = null;
        exist = false;
    }

    /**
     * Constructor with database support
     * @param rs
     * @throws SQLException
     */
    One_Time_Entrc(ResultSet rs) throws SQLException {
        flag = 0;
        admin_codes_id = rs.getInt("admin_codes_id");
        admin_id = rs.getInt("admin_id");
        admin_code_data = rs.getString("admin_code_data");
        admin_code_time = rs.getObject("admin_code_time",LocalDateTime.class);
        exist = true;
    }

    /**
     * Function for validating one-time code
     * @return
     */
    boolean validate() throws SQLException {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
        String query = "SELECT * FROM ADMIN_CODES WHERE admin_code_data = ?;";
        try{
            PreparedStatement ppst = EntrcApi.database.con.prepareStatement(query);
            ppst.setString(1,admin_code_data);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                if ( admin_code_data.equals(rs.getString("admin_code_data"))){
                    admin_id = rs.getInt("admin_id");
                    admin_code_time = rs.getObject("admin_code_time",LocalDateTime.class);
                    admin_codes_id = rs.getInt("admin_code_id");
                    if (admin_code_time.isBefore(now)){
                        EntrcApi.eal.add("One-Time code validated!");
                        remove_code();
                        return true;
                    };
                    EntrcApi.eal.add("One-Time code expired!");
                    return false;
                }
            }
            return false;
        } catch (SQLException e) {
            EntrcApi.database.log("Failed to validate admin_code ("+e.toString()+")");
            return false;
        }
    }

    /**
     * Function for removing used code
     */
    void remove_code(){
        String query = "DELETE FROM ADMIN_CODES WHERE admin_id = ?;";
        try{
            PreparedStatement ppst = EntrcApi.database.con.prepareStatement(query);
            ppst.setInt(1,admin_id);
            ppst.execute();
            EntrcApi.eal.add("Removed one-time code!");
        }catch(SQLException e){
            EntrcApi.eal.add("Failed to remove one-time code ("+e.toString()+")");
        }
    }
}
