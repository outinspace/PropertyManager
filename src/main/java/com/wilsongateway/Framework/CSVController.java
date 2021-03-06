package com.wilsongateway.Framework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.javalite.activejdbc.ColumnMetadata;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.vaadin.server.VaadinService;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class CSVController {

	private final EncryptedModel dataType;
	private final LazyList<? extends Model> models;
	private final String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private final String subFolder = "/WEB-INF/classes/reports/";
	
	private File destination;
	
	public CSVController(EncryptedModel dataType, LazyList<? extends Model> models){
		this.dataType = dataType;
		this.models = models;
	}
	
	public void createDestination(){
		destination = new File(basepath + subFolder + dataType.getTableName() + ".csv");
		System.out.println("Creating Report at: " + destination);
	}
	
	public boolean createCSVFile(){
		createDestination();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(destination));
			
			//Write column headers
			String header = "";
			for(String attribute : dataType.attributeNames()){
				header += attribute + ",";
			}
			writer.write(header);
			writer.newLine();
			
			//Write data line by line
			for(Model model : models){
				String newLine = "";
				for(String attribute : model.attributeNames()){
					Object value = ((EncryptedModel)model).getDecrypted(attribute);
					if(value != null){
						newLine += value.toString().replace(",", " ");
					}
					newLine += ",";
				}
				writer.write(newLine);
				writer.newLine();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				writer.close();
			} catch (Exception e) {}
		}
	}
	
	public File getFile(){
		return destination;
	}
}
