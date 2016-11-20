package com.wilsongateway.Framework;

import java.io.File;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public class LoginView extends VerticalLayout implements View{

	private SessionManager manager;
	
	//Login GUI elements
	private VerticalLayout content;
	private Panel loginPanel;
	private Button rightBtn;
	private Button leftBtn;
	private TextField usernameField;
	private PasswordField passwordField;
	private TextField emailField;
	private Image logo;
	
	private enum loginMode{FORGOT, LOGIN};
	private loginMode mode;
	//private int attempts = 0;
	
	public LoginView(SessionManager manager){
		this.manager = manager;
		mode = loginMode.LOGIN;
		
		setMargin(true);
		setSizeFull();
		
		content = new VerticalLayout();
		content.setSizeUndefined();
		content.setSpacing(true);
		addComponent(content);
		setComponentAlignment(content, Alignment.MIDDLE_CENTER);
		
		loadWelcomeGUI();
		
		loadLoginGUI();
	}
	
	private void loadLoginGUI() {
		loginPanel = new Panel();
		loginPanel.setVisible(true);
		loginPanel.setSizeUndefined();
		content.addComponent(loginPanel);
		content.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		
		VerticalLayout loginContent = new VerticalLayout();
		loginContent.setSizeUndefined();
		loginContent.setSpacing(true);
		loginContent.setMargin(true);
		loginPanel.setContent(loginContent);
		
		/*
		 * Adding the login components to the loginContent.
		 * When register button is clicked, the register components are set to be visible.
		 */
		usernameField = new TextField("Username");
		usernameField.setColumns(15);
		loginContent.addComponent(usernameField);
		
		passwordField = new PasswordField("Password");
		passwordField.setWidth("100%");
		loginContent.addComponent(passwordField);
		
		emailField = new TextField("Email");
		emailField.setWidth("100%");
		emailField.setVisible(false);
		loginContent.addComponent(emailField);
		
		HorizontalLayout buttonContent = new HorizontalLayout();
		buttonContent.setWidth("100%");
		buttonContent.setSpacing(true);
		loginContent.addComponent(buttonContent);
		
		leftBtn = new Button("Forgot?");
		leftBtn.setStyleName("quiet");
		leftBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				switch(mode){
				case LOGIN:
					emailField.setVisible(true);
					mode = loginMode.FORGOT;
					leftBtn.setCaption("Back");
					rightBtn.setCaption("Send Email");
					usernameField.focus();
					break;
				case FORGOT:
					emailField.setVisible(false);
					mode = loginMode.LOGIN;
					leftBtn.setCaption("Forgot?");
					rightBtn.setCaption("Login");
					usernameField.focus();
					break;
				}
			}
			
		});
		buttonContent.addComponent(leftBtn);
		buttonContent.setComponentAlignment(leftBtn, Alignment.MIDDLE_LEFT);
		
		rightBtn = new Button("Login");
		rightBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				executeLogin();
			}
			
		});
		
		buttonContent.addComponent(rightBtn);
		buttonContent.setComponentAlignment(rightBtn, Alignment.MIDDLE_RIGHT);
	}

	private void loadWelcomeGUI(){
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource logoResource = new FileResource(new File(basepath + "/WEB-INF/classes/images/mercyLogo.png"));
		
		logo = new Image("", logoResource);
		logo.setWidth("400px");
		content.addComponent(logo);
		content.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
		
		Label spacer = new Label("");
		spacer.setHeight("1em");
		content.addComponent(spacer);
	}
	
	private void executeLogin(){
		SessionManager.openBase();
		
		switch(mode){
		case LOGIN:
			User temp = Tables.USER.find(usernameField.getValue());
			
			if(temp != null && temp.getPassword().equals(passwordField.getValue())){
				manager.setCurrentUser(temp);
				
				//create and navigate to dashboard
				manager.getNav().addView(SessionManager.DASHBOARDVIEW, new DashboardView(manager));
				manager.getNav().navigateTo(SessionManager.DASHBOARDVIEW);
			}else{
				Notification.show("Incorrect Username or Password");
			}
			break;
		case FORGOT:
			Notification.show("Does nothing yet...");
			break;
		}
		SessionManager.closeBase();
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		//TODO for testing purposes
		if(SessionManager.TESTMODE){
			usernameField.setValue("admin");
			passwordField.setValue("Password1");
		}
	}
	
}
