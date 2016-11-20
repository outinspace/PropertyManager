package com.wilsongateway.Tabs;

import java.io.File;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class LogoScreen extends VerticalLayout implements View {

	private Image logo;
	
	public LogoScreen(){
		setSizeFull();
		
		//TODO make dynamic
		addComponent(new Label(""));
		addComponent(new Label(""));
		addComponent(new Label(""));
		addComponent(new Label(""));
		addComponent(new Label(""));
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource logoResource = new FileResource(new File(basepath + "/WEB-INF/classes/images/mercyLetterM.png"));
		
		logo = new Image("", logoResource);
		logo.setWidth("400px");
		addComponent(logo);
		setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {}
}
