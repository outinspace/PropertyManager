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
public class ViewAllClients extends ViewAllForm{

	private static final long serialVersionUID = 7816615823531633204L;
	
	private boolean isEditable;
	
	public ViewAllClients(SessionManager manager, boolean isEditable) {
		super(manager, "Clients", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.CLIENT);
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("id", String.class, "ID", 0);
		addTableColumn("first_name", String.class, "First Name", 0);
		addTableColumn("last_name", String.class, "Last Name", 0);
		addTableColumn("unit", String.class, "Unit #", 0);
		
		addRelationshipColumn("properties", Property.class, "Located At", 1);
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
