package com.wilsongateway.SubPanels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;

public class ImageUploader extends Window implements Receiver, SucceededListener{
	
	private static final long serialVersionUID = -3979195317838824968L;

	private SessionManager manager;
	
	private VerticalLayout content;
	
	private FileOutputStream fos;
	private String filename;
	private String mimeType;
	
	public ImageUploader(SessionManager manager) {
		super("Upload Image");
		this.manager = manager;
		
		content = new VerticalLayout();
		setContent(content);
		
		Image embedded = new Image("Uploaded Image");
		embedded.setVisible(false);
		content.addComponent(embedded);
		
		Upload uploader = new Upload("", this);
		content.addComponent(uploader);
		
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		this.filename = filename;
		this.mimeType = mimeType;

		try {
			fos = new FileOutputStream(new File(SessionManager.getResourcePath() 
					+ "/cachedImages/" + manager.getCurrentUser().getDecrypted("username")));
		} catch (FileNotFoundException | NullPointerException e) {
			e.printStackTrace();
		}
		
		return fos;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("Upload Succeeded", Notification.Type.TRAY_NOTIFICATION);
		
		
		//TODO save to database
		
		
	}

}
