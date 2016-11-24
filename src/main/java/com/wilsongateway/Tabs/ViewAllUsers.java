package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class ViewAllUsers extends ViewAllForm{

	private boolean isEditable;
	
	public ViewAllUsers(SessionManager manager, boolean isEditable) {
		super(manager, "Users", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.USER);
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("username", String.class, "Username");
		addTableColumn("first_name", String.class, "First Name");
		addTableColumn("last_name", String.class, "Last Name");
		
		addRelationshipColumn("groups", Group.class, "Groups");
		addRelationshipColumn("properties", Property.class, "Properties");
	}

	@Override
	protected void navToEdit(EncryptedModel usr) {
		manager.getDash().getTabNav().addView("EDITUSER", new EditUser(manager, (User) usr, isEditable));
		manager.getDash().getTabNav().navigateTo("EDITUSER");
	}

	@Override
	protected List<? extends Model> getModels() {
		return Tables.USER.findAll();
	}
}