package com.wilsongateway.Tabs;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public class ViewAllUsers extends ViewAllForm{

	public ViewAllUsers(SessionManager manager, boolean isEditable) {
		super(manager, Tables.USER, "Users", isEditable);
	}

	@Override
	protected void setContainerProperties(Table t) {
		t.addContainerProperty("username", String.class, "");
		t.addContainerProperty("password", String.class, "");
		t.addContainerProperty("first_name", String.class, "");
		t.addContainerProperty("last_name", String.class, "");
		t.addContainerProperty("groups", String.class, "");
	}

	@Override
	protected void navToEdit(EncryptedModel usr) {
		manager.getDash().getTabNav().addView("EDITUSER", new EditUser(manager, (User) usr));
		manager.getDash().getTabNav().navigateTo("EDITUSER");
	}
}
