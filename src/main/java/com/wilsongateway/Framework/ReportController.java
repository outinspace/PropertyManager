package com.wilsongateway.Framework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.javalite.activejdbc.ColumnMetadata;
import org.javalite.activejdbc.Model;

import com.vaadin.server.VaadinService;

public class ReportController {

	private final EncryptedModel dataType;
	private final String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private final String subFolder = "/WEB-INF/classes/reports/";
	
	private File destination;
	
	public ReportController(EncryptedModel dataType){
		this.dataType = dataType;
	}
	
	public void createDestination(){//TODO io locking, add iterating names + autodeletion
		destination = new File(basepath + subFolder + dataType.getTableName() + ".csv");
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
			for(Model model : dataType.findAll()){
				String newLine = "";
				for(String attribute : model.attributeNames()){
					String value = ((EncryptedModel)model).getDecrypted(attribute).toString();
					if(value != null){
						newLine += value;
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
