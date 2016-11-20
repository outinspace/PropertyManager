package com.wilsongateway.Tabs;

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
		super(manager, Tables.GROUP.findAll(), "Groups", isEditable);
		this.isEditable = isEditable;
		
		addReportBtn(Tables.GROUP, "Generate Report");
	}

	@Override
	protected void setContainerProperties(Table t) {
		t.addContainerProperty("name", String.class, "", "Name", null, null);
		t.addContainerProperty("tabs", String.class, "", "Tabs", null, null);
		
	}

	@Override
	protected void navToEdit(EncryptedModel grp) {
		manager.getDash().getTabNav().addView("EDITGROUP", new EditGroup(manager, (Group) grp, isEditable));
		manager.getDash().getTabNav().navigateTo("EDITGROUP");
	}
}
