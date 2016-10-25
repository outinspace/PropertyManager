package com.wilsongateway.Tabs;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public class ViewAllGroups extends ViewAllForm{

	public ViewAllGroups(SessionManager manager, boolean isEditable) {
		super(manager, Tables.GROUP, "Groups", isEditable);
	}

	@Override
	protected void setContainerProperties(Table t) {
		t.addContainerProperty("name", String.class, "");
		t.addContainerProperty("tabs", String.class, "");
		
	}

	@Override
	protected void navToEdit(Model grp) {
		manager.getDash().getTabNav().addView("EDITGROUP", new EditGroup(manager, (Group) grp));
		manager.getDash().getTabNav().navigateTo("EDITGROUP");
	}
}
