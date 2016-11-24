package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class ViewAllGroups extends ViewAllForm{

	private boolean isEditable;
	
	public ViewAllGroups(SessionManager manager, boolean isEditable) {
		super(manager, "Groups", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.GROUP);
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("id", String.class, "ID", 0);
		addTableColumn("name", String.class, "Name", 0);
		addTableColumn("tabs", String.class, "Tabs", 1);
	}

	@Override
	protected void navToEdit(EncryptedModel grp) {
		manager.getDash().getTabNav().addView("EDITGROUP", new EditGroup(manager, (Group) grp, isEditable));
		manager.getDash().getTabNav().navigateTo("EDITGROUP");
	}

	@Override
	protected List<? extends Model> getModels() {
		return Tables.GROUP.findAll();
	}
}
