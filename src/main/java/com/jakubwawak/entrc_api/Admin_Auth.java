/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrc_api;

import com.jakubwawak.database.Database_Connector;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Admin_Auth {

    Database_Connector database;
    private String login,password;
    boolean logged;
    /**
     * Constructor
     * @param login
     * @param password
     */
    public Admin_Auth(String login, String password,Database_Connector database){
        this.login = login;
        this.password = password;
        this.database = database;
        logged = false;
    }

    /**
     * Function for logging in aplication
     * @return
     * @throws SocketException
     * @throws UnknownHostException
     * @throws SQLException
     * @throws NoSuchAlgorithmException
     */
    public boolean login() throws SocketException, UnknownHostException, SQLException, NoSuchAlgorithmException {
        if ( database.login_admin(login,password) == 1){
            logged = true;
            return true;
        }
        logged = false;
        return false;
    }
}
