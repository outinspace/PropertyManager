package com.wilsongateway.Framework;

import java.io.File;
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
import com.wilsongateway.Framework.Tab.TabType;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Tabs.LogoScreen;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class DashboardView extends VerticalLayout implements View{

	private static final long serialVersionUID = 5093369175229691667L;

	private SessionManager manager;
	
	//UI Elements
	private HorizontalLayout topBar;
	private VerticalLayout dashContent;
	private VerticalLayout sideContent;
	
	private Accordion sideMenu;
	
	private Navigator tabNav;
	
	//Navigation Variables
	private TabType lastTab;
	public final static String DEFAULT = "";
	
	public DashboardView(SessionManager manager){
		SessionManager.openBase();
		manager.setCurrentDash(this);
		
		this.manager = manager;
		setSizeFull();
		setHeightUndefined();
		
		topBar = new HorizontalLayout();
		topBar.setSpacing(true);
		topBar.setSizeUndefined();
		addComponent(topBar);
		setComponentAlignment(topBar, Alignment.TOP_RIGHT);
		fillTopBar();
		
		GridLayout mainGrid = new GridLayout(2, 1);
		mainGrid.setSizeFull();
		addComponent(mainGrid);
		
		mainGrid.setColumnExpandRatio(0, 1);
		mainGrid.setColumnExpandRatio(1, 4);
		
		dashContent = new VerticalLayout();
		mainGrid.addComponent(dashContent, 1, 0);
		
		tabNav = new Navigator(manager, dashContent);
		tabNav.addView(DEFAULT, new LogoScreen());
		tabNav.navigateTo(DEFAULT);
		tabNav.setErrorView(new LogoScreen());
		
		sideContent = new VerticalLayout();
		sideContent.setSpacing(true);
		mainGrid.addComponent(sideContent, 0, 0);
		
		addUpperSideContent();
		
		sideMenu = new Accordion();
		sideContent.addComponent(sideMenu);
		populateSideMenu();
		
		SessionManager.closeBase();
	}
	
	private void addUpperSideContent() {
		VerticalLayout logoContent = new VerticalLayout();
		logoContent.setWidth("90%");
		
		sideContent.addComponent(logoContent);
		sideContent.setComponentAlignment(logoContent, Alignment.MIDDLE_RIGHT);
		
		String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
		FileResource logoResource = new FileResource(new File(basepath + "/WEB-INF/classes/images/mercySmall.png"));
		
		Image logo = new Image(null, logoResource);
		logo.setSizeFull();
		logoContent.addComponent(logo);
		logoContent.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
		
		logoContent.addComponent(new Label("Welcome back " + manager.getCurrentUser().toString()));
	}

	private void populateSideMenu() {//Lazy loading
		//Get list of all groups
		LazyList<Tables.Group> groups = manager.getCurrentUser().getAll(Group.class);
		
		for(Group g : groups){
			//Create container for subMenu
			VerticalLayout content = new VerticalLayout();
			content.setSizeUndefined();
			
			for(TabType type : g.getTabs()){
				try{
					Tab t = Tab.getInstance(type, manager, this);
					tabNav.addView(type.toString(), t);
					
					Button pageLink = new Button(t.getName());
					pageLink.addClickListener(e -> {
						tabNav.navigateTo(type.toString());
						lastTab = type;
					});
					pageLink.setStyleName("link");
					content.addComponent(pageLink);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			sideMenu.addTab(content, g.getAsString("name"));
		}
		sideMenu.setSelectedTab(sideMenu.addTab(new CssLayout(), "", FontAwesome.CLOSE));
	}
	
	private void fillTopBar(){
		Button popoutBtn = new Button();
		popoutBtn.setIcon(FontAwesome.BINOCULARS);
		popoutBtn.setStyleName("tiny");
		popoutBtn.addClickListener(e -> {
			if(lastTab != null){
				Window subWindow = new Window(": :");
				subWindow.setStyleName("tiny");
				subWindow.setContent(Tab.getInstance(lastTab, manager, this));
				subWindow.setHeight("40%");
				subWindow.setWidth("40%");
				subWindow.center();
				
				UI.getCurrent().addWindow(subWindow);
			}else{
				Notification.show("Select A Tab First", Notification.Type.TRAY_NOTIFICATION);
			}
		});
		topBar.addComponent(popoutBtn);
		
		Button logoutBtn = new Button("Logout");
		logoutBtn.setIcon(FontAwesome.SIGN_OUT);
		logoutBtn.setStyleName("tiny");
		topBar.addComponent(logoutBtn);
	}

	public Navigator getTabNav(){
		return tabNav;
	}

	public void navigateBack(){
		if(lastTab != null){
			tabNav.navigateTo(lastTab.toString());
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(manager.getCurrentUser() == null){
			SessionManager.getCurrent().getNavigator().navigateTo(SessionManager.LOGINVIEW);
		}
	}

}
