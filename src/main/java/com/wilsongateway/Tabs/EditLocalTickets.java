package com.wilsongateway.Tabs;

import java.util.ArrayList;
import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;

public class EditLocalTickets extends ViewAllForm{

	private static final long serialVersionUID = 2164419100750800569L;
	private List<Property> properties;
	
	public EditLocalTickets(SessionManager manager) {
		super(manager, new ArrayList<Model>(), "Local Tickets", true);
		
		SessionManager.openBase();
		properties = manager.getCurrentUser().getAll(Property.class);
		
		if(properties.size() > 0){
			this.models = properties.get(0).getAll(Ticket.class);
			populateTable();
		}
		
		SessionManager.closeBase();
	}
	
	@Override
	protected void insertTopGui(){//messy
		if(manager.getCurrentUser().getAll(Property.class).size() < 2){
			return;
		}
		
		ComboBox propCB = new ComboBox("Property");
		propCB.addItems(manager.getCurrentUser().getAll(Property.class));
		addComponent(propCB);
		
		propCB.addValueChangeListener(e -> {
			models = ((Property) propCB.getValue()).getAll(Ticket.class);
			populateTable();
		});
	}

	@Override
	protected void navToEdit(EncryptedModel usr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("date", String.class, "Date");
		addTableColumn("status", String.class, "Status");
		addTableColumn("description", String.class, "Description");
	}

}
