package com.wilsongateway.Framework;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Tabs.AccountTab;
import com.wilsongateway.Tabs.AdminConsole;
import com.wilsongateway.Tabs.EditAllTickets;
import com.wilsongateway.Tabs.EditClient;
import com.wilsongateway.Tabs.EditGroup;
import com.wilsongateway.Tabs.EditLocalTickets;
import com.wilsongateway.Tabs.EditProperty;
import com.wilsongateway.Tabs.EditTicket;
import com.wilsongateway.Tabs.EditUser;
import com.wilsongateway.Tabs.ErrorTab;
import com.wilsongateway.Tabs.ViewAllClients;
import com.wilsongateway.Tabs.ViewAllGroups;
import com.wilsongateway.Tabs.ViewAllProperties;
import com.wilsongateway.Tabs.ViewAllUsers;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public abstract class Tab extends VerticalLayout implements View{

	private static final long serialVersionUID = 161818186695982586L;
	
	protected SessionManager manager;
	protected String name;
	protected String description;
	
	//Enums for all Tabs
	public enum TabType {ACCOUNTTAB, ADMINCONSOLE,
									ADDUSER, VIEWALLUSERS, EDITALLUSERS, 
									ADDGROUP, VIEWALLGROUPS, EDITALLGROUPS,
									ADDPROPERTY, VIEWALLPROPERTIES, EDITALLPROPERTIES,
									ADDCLIENT, VIEWALLCLIENTS, EDITALLCLIENTS,
									ADDTICKET, EDITLOCALTICKETS, EDITALLTICKETS};
	
	protected Tab(String name, SessionManager manager){
		this.name = name;
		this.manager = manager;
		description = "";
		
		//Config Vertical Layout
		this.setMargin(true);
		this.setSpacing(true);
	}
	
	protected void addLineBreak(){
		addComponent(new Label("<hr />",ContentMode.HTML));
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public static Tab getInstance(TabType type, SessionManager manager, DashboardView dash){
		try{
			SessionManager.openBase();
			switch(type){
			case ACCOUNTTAB:
				return new AccountTab(manager);
			case ADMINCONSOLE:
				return new AdminConsole(manager);
			case ADDUSER:
				return new EditUser(manager, null, true);
			case ADDGROUP:
				return new EditGroup(manager, null, true);
			case VIEWALLUSERS:
				return new ViewAllUsers(manager, false);
			case EDITALLUSERS:
				return new ViewAllUsers(manager, true);
			case VIEWALLGROUPS:
				return new ViewAllGroups(manager, false);
			case EDITALLGROUPS:
				return new ViewAllGroups(manager, true);
			case ADDPROPERTY:
				return new EditProperty(manager, null, true);
			case VIEWALLPROPERTIES:
				return new ViewAllProperties(manager, false);
			case EDITALLPROPERTIES:
				return new ViewAllProperties(manager, true);
			case ADDCLIENT:
				return new EditClient(manager, null, true);
			case VIEWALLCLIENTS:
				return new ViewAllClients(manager, false);
			case EDITALLCLIENTS:
				return new ViewAllClients(manager, true);
			case ADDTICKET:
				return new EditTicket(manager, null, true);
			case EDITLOCALTICKETS:
				return new EditLocalTickets(manager);
			case EDITALLTICKETS:
				return new EditAllTickets(manager);
			default:
				return new ErrorTab(manager, "Tab not yet created");
		}
		}finally{
			SessionManager.closeBase();
		}
	}
}
