/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.administrator.Configuration;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;
/**
 * Database object connector
 * @author jakubwawak
 */
public class Database_Connector {

    // version of database
    public final String version = "v0.1.2";
    public LocalDateTime run_time;
    // header for logging data
    // connection object for maintain connection to the database
    public Connection con;

    // variable for debug purposes
    public  int debug = 1;

    public boolean connected;                      // flag for checking connection to the database
    public String ip;                              // ip data for the connector
    public String database_name;                   // name of the database
    public String database_user;
    String database_password; // user data for cred
    public ArrayList<String> database_log;         // collection for storing data
    private ArrayList<String> database_log_copy;

    public Configuration configuration;            // vield for storing configuration data

    public int admin_id;                           // id currently logged admin
    public int access_level;                       // number of access level

    /**
     * Constructor
     */
    public Database_Connector() throws SQLException{
        con = null;
        database_log = new ArrayList<>();
        database_log_copy = new ArrayList<>();
        connected = false;
        ip = "";
        database_name = "";
        database_user = "";
        database_password = "";
        admin_id = -3;
        configuration = null;
        run_time = null;
        //log("Started! Database Connector initzialazed");
    }

    /**
     * Function for logging admin events
     * @param log_data
     * @param event_code
     * @return int
     * @throws SQLException
     * return codes:
     * 1  - inserted record
     *-1  - failed (error encountered)
     * EVENTS:
     * ADMIN_CREATED +
     * ADMIN_ELEVATED  - ( given privilages ) +
     * ADMIN_DELETED +
     * ADMIN_EDITED  +
     * ADMIN_CHANGED_PASS +
     * WORKER_DELETED
     * WORKER_ADDED +
     * WORKER_DATA_CHANGED +
     * CHECKED_LOG +
     * GENERATED_RAPORT
     * MESSAGE_DELETED +
     * ADMIN_INFO +
     */
    public int log_event(String log_data,String event_code) throws SQLException{
        LocalDateTime log_time = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
        String query =  "INSERT INTO DATA_LOG\n" +
                "(admin_id,data_log_date,data_log_action,data_log_desc)\n" +
                "VALUES\n" +
                "(?,?,?,?);";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setInt(1,admin_id);
            ppst.setObject(2,log_time);
            ppst.setString(3, event_code);
            ppst.setString(4,log_data);


            ppst.execute();
            return 1;
        }catch(SQLException e){
            log("Failed to log admin event. ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting messages to worker
     * @param worker_id
     * @param message
     * @return boolean
     * @throws SQLException
     */
    public boolean set_message_to_worker(int worker_id,String message) throws SQLException{

        String query =  "INSERT INTO USER_MESSAGE (admin_id,worker_id,user_message_content,user_message_seen)\n" +
                "VALUES\n" +
                "(?,?,?,?);";

        if ( worker_id == 0){
            // we need to send message to everyone
            Database_Worker dw = new Database_Worker(this);
            ArrayList<Integer> ids_list = dw.get_all_worker_ids();

            try{
                PreparedStatement ppst = con.prepareStatement(query);

                ppst.setInt(1,admin_id);
                ppst.setString(3,message);
                ppst.setInt(4,0);

                for(Integer id : ids_list){
                    ppst.setInt(2,id);
                    ppst.execute();
                }
                return true;
            }catch(SQLException e){
                log("Failed to set messsage for all workers ("+e.toString()+")");
                return false;
            }

        }
        else{
            try{
                log("Sending message to worker(id:"+worker_id+")");
                PreparedStatement ppst = con.prepareStatement(query);

                ppst.setInt(1, admin_id);
                ppst.setInt(2,worker_id);
                ppst.setString(3, message);
                ppst.setInt(4,0);

                ppst.execute();
                return true;
            }catch(SQLException e){
                log("Failed to set message to worker ("+e.toString()+")");
                return false;
            }
        }
    }

    /**
     * Function for getting raw query data
     * @param query
     * @return ResultSet
     * @throws SQLException
     */
    public ResultSet get_raw_query(String query) throws SQLException{
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            return ppst.executeQuery();

        }catch(SQLException e){
            log("Failed to get raw query: "+query+" ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for loading faceAPI log data
     * @return
     */
    public ArrayList<String> load_faceAPI_log() throws SQLException{
        ArrayList<String> data = new ArrayList<>();
        /**
         *  rec_api_data_id int AI PK
         rec_api_data_filename varchar(200)
         rec_api_data_time timestamp
         rec_api_data_desc varchar(100)
         rec_api_data_code
         */
        String query = "SELECT rec_api_data_code, rec_api_data_desc, rec_api_data_filename FROM REC_API_DATA ORDER BY id DESC\n" +
                "LIMIT 50;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            while(rs.next()){
                data.add(rs.getString("rec_api_data_code")+": ["+rs.getString("rec_api_data_filename")+"] "+rs.getString("rec_api_data_desc"));
            }
        }catch(SQLException e){
            log("Failed to load faceAPI log ("+e.toString()+")");
        }
        if (data.size() == 0 ){
            data.add("Pusto");
        }
        return data;
    }

    /**
     * Function for setting message unread again
     * @param message_id
     * @return boolean
     * @throws SQLException
     */
    public boolean update_message_to_worker(int message_id) throws SQLException{
        String query = "UPDATE USER_MESSAGE SET user_message_seen = 0 WHERE user_message_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,message_id);

            ppst.execute();
            return true;
        }catch(SQLException e){
            log("Failed to update message worker: "+e.toString()+")");
            return false;
        }
    }

    /**
     * Function for deleting messages
     * @param message_id
     * @return boolean
     * @throws SQLException
     */
    public boolean delete_message(int message_id) throws SQLException{
        String query = "DELETE FROM USER_MESSAGE where user_message_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,message_id);

            ppst.execute();
            return true;
        }catch(SQLException e){
            log("Failed to delete message ("+e.toString()+")");
            return false;
        }
    }

