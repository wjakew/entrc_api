package com.jakubwawak.entrc_api;

import com.jakubwawak.database.Database_Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class EntrcApi {

	final static String version = "v.1.0.0A1";
	public static Database_Connector database;
	static int debug = 1;
	static EntrcApi_Logger eal;

	public static void main(String[] args){
		show_header();
		eal = new EntrcApi_Logger(version,debug);

		try{
			database = new Database_Connector();

			database.connect("192.168.1.100","entrc_database","entrc_admin","password");

			if ( database.connected ){
				System.out.println("Database connected!\n Running server...");
				SpringApplication.run(EntrcApi.class, args);
			}
			else{
				System.out.println("Failed to connect to the database");
			}

		}catch(SQLException | ClassNotFoundException e) {
			System.out.println("Database Exception: " + e.toString());
		}
	}

	/**
	 * Function for showing header of application
	 */
	static void show_header(){
		String header = " _____       _               _    ____ ___\n" +
						"| ____|_ __ | |_ _ __ ___   / \\  |  _ \\_ _|\n" +
						"|  _| | '_ \\| __| '__/ __| / _ \\ | |_) | |\n" +
						"| |___| | | | |_| | | (__ / ___ \\|  __/| |\n" +
						"|_____|_| |_|\\__|_|  \\___/_/   \\_\\_|  |___|\n";
		header = header+"by JAKUB WAWAK       2021            "+version+"\n";
		System.out.println(header);
	}

}
