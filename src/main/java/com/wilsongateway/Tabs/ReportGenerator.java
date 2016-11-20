package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.ReportController;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;

@SuppressWarnings("serial")
public class ReportGenerator extends Tab{

	public ReportGenerator(SessionManager manager) {
		super("Report Generator", manager);
		
		initGui();
	}

	private void initGui() {
		Label heading = new Label("Report Generator");
		addComponent(heading);
		addLineBreak();
		
		Button userBtn = new Button("Generate User Report");
		userBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				downloadReport(Tables.USER, userBtn);
			}
			
		});
		addComponent(userBtn);
	}

	private void downloadReport(EncryptedModel model, Button downBtn) {//TODO fix multiple downloads
		downBtn.setCaption("");
		downBtn.setIcon(FontAwesome.SPINNER);
		
		SessionManager.openBase();
		
		ReportController reportGen = new ReportController(model);
		if(reportGen.createCSVFile()){
			Resource res = new FileResource(reportGen.getFile());
			FileDownloader downloader = new FileDownloader(res);
			downloader.extend(downBtn);
			
			downBtn.setCaption("Click To Download");
			downBtn.setIcon(null);
		}else{
			Notification.show("Could Not Generate Report", Notification.Type.WARNING_MESSAGE);
		}
		
		SessionManager.closeBase();
	}

	@Override
	public void enter(ViewChangeEvent event) {}
}
