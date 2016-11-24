package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Client;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class ViewAllClients extends ViewAllForm{

	private boolean isEditable;
	
	public ViewAllClients(SessionManager manager, boolean isEditable) {
		super(manager, "Clients", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.CLIENT);
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

	@Override
	protected List<? extends Model> getModels() {
		return Tables.CLIENT.findAll();
	}
}
