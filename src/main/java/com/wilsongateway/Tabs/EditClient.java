package com.wilsongateway.Tabs;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Client;
import com.wilsongateway.Framework.Tables.Property;

@SuppressWarnings("serial")
public class EditClient extends EditForm<Client>{

	private ComboBox propertyCB;
	private Client c;
	
	public EditClient(SessionManager manager, Client c, boolean isEditable) {
		super(manager, c, "Client", isEditable);
		this.c = c;
	}

	@Override
	protected void saveBtnAction() {
		try{
			if(c == null){
				c = new Client();
			}
			c.setEncrypted("first_name", getTFValue("first_name"));
			c.setEncrypted("last_name", getTFValue("last_name"));
			c.setEncrypted("unit", getTFValue("unit"));
			c.save();
			
			if(propertyCB.getValue() != null){
				Property p = (Property) propertyCB.getValue();
				p.add(c);
			}
			c.save();
			
			Notification.show("Client Saved", Notification.Type.HUMANIZED_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Save Client", Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	protected void clearFields() {
		clearAllTF();
		propertyCB.clear();
	}

	@Override
	protected void reloadData() {
		propertyCB.removeAllItems();
		propertyCB.addItems(Tables.PROPERTY.findAll());
	}

	@Override
	protected void setViewMode(Client c) {
		if(getItem() == null){
			viewMode = Mode.ADD;
		}else{
			viewMode = Mode.VIEW;
		}
	}

	@Override
	protected void fillFields(Client c) {
		
	}

	@Override
	protected void populateLeftCol(FormLayout leftCol, Client c) {
		addAndFillTF("first_name", "First Name", FontAwesome.USER);
		addAndFillTF("last_name", "Last Name", FontAwesome.USER);
		addAndFillTF("unit", "Unit #");
		
	}

	@Override
	protected void populateRightCol(VerticalLayout rightCol, Client c) {
		propertyCB = addOneToManySelector(Property.class, Tables.PROPERTY, "Located At",rightCol);
	}

}
