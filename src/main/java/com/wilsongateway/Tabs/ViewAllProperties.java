package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class ViewAllProperties extends ViewAllForm{

	private boolean isEditable;
	public ViewAllProperties(SessionManager manager, boolean isEditable) {
		super(manager, "Properties", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.PROPERTY);
	}

	@Override
	protected void setContainerProperties(Table t) {
		t.addContainerProperty("name", String.class, "", "Name", null, null);
		t.addContainerProperty("street_address", String.class, "", "Addresss", null, null);
		t.addContainerProperty("city", String.class, "", "City", null, null);
		t.addContainerProperty("state", String.class, "", "State", null, null);
		t.addContainerProperty("zip", String.class, "", "Zip", null, null);
	}

	@Override
	protected void navToEdit(EncryptedModel ppt) {
		manager.getDash().getTabNav().addView("EDITPROPERTY", new EditProperty(manager, (Property) ppt, isEditable));
		manager.getDash().getTabNav().navigateTo("EDITPROPERTY");
	}

	@Override
	protected List<? extends Model> getModels() {
		return Tables.PROPERTY.findAll();
	}
}
