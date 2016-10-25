package com.wilsongateway.Deprecated;

import org.javalite.activejdbc.Model;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Table;
import com.wilsongateway.Framework.DashboardView;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Tabs.EditUser;

@SuppressWarnings("serial")
public class ViewAllUsersDep extends Tab{

	private Table t;
	
	public ViewAllUsersDep(SessionManager manager) {
		super("View All Users", manager);
		
		t = new Table(null);
		t.setSizeUndefined();
		t.addContainerProperty("username", String.class, "");
		t.addContainerProperty("password", String.class, "");
		t.addContainerProperty("first_name", String.class, "");
		t.addContainerProperty("last_name", String.class, "");
		t.addContainerProperty("groups", String.class, "");
		t.addContainerProperty("", CssLayout.class, "");
		
		populateTable();
		addComponent(t);
	}

	private void populateTable() {
		t.removeAllItems();
		//Iterate through all users
		for(Model m : Tables.USER.findAll()){
			User usr = (User)m;
			int length = t.getContainerPropertyIds().size();
			Object[] cells = new Object[length];
			
			//get column data from user using propertyIds
			for(int i = 0; i < length - 1; i++){
				String id = (String)t.getContainerPropertyIds().toArray()[i];
				if(!id.equals("password")){
					cells[i] = usr.get(id).toString();
				}else{
					cells[i] = "********";
				}
			}
			
			//Create CssLayout and adding a button
			CssLayout btnLayout = new CssLayout();
			cells[length - 1] = btnLayout;
			
			Button btn = new Button("edit", event -> navToEdit(usr));
			btn.setStyleName("quiet");
			btnLayout.addComponent(btn);
			
			t.addItem(cells, usr.getId());
		}
		
	}

	private void navToEdit(User usr) {
		manager.getDash().getTabNav().addView("EDITUSER", new EditUser(manager, usr));
		manager.getDash().getTabNav().navigateTo("EDITUSER");
	}

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		manager.ensureBase();
		populateTable();
		manager.closeBase();
	}

}
