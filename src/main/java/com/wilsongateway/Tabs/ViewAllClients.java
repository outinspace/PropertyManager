package com.wilsongateway.Tabs;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Client;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;

@SuppressWarnings("serial")
public class ViewAllClients extends ViewAllForm{

	private boolean isEditable;
	
	public ViewAllClients(SessionManager manager, boolean isEditable) {
		super(manager, Tables.CLIENT, "Clients", isEditable);
		this.isEditable = isEditable;
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("first_name", String.class, "First Name");
		addTableColumn("last_name", String.class, "Last Name");
		addTableColumn("unit", String.class, "Unit #");
		
		addRelationshipColumn("properties", Property.class, "Located At");
	}

	@Override
	protected void navToEdit(EncryptedModel c) {
		manager.getDash().getTabNav().addView("EDITCLIENT", new EditClient(manager, (Client) c, isEditable));
		manager.getDash().getTabNav().navigateTo("EDITCLIENT");
	}
}
