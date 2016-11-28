package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;
import com.wilsongateway.SubPanels.TicketActionMenu;

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
		
		if(manager.getCurrentUser().getAll(Ticket.class).size() > 0){
			propCB.setValue(manager.getCurrentUser().getAll(Ticket.class).get(0));
			refresh();
		}
		
		SessionManager.closeBase();
	}
	
	protected void createTopBar(){
		getTopBar().addComponent(new Label("Property:"));
		
		propCB = new ComboBox();
		propCB.addItems(properties);
		propCB.setStyleName("tiny");
		getTopBar().addComponent(propCB);
		
		propCB.addValueChangeListener(e -> {
			selectedProperty = (Property) propCB.getValue();
			refresh();
		});
	}

	@Override
	protected void navToEdit(EncryptedModel usr) {}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("id", String.class, "ID", 0);
		addTableColumn("date", String.class, "Date", 0);
		addTableColumn("status", String.class, "Status", 0);
		addRelationshipColumn("property", Property.class, "Property", 0);
		addTableColumn("description", String.class, "Description", 1);
	}
	
	@Override 
	protected void reloadData(){
		SessionManager.openBase();
		propCB.removeAllItems();
		propCB.addItems(manager.getCurrentUser().getAll(Property.class));
		SessionManager.closeBase();
	}

	@Override
	protected LazyList<? extends Model> getModels() {
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
	
	@Override
	protected void fillActionLayout(Layout actionLayout, EncryptedModel em){
		PopupView actionMenu = new PopupView("More", new TicketActionMenu(manager, (Ticket)em));
		actionLayout.addComponent(actionMenu);
	}
}
