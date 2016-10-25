package com.wilsongateway.Tabs;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public class ViewAllProperties extends ViewAllForm{

	public ViewAllProperties(SessionManager manager, boolean isEditable) {
		super(manager, Tables.PROPERTY, "Properties", isEditable);
	}

	@Override
	protected void setContainerProperties(Table t) {
		t.addContainerProperty("name", String.class, "");
		t.addContainerProperty("street_address", String.class, "");
		t.addContainerProperty("city", String.class, "");
		t.addContainerProperty("state", String.class, "");
		t.addContainerProperty("zip", String.class, "");
	}

	@Override
	protected void navToEdit(Model ppt) {
		manager.getDash().getTabNav().addView("EDITPROPERTY", new EditProperty(manager, (Property) ppt));
		manager.getDash().getTabNav().navigateTo("EDITPROPERTY");
	}
}
