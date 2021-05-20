/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */
package com.jakubwawak.barcode;


import com.jakubwawak.database.Database_Connector;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/*
by Jakub Wawak
kubawawak@gmail.com
all rights reserved
 */

/**
 *Object for creating barcodes
 * @author kubaw
 */
public class BarCodeCreator {
    
    public String worker_pin;
    public int worker_id;
    public Date date;
    public Barcode128 barcode_object;
    public String raw_barecode_data;
    boolean new_flag;
    
    
    public String file_name;
    
    /**
     * Constructor for creating blank object
     * @param worker_pin 
     */
    public BarCodeCreator(String worker_pin){
        this.worker_pin = worker_pin;
        worker_id = -1;
        date = new Date();
        barcode_object = null;
        raw_barecode_data = "";
        new_flag = true;
        file_name = null;
        generate_barcode();
    }
    
    /**
     * Constructor for creating object
     * @param worker_pin
     * @param raw_barecode_data 
     */
    public BarCodeCreator(String worker_pin,String raw_barecode_data){
        this.worker_pin = worker_pin;
        worker_id = -1;
        this.raw_barecode_data = raw_barecode_data;
        date = new Date();
        new_flag = false;
        file_name = null;
        generate_barcode();
    }
    
    /**
     * Object for loading barcode from database
     * @param to_add 
     */
    public BarCodeCreator(ResultSet to_add) throws SQLException{
        this.worker_pin = null;
        worker_id = to_add.getInt("worker_id");
        this.raw_barecode_data = to_add.getString("barcode_raw_data");
        date = Date.from(to_add.getObject("barcode_date",LocalDateTime.class)
                .atZone(ZoneId.systemDefault()).toInstant());
        new_flag = false;
        file_name = null;
        prepare_barcode();
    }
    
    /**
     * Function for parsing date object
     * @return String
     */
    String parse_date(){
        String[] elements = date.toString().split(" ");
        String[] time = elements[3].split(":");
        return elements[2]+time[0]+time[1]+time[2];
    }
    
    /**
     * Function for preparing barcode from data from database
     */
    public void prepare_barcode(){
        barcode_object = new Barcode128();
        barcode_object.setGenerateChecksum(true);
        barcode_object.setCode(raw_barecode_data);
    }
    
    /**
     * Function for generating barcode
     */
    public void generate_barcode(){
        barcode_object = new Barcode128();
        raw_barecode_data = parse_date()+worker_pin;
        barcode_object.setGenerateChecksum(true);
        barcode_object.setCode(raw_barecode_data);
    }
    
    /**
     * Function for creating image
     * @return Image
     */
    public Image get_image(){
        return barcode_object.createAwtImage(Color.WHITE, Color.BLACK);
    }
    /**
     * Function for creating pdf
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws SQLException 
     */
    public void create_pdf(Database_Connector database) throws FileNotFoundException, DocumentException, SQLException{
        
        try{
            Document document = new Document(new Rectangle(PageSize.A4));   
            String data;
            if ( worker_pin == null ){
                data = database.get_worker_data(worker_id);
            }
            else
                data = database.get_worker_data(worker_pin);
            
            data.replace(" ", "");
            file_name = data+"_entrccode_.pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file_name));
            document.open(); 
            document.add(barcode_object.createImageWithBarcode(writer.getDirectContent(),null,null));
            document.add(new Paragraph(database.get_worker_data(worker_pin)));
            document.close();
        }catch(NullPointerException e){
            database.log("No generated barcode ("+e.toString()+")");
        }
    }
}
