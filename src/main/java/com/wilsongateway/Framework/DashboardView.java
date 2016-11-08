package com.wilsongateway.Framework;

import java.io.File;
import java.util.ArrayList;

import org.javalite.activejdbc.LazyList;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.wilsongateway.Framework.Tab.TabType;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Tabs.ErrorTab;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DashboardView extends VerticalLayout implements View{

	private SessionManager manager;
	
	//UI Elements
	private Navigator tabNav;
	private VerticalLayout dashContent;
	private VerticalLayout sideContent;
	private Accordion sideMenu;
	
	public DashboardView(SessionManager manager){
		manager.ensureBase();
		manager.setCurrentDash(this);
		
		this.manager = manager;
		this.setSizeFull();
		this.setHeightUndefined();
		
		GridLayout mainGrid = new GridLayout(2, 1);
		mainGrid.setSizeFull();
		this.addComponent(mainGrid);
		
		mainGrid.setColumnExpandRatio(0, 1);
		mainGrid.setColumnExpandRatio(1, 4);
		
		dashContent = new VerticalLayout();
		mainGrid.addComponent(dashContent, 1, 0);
		
		tabNav = new Navigator(manager, dashContent);
		//TODO load initial view or default dashboard
		
		sideContent = new VerticalLayout();
		sideContent.setSpacing(true);
		mainGrid.addComponent(sideContent, 0, 0);
		
		addUpperSideContent();
		
		sideMenu = new Accordion();
		sideContent.addComponent(sideMenu);
		
		populateSideMenu();
	}
	
	private void addUpperSideContent() {
		VerticalLayout logoContent = new VerticalLayout();
		logoContent.setWidth("90%");
		//logoContent.setMargin(true);
		
		sideContent.addComponent(logoContent);
		sideContent.setComponentAlignment(logoContent, Alignment.MIDDLE_RIGHT);
		
		Label spacer = new Label("");
		spacer.setHeight("1em");
		logoContent.addComponent(spacer);
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource logoResource = new FileResource(new File(basepath + "/WEB-INF/classes/images/mercySmall.png"));
		
		Image logo = new Image(null, logoResource);
		logo.setSizeFull();
		logoContent.addComponent(logo);
		logoContent.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
		
		logoContent.addComponent(new Label("Welcome back " + manager.getCurrentUser().getUsername()));
	}

	private void populateSideMenu() {
		//Get list of all groups
		LazyList<Tables.Group> groups = manager.getCurrentUser().getAll(Group.class);
		
		for(Group g : groups){
			//Create container for subMenu
			VerticalLayout content = new VerticalLayout();
			
			for(TabType type : g.getTabs()){
				Tab t;
				try{
					t = Tab.getInstance(type, manager, this);
				}catch(Exception e){
					t = new ErrorTab(manager, e.getMessage());
					e.printStackTrace();
				}
				
				tabNav.addView(type.toString(), t);
				
				Button b = new Button(t.getName());
				b.setStyleName("link");
				b.addClickListener(new ClickListener(){

					@Override
					public void buttonClick(ClickEvent event) {
						tabNav.navigateTo(type.toString());
					}
					
				});
				content.addComponent(b);
			}
			sideMenu.addTab(content, g.getString("name"));
		}
		
		sideMenu.addTab(new CssLayout(), "", FontAwesome.CLOSE);
	}
	
	public Navigator getTabNav(){
		return tabNav;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		if(manager.getCurrentUser() == null){
			SessionManager.getCurrent().getNavigator().navigateTo(SessionManager.LOGINVIEW);
		}
	}

}
