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
public class ViewAllProperties extends ViewAllForm{

	private static final long serialVersionUID = 4716418732014493355L;
	
	private boolean isEditable;
	public ViewAllProperties(SessionManager manager, boolean isEditable) {
		super(manager, "Properties", isEditable);
		this.isEditable = isEditable;
		
		this.addRefreshButton();
		this.addReportBtn(Tables.PROPERTY);
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("id", String.class, "ID", 0);
		addTableColumn("name", String.class, "Name", 0);
		addTableColumn("street_address", String.class, "Address", 1);
		addTableColumn("city", String.class, "City", 0);
		addTableColumn("State", String.class, "state", 0);
		addTableColumn("zip", String.class, "Zip", 0);
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