    /**
     * Function for getting database stats
     * @return String
     * @throws SQLException
     */
    public String database_stats() throws SQLException{
        String data = "Database Connector "+version +"\n";
        data = data + "Logged as: "+get_admin_login(admin_id) +"\n";
        data = data + "Level of security: "+access_level+"\n";
        data = data + "Number of records in database: \n";
        return data;
    }
    /**
     * Function for getting MAC addreses of the local computer
     * @return
     * @throws UnknownHostException
     * @throws SocketException
     */
    String get_local_MACAddress() throws UnknownHostException, SocketException{
        if( System.getProperty("os.name").equals("Mac OS X")){
            return "macos - not supported";
        }
        else{
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            return String.join("-", hexadecimal);
        }

    }

    /**
     * Function for setting configuration data to variable
     * @param config
     */
    public void set_configuration(Configuration config){
        configuration = config;
    }

    /**
     * Function for getting admin login
     * @param admin_id
     * @return String
     */
    public String get_admin_login(int admin_id) throws SQLException{
        String query = "SELECT admin_login FROM ADMIN_DATA where admin_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,admin_id);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("admin_login");
            }
            return null;

        }catch(SQLException e){
            log("Failed to get admin login ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting admin email data
     * @return String
     * @throws SQLException
     */
    public String get_admin_email() throws SQLException{
        String query = "SELECT admin_email FROM ADMIN_DATA WHERE admin_id=?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setInt(1, admin_id);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return rs.getString("admin_email");
            }
            return null;
        }catch(SQLException e){
            log("Failed to get admin email ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting user data ( name and surname )
     * @param worker_id
     * @return String
     * @throws SQLException
     */
    public String get_worker_nameusername(int worker_id) throws SQLException{
        String query = "SELECT worker_name,worker_surname FROM WORKER where worker_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,worker_id);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                if( worker_id == 1){
                    return "brak";
                }
                return rs.getString("worker_name")+" "+rs.getString("worker_surname");
            }
            return null;

        }catch(SQLException e){
            log("Failed to get worker name and surname ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting pin by given id
     * @param worker_id
     * @return
     * @throws SQLException
     */
    public String get_pin_byworkerid(int worker_id) throws SQLException{
        String query = "SELECT worker_pin FROM WORKER where worker_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,worker_id);
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("worker_pin");
            }
            return null;
        }catch(SQLException e){
            log("Failed to get worker pin by id ("+e.toString()+")");
            return null;
        }
    }
    /**
     * Function for getting worker id by pin
     * @param pin
     * @return Integer
     * @throws SQLException
     * return codes:
     * any - worker id
     * 0 - no worker with given id
     * -1 - database connector failed
     */
    public int get_worker_id_bypin(String pin) throws SQLException{
        String query = "SELECT worker_id FROM WORKER WHERE worker_pin=?";

        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setString(1,pin);
        try{
            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("worker_id");
            }
            return 0;
        }catch(SQLException e){
            log("Failed to get worker_id ("+e.toString());
            return -1;
        }
    }

    /**
     * Function for getting worker data by id
     * @param id
     * @return String
     * @throws SQLException
     */
    public String get_worker_data(int id) throws SQLException{
        String query = "SELECT worker_name,worker_surname FROM WORKER where worker_id=?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setInt(1,id);

        try{

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("worker_name") + " " + rs.getString("worker_surname");
            }
            return null;

        }catch(SQLException e){
            log("Failed to get worker data ("+e.toString());
            return null;
        }
    }

    /**
     * Function for getting worker name and surname by pin
     * @param pin
     * @return String
     * @throws SQLException
     * Returns null if worker don't exist
     * NOTE: less than probable, func used only after get_worker_id_bypin(String pin)
     */
    public String get_worker_data(String pin) throws SQLException{
        String query = "SELECT worker_name,worker_surname FROM WORKER where worker_pin=?;";
        PreparedStatement ppst = con.prepareStatement(query);
        ppst.setString(1,pin);

        try{

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getString("worker_name") + " " + rs.getString("worker_surname");
            }
            return null;

        }catch(SQLException e){
            log("Failed to get worker data ("+e.toString());
            return null;
        }
    }

    /**
     * Function for gathering database log
     * @param log
     */
    public void log(String log) throws SQLException{
        java.util.Date actual_date = new java.util.Date();
        database_log.add("("+actual_date.toString()+")"+" - "+log);
        database_log_copy.add("("+actual_date.toString()+")"+" - "+log);
        // load log to database
        if ( debug == 1){
            String query = "INSERT INTO PROGRAM_LOG (program_log_desc) VALUES (?); ";
            System.out.println("ENTRC LOG: "+database_log.get(database_log.size()-1));
            if ( con == null){
                System.out.println("Bład bazy: con=null ("+log+")");
            }
            else{
                PreparedStatement ppst = con.prepareStatement(query);

                try{

                    ppst.setString(1,log);

                    ppst.execute();

                }catch(SQLException e){}
            }

            // after 100 records dump to file
            if(database_log.size() > 100){
                database_log.clear();
            }
        }
    }

    /**
     * Function for printing info data on the screen
     * @param data
     */
    public void info_print(String data){
        if ( debug == 1 ){
            System.out.println(data);
        }
    }
    /**
     * Function for connecting to the database
     * @param ip
     * @param database_name
     * @param user
     * @param password
     * @throws SQLException
     */
    public void connect(String ip,String database_name,String user,String password) throws SQLException, ClassNotFoundException{
        this.ip = ip;
        this.database_name = database_name;
        database_user = user;
        database_password = password;

        String login_data = "jdbc:mysql://"+this.ip+"/"+database_name+"?"
                + "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&" +
                "user="+database_user+"&password="+database_password;
        try{
            con = DriverManager.getConnection(login_data);
            connected = true;
            run_time = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
            log("Connected succesfully");
            log(login_data.substring(0,login_data.length()-25)+"...*END*");
        }catch(SQLException e){
            connected = false;
            log("Failed to connect to database ("+e.toString()+")");
        }
        log("Database string: "+login_data.substring(0,login_data.length()-25)+"...*END*");
    }

    /**
     * Function for reconnecting to the database
     */
    public void reconnect() throws SQLException{
        log("Reconnecting with database...");
        String login_data = "jdbc:mysql://"+this.ip+"/"+database_name+"?"
                + "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&" +
                "user="+database_user+"&password="+database_password;
        try{
            con = DriverManager.getConnection(login_data);
            connected = true;
            run_time = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );
            log("Connected succesfully");
            log(login_data.substring(0,login_data.length()-25)+"...*END*");
        }catch(SQLException e){
            connected = false;
            log("Failed to connect to database ("+e.toString()+")");
        }
        log("Database string: "+login_data.substring(0,login_data.length()-25)+"...*END*");
    }

    /**
     * Function for checking database version
     * @param required_version
     * @return boolean
     * @throws SQLException
     */
    public boolean check_databaseversion(String required_version) throws SQLException{
        Database_ProgramCodes dpc = new Database_ProgramCodes(this);
        log("Actual version of database: "+dpc.get_value("DATABASEVERSION")+" required: "+required_version);
        String version = dpc.get_value("DATABASEVERSION");
        try{
            int ver = Integer.parseInt(version);
            int got = Integer.parseInt(required_version);

            return got <= ver;
        }catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Function for encrypting password
     */
    public String code_password(String password) throws NoSuchAlgorithmException, SQLException{
        // function in development
        Password_Validator pv = new Password_Validator(password);
        String password_hash = pv.hash();
        //c3284d0f94606de1fd2af172aba15bf3
        log("Hashed input: "+password_hash);
        return password_hash;
    }

    /**
     * Function for checking availability for admin logins
     * @param admin_login
     * @return int
     * @throws SQLException
     * return codes:
     * 1 - login available
     * 0 - login uavaialbe
     * -1 - database error
     */
    public int check_ava_adminlogin(String admin_login) throws SQLException{

        String query = "SELECT * from ADMIN_DATA where admin_login = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,admin_login);

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return 1;
            }
            return 0;

        }catch(SQLException e){
            log("Failed to check login checking availability ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for creating random passwords
     * @return String
     */
    public String create_randomPassword(){
        // ASCII range - alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of loop choose a character randomly from the given ASCII range
        // and append it to StringBuilder instance

        for (int i = 0; i < 12; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }

    /**
     * Function for creating admin accounts
     * @param admin_login
     * @return String
     * Function returns generated password, null if database error
     */
    public String create_admin(String admin_login,String admin_email,int admin_level) throws SQLException, NoSuchAlgorithmException{
        String query = "INSERT INTO ADMIN_DATA\n" +
                "(admin_login,admin_password,admin_email,admin_level,admin_active)\n" +
                "VALUES\n" +
                "(?,?,?,?,1);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,admin_login);
            String password = create_randomPassword();
            Password_Validator pv = new Password_Validator(password);
            ppst.setString(2,pv.hash());
            ppst.setString(3,admin_email);
            ppst.setInt(4,admin_level);
            log_event("Created new admin login("+admin_login+") by admin(id"+admin_id+")","ADMIN_CREATED");
            ppst.execute();
            log_event("Admin account login("+admin_login+") elevated to: "+admin_level,"ADMIN_ELEVATED");
            return password;
        }catch(SQLException e){
            log("Failed to create admin ("+e.toString()+")");
            return null;
        }
    }
    /**
     * Function for loading admin PIN data for managing client window
     * @param data
     * @return boolean
     */
    public boolean load_admin_PIN_data(ArrayList<String> data) throws SQLException{
        String query = "INSERT INTO CONFIGURATION\n" +
                "(entrc_user_exit_pin,entrc_user_ask_pin,entrc_user_manage_pin,entrc_admin_manage_pin)\n" +
                "VALUES\n" +
                "(?,?,?,?);";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,data.get(0));
            ppst.setString(2,data.get(1));
            ppst.setString(3,data.get(2));
            ppst.setString(4,data.get(3));

            ppst.execute();
            return true;

        }catch(SQLException e){
            log("Failed to update PIN on database ("+e.toString()+")");
            return false;
        }
    }

    /**
     * Function for getting PIN data for managing client window
     * @return ArrayList
     * @throws SQLException
     */
    public ArrayList<String> get_admin_PIN_data() throws SQLException{
        ArrayList<String> data_to_get = new ArrayList<>();

        String query = "SELECT * from CONFIGURATION;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                data_to_get.add(rs.getString("entrc_user_exit_pin"));
                data_to_get.add(rs.getString("entrc_user_ask_pin"));
                data_to_get.add(rs.getString("entrc_user_manage_pin"));
                data_to_get.add(rs.getString("entrc_admin_manage_pin"));
                return data_to_get;
            }
            return null;

        }catch(SQLException e){
            log("Failed to get admin PIN data ("+e.toString());
            return null;
        }
    }
    /**
     * Function for updating data on admin objects
     * @param admin_id
     * @param admin_login
     * @param admin_email
     * @param admin_level
     * @return boolean
     */
    public boolean update_admin(int admin_id,String admin_login,String admin_email,int admin_level) throws SQLException{
        String query =  "UPDATE ADMIN_DATA SET \n" +
                "admin_login = ?, admin_email = ?, admin_level = ?\n" +
                "where admin_id = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1,admin_login);
            ppst.setString(2,admin_email);
            ppst.setInt(3,admin_level);
            ppst.setInt(4,admin_id);

            ppst.execute();
            return true;
        }catch(SQLException e){
            log("Failed to update admin data. ("+e.toString()+")");
            return false;
        }
    }
    /**
     * Function for getting database version
     * @return String
     */
    public String get_database_version() throws SQLException{
        String query = "SELECT programcodes_value FROM PROGRAMCODES where programcodes_key = 'databaseversion';";
        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ResultSet rs = ppst.executeQuery();

            if(rs.next()){
                return rs.getString("programcodes_value");
            }
            return "brak";
        }catch(SQLException e){
            log("Failed to load database version ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting admin data in simple collection
     * @param admin_id
     * @return ArrayList
     * Returns collection with:
     * [admin_id,admin_login,admin_email,admin_level]
     */
    public ArrayList<String> get_admin(int admin_id) throws SQLException{
        String query = "SELECT * FROM ADMIN_DATA WHERE admin_id = ?;";
        ArrayList<String> data_toRet = new ArrayList<>();

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setInt(1,admin_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                data_toRet.add(Integer.toString(rs.getInt("admin_id")));
                data_toRet.add(rs.getString("admin_login"));
                data_toRet.add(rs.getString("admin_email"));
                data_toRet.add(Integer.toString(rs.getInt("admin_level")));
                return data_toRet;
            }
            return null;
        }catch(SQLException e){
            log("Failed to get admin data ("+e.toString()+")");
            return null;
        }
    }

    /**
     * Function for getting access level from database
     * @param admin_id
     * @return Integer
     * @throws SQLException
     * return codes:
     * any - level of access
     * -1 - can't find admin with that level of access
     * -2 - database faliure
     */
    public int prepare_access_level(int admin_id) throws SQLException{
        String query = "SELECT admin_level from ADMIN_DATA where admin_id = ?;";
        PreparedStatement ppst = con.prepareStatement(query);

        try{
            ppst.setInt(1, admin_id);

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                return rs.getInt("admin_level");
            }
            else{
                return -1;
            }

        }catch(SQLException e){
            log("Failed to get access level (id:"+admin_id+") ("+e.toString()+")");
            return -2;
        }

    }

    /**
     * Function for login admin
     * @param admin_login
     * @param admin_password
     * @return Integer
     * return codes:
     * any - administrator id
     * -1 - failed to find admin with given admin_login
     * -2 - database faliure
     * -3 - admin deactivated
     */
    public int login_admin(String admin_login,String admin_password) throws SQLException, UnknownHostException, SocketException, NoSuchAlgorithmException{

        String query = "SELECT * FROM ADMIN_DATA where admin_login = ? and admin_password = ?;";

        if (con == null){
            System.out.println("Failed to connect to the database");
            return -2;
        }
        else{
            try{
                PreparedStatement ppst = con.prepareStatement(query);
                ppst.setString(1,admin_login);

                Password_Validator pv = new Password_Validator(admin_password);
                ppst.setString(2,pv.hash());

                ResultSet rs = ppst.executeQuery();
                log("Loggin query: "+ppst.toString());
                if ( rs.next() ){
                    if ( rs.getInt("admin_active") == 0){
                        log("Trying to log on unactive admin account");
                        return -3;
                    }
                    admin_id = rs.getInt("admin_id");
                    access_level = rs.getInt("admin_level");
                    log("Succesfully logged admin");
                    return rs.getInt("admin_id");
                }
                log("Passwords ("+admin_password+") doesn't match");
                return -1;
            }catch(SQLException e){
                log("Failed to log admin with ("+e.toString()+")");
                return -2;
            }
        }
    }



    /**
     * Function for checking password
     * @param password
     * @return Integer
     * @throws SQLException
     * return codes:
     *  1 - password correct
     *  0 - password incorrect
     * -1 - database error
     */
    public int check_password(String password) throws SQLException, NoSuchAlgorithmException{
        String query = "SELECT * FROM ADMIN_DATA where admin_id = ? and admin_password = ?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setInt(1,admin_id);

            ppst.setString(2,code_password(password));

            ResultSet rs = ppst.executeQuery();

            if (rs.next()){
                return 1;
            }
            else{
                return 0;
            }

        }catch(SQLException e){
            log("Failed to check password ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating password
     * @param password
     * @return integer
     * @throws SQLException
     * @throws UnknownHostException
     * @throws SocketException
     * return codes:
     *  1 - password updated
     * -1 - database error
     */
    public int change_password(String password) throws SQLException, UnknownHostException, SocketException, NoSuchAlgorithmException{
        String query = "UPDATE ADMIN_DATA SET admin_password = ? where admin_id=?;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);

            ppst.setString(1, code_password(password));
            ppst.setInt(2, admin_id);

            log_event("Trying to change admin password admin(id:"+admin_id+")","ADMIN_CHANGED_PASS");
            ppst.execute();
            log_event("Updated succesfully admin(id"+admin_id+") machne: "+get_local_MACAddress(),"ADMIN_CHANGED_PASS");
            return 1;
        }catch(SQLException e){
            log("Failed to change admin (id:"+admin_id+") ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting current user logged on the machine
     * @return Integer
     * @throws SQLException
     */
    public int get_logged_user() throws SQLException, UnknownHostException, SocketException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );

        String query = "SELECT admin_id,data_log_date FROM DATA_LOG where data_log_desc = ? ORDER BY data_log_id DESC LIMIT 1;";

        PreparedStatement ppst = con.prepareStatement(query);

        try{
            ppst.setString(1,get_local_MACAddress());

            ResultSet rs = ppst.executeQuery();

            if ( rs.next() ){
                LocalDateTime time_logon = rs.getObject("data_log_date",LocalDateTime.class);
                todayLocalDate = todayLocalDate.minusMinutes(10);

                if ( todayLocalDate.isAfter(time_logon) ){
                    return rs.getInt("admin_id");
                }
                return -2;
            }
            return 0;

        }catch(SQLException e){
            log("Failed to get logged user from database ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for log event ADMIN_LOGIN
     * @param admin_id
     * @return Integer
     */
    public int log_ADMIN_LOGIN(int admin_id) throws SQLException, UnknownHostException, SocketException{
        LocalDateTime todayLocalDate = LocalDateTime.now( ZoneId.of( "Europe/Warsaw" ) );

        String query = "INSERT INTO DATA_LOG (admin_id,data_log_date,data_log_action,data_log_desc)"
                + " VALUES\n"
                + "(?,?,?,?);";

        PreparedStatement ppst = con.prepareStatement(query);

        try{

            ppst.setInt(1,admin_id);
            ppst.setObject(2,todayLocalDate);
            ppst.setString(3,"ADMIN_LOGIN");
            ppst.setString(4,get_local_MACAddress());

            ppst.execute();

            return 1;

        }catch(SQLException e){
            log("Failed to log ADMIN_LOGIN event ("+e.toString()+")");
            return -1;
        }catch(UnknownHostException ex){
            log("Failed to get local MAC Address ("+ex.toString()+")");
            return -2;
        }
    }

    /**
     * Function for getting all messages
     * @return ArrayList
     * @throws SQLException
     */
    public ArrayList<String> get_all_messages() throws SQLException{
        ArrayList<String> data = new ArrayList<>();

        String query = "SELECT * FROM USER_MESSAGE;";

        try{
            PreparedStatement ppst = con.prepareStatement(query);


            ResultSet rs = ppst.executeQuery();


            while( rs.next() ){
                data.add(rs.getInt("user_message_id") +": "+get_worker_nameusername(rs.getInt("worker_id"))
                        +" |"+rs.getString("user_message_content"));
            }

            return data;

        }catch(SQLException e){
            log("Failed to get all messages ("+e.toString()+")");
            return null;
        }
    }

}