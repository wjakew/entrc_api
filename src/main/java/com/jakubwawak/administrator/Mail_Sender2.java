/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.administrator;

import java.io.File;
import java.util.ArrayList;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *Object for maintaining e-mail connections
 * @author kubaw
 */
public class Mail_Sender2 {
    
    // variables for e-mail addresses
    String to;
    // variables for storing data
    Properties properties;
    Session session;
    
    // variables for maintaining 
    String message,title;
    File attachment;
    boolean attachment_set;
    
    // variables for administration data
    String administration_mail = "main.tes.instruments@gmail.com";
    String administration_pass = "minidysk";
    
    //Constructor
    /**
     *Main object constructor
     * @param e_mail_to
     */
    public Mail_Sender2(String e_mail_to){
        to = e_mail_to;

        attachment = null;
        properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        
        session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(administration_mail, administration_pass);
            }
        });
        session.setDebug(true);
    }
    
    /**
     * Function for setting title
     * @param title 
     */
    public void set_title(String title){
        this.title = title;
    }
    
    /**
     * Function for setting message
     * @param message 
     */
    public void set_message(String message){
        this.message = message;
    }
    /**
     * Function for setting message
     * @param message 
     */
    public void set_message(ArrayList<String> message){
        String to_send = "";
        for(String line : message){
            to_send = to_send + line + "\n";
        }
        this.message = to_send;
    }
    
    /**
     * Function for adding 
     * @param attachment_src
     * @return boolean
     */
    public boolean set_attachment(String attachment_src){
        attachment = new File(attachment_src);
        return attachment.exists();
    }
    
    /**
     * Function for sending e-mail
     */
    public boolean send_message() throws MessagingException{
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(administration_mail));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(title);

            // Now set the actual message
            message.setText(this.message);
            // Send message
            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }
}
