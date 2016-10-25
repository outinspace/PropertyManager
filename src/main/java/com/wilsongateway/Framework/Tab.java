package com.wilsongateway.Framework;

import com.vaadin.navigator.View;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Deprecated.EditGroupDep;
import com.wilsongateway.Deprecated.EditUserDep;
import com.wilsongateway.Deprecated.ViewAllGroupsDep;
import com.wilsongateway.Deprecated.ViewAllUsersDep;
import com.wilsongateway.Tabs.AccountTab;
import com.wilsongateway.Tabs.EditGroup;
import com.wilsongateway.Tabs.EditProperty;
import com.wilsongateway.Tabs.EditUser;
import com.wilsongateway.Tabs.ViewAllGroups;
import com.wilsongateway.Tabs.ViewAllProperties;
import com.wilsongateway.Tabs.ViewAllUsers;

@SuppressWarnings("serial")
public abstract class Tab extends VerticalLayout implements View{

	protected SessionManager manager;
	protected String name;
	protected String description;
	
	//Enums for all Tabs
	public enum TabType {ACCOUNTTAB, ADDUSER, VIEWALLUSERS, EDITALLUSERS, 
									ADDGROUP, VIEWALLGROUPS, EDITALLGROUPS,
									ADDPROPERTY, VIEWALLPROPERTIES, EDITALLPROPERTIES};
	
	protected Tab(String name, SessionManager manager){
		this.name = name;
		this.manager = manager;
		description = "";
		
		//Config Vertical Layout
		this.setMargin(true);
		this.setSpacing(true);
		
		manager.ensureBase();
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
		switch(type){
			case ACCOUNTTAB:
				return new EditUser(manager, manager.getCurrentUser());
			case ADDUSER:
				return new EditUser(manager, null);
			case ADDGROUP:
				return new EditGroup(manager, null);
			case VIEWALLUSERS:
				return new ViewAllUsers(manager, false);
			case EDITALLUSERS:
				return new ViewAllUsers(manager, true);
			case VIEWALLGROUPS:
				return new ViewAllGroups(manager, false);
			case EDITALLGROUPS:
				return new ViewAllGroups(manager, true);
			case ADDPROPERTY:
				return new EditProperty(manager, null);
			case VIEWALLPROPERTIES:
				return new ViewAllProperties(manager, false);
			case EDITALLPROPERTIES:
				return new ViewAllProperties(manager, true);
		}
		return null;
	}
}
