/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.entrc_api;

import com.jakubwawak.administrator.Configuration;
import com.jakubwawak.database.Database_APIController;
import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.database.Database_ProgramCodes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;

@SpringBootApplication (scanBasePackages = {"com.jakubwawak"})
public class EntrcApi {

	public final static String version = "v.1.1.0";
	public final static String build_number = "121121REV1";
	public static Database_Connector database;
	static int debug = 1;
	public static EntrcApi_Logger eal;
	public static Database_APIController dac;
	static Scanner user_handler;
	static Admin_Auth adm_auth;
	static Scanner user_input;
	public static void main(String[] args) throws UnknownHostException {
		show_header();
		eal = new EntrcApi_Logger(version,debug);
		System.out.println("Log printing:");
		try{
			database = new Database_Connector();
			user_handler = new Scanner(System.in);

			Configuration config = new Configuration("config.entrconf");
			config.run();
			if (config.prepared){
				database.connect(config.ip,config.database,config.databaseuser,config.databasepass);
			}
			else{
				System.out.println("Configuration file not found");
				String ip,databasename,databaseuser,databasepassword;
				System.out.print("ip:");
				ip = user_handler.nextLine();
				config.ip = ip;
				System.out.print("database name:");
				databasename = user_handler.nextLine();
				config.database = databasename;
				System.out.print("user:");
				databaseuser = user_handler.nextLine();
				config.databaseuser = databaseuser;
				System.out.print("password:");
				databasepassword = user_handler.nextLine();
				config.databasepass = databasepassword;

				database.connect(config.ip,config.database,config.databaseuser, config.databasepass);

				config.copy_configuration();
			}
			if ( database.connected ){
				InetAddress inetAddress = InetAddress.getLocalHost();
				dac = new Database_APIController(database);
				dac.log("ENTRC_API loaded on "+inetAddress.getHostAddress()+":8080");
				System.out.println("Database connected!\nRunning server...");
				Database_ProgramCodes dpc = new Database_ProgramCodes(database);
				System.out.println("Admin login requires to run API.");
				// admin login in loop (Admin_Auth)
				// admin asked about gui app (opens window in new thread)
				user_input = new Scanner(System.in);
				System.out.println("You need to use admin auth to run thi api");
				System.out.print("admin_login: ");
				Console cnsl = System.console();
				String admin_login = user_input.nextLine();
				String admin_password = "";
				if ( cnsl == null ){
					System.out.print("password: ");
					admin_password = user_input.nextLine();
				}
				else{
					System.out.print("password: ");
					admin_password = String.valueOf(cnsl.readPassword());
				}
				adm_auth = new Admin_Auth(admin_login,admin_password,database);
				adm_auth.login();
				if (adm_auth.logged){
					System.out.println("Access authorized");
					if (dpc.get_value("API_ENABLED").equals("TRUE")) {
						dac.log("ENTRC API ENABLED ON THAT DATABASE. LOADING SPRING");
						SpringApplication.run(EntrcApi.class, args);
					}
					else{
						System.out.println("ENTRC API IS NOT ENABLED. TO ENABLE CHANGE SETTINGS IN ADMIN APP");
						dac.log("ENTRC API IS NOT ENABLED. TO ENABLE CHANGE SETTINGS IN ADMIN APP");
					}
				}
				else{
					System.out.println("Access not granted. Wrong password");
				}
			}
			else{
				System.out.println("Failed to connect to the database");
			}

		}catch(SQLException e) {
			System.out.println("Database Exception: " + e.toString());
		} catch (FileNotFoundException e) {
			System.out.println("File not found: "+e.toString());
		} catch (IOException e) {
			System.out.println("IOException: "+e.toString());
		} catch (URISyntaxException e) {
			System.out.println("URI exception: "+e.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found error: "+e.toString());
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Class not found error: "+e.toString());
		}
	}

	/**
	 * Function for showing ipconfiguration
	 */
	static void show_ipconfig() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		System.out.println("EntrcApi current ip configuration:");
		System.out.println("ip:"+inetAddress.getHostAddress()+"port: 8080");
		System.out.println("machine name: "+inetAddress.getHostName());
	}

	/**
	 * Function for showing header of application
	 */
	static void show_header() throws UnknownHostException {
		String header = " _____       _               _    ____ ___\n" +
						"| ____|_ __ | |_ _ __ ___   / \\  |  _ \\_ _|\n" +
						"|  _| | '_ \\| __| '__/ __| / _ \\ | |_) | |\n" +
						"| |___| | | | |_| | | (__ / ___ \\|  __/| |\n" +
						"|_____|_| |_|\\__|_|  \\___/_/   \\_\\_|  |___|\n";
		header = header+"by JAKUB WAWAK       2021         "+version+"/"+build_number+"\n";
		System.out.println(header);
		System.out.println("\n");
		show_ipconfig();
	}

}
