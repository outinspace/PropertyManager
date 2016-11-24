package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.Model;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;

public class EditLocalTickets extends ViewAllForm{

	private static final long serialVersionUID = 2164419100750800569L;
	
	private ComboBox propCB;
	
	private List<Property> properties;
	private Property selectedProperty;
	
	public EditLocalTickets(SessionManager manager) {
		super(manager, "Local Tickets", true);
		SessionManager.openBase();
		
		
		
		this.addRefreshButton();
		this.addReportBtn(Tables.TICKET);//Individualize
		createTopBar();
		
		SessionManager.closeBase();
	}
	
	protected void createTopBar(){//messy
		getTopBar().addComponent(new Label("Property:"));
		
		propCB = new ComboBox();
		propCB.addItems(manager.getCurrentUser().getAll(Property.class));
		propCB.setStyleName("tiny");
		getTopBar().addComponent(propCB);
		
		propCB.addValueChangeListener(e -> {
			selectedProperty = (Property) propCB.getValue();
			refresh();
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
	
	@Override 
	protected void reloadData(){
		SessionManager.openBase();
		propCB.removeAllItems();
		propCB.addItems(manager.getCurrentUser().getAll(Property.class));
		SessionManager.closeBase();
	}

	@Override
	protected List<? extends Model> getModels() {
		if(properties == null){
			properties = manager.getCurrentUser().getAll(Property.class);
		}
		
		if(selectedProperty != null){
			//Load selected property
			return selectedProperty.getAll(Ticket.class);
		}else{
			return Tables.TICKET.find("id = (?)", -1);
		}
	}

}
