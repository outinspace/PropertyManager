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
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Tabs.EditGroup;

@SuppressWarnings("serial")
public class ViewAllGroupsDep extends Tab{

	private Table t;
	
	public ViewAllGroupsDep(SessionManager manager) {
		super("View All Groups", manager);
		
		t = new Table(null);
		t.setSizeUndefined();
		t.addContainerProperty("name", String.class, "");
		t.addContainerProperty("tabs", String.class, "");
		t.addContainerProperty("", CssLayout.class, "");
		
		populateTable();
		addComponent(t);
	}

	private void populateTable() {
		t.removeAllItems();
		
		//Iterate through all Groups
		for(Model m : Tables.GROUP.findAll()){
			Group grp = (Group)m;
			int length = t.getContainerPropertyIds().size();
			Object[] cells = new Object[length];
			
			//get column data from user using propertyIds
			for(int i = 0; i < length - 1; i++){
				String id = (String)t.getContainerPropertyIds().toArray()[i];
				cells[i] = grp.get(id).toString();
			}
			
			//Create CssLayout and adding a button
			CssLayout btnLayout = new CssLayout();
			cells[length - 1] = btnLayout;
			
			Button btn = new Button("edit", event -> navToEdit(grp));
			btn.setStyleName("quiet");
			btnLayout.addComponent(btn);
			
			t.addItem(cells, grp.getId());
		}
		
	}

	private void navToEdit(Group grp) {
		manager.getDash().getTabNav().addView("EDITGROUP", new EditGroup(manager, grp));
		manager.getDash().getTabNav().navigateTo("EDITGROUP");
	}

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		manager.ensureBase();
		populateTable();
		manager.closeBase();
	}

}
