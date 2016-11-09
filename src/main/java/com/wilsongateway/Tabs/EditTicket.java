package com.wilsongateway.Tabs;

import java.util.ArrayList;
import java.util.Date;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Client;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;
import com.wilsongateway.Framework.Tables.Ticket.Status;

@SuppressWarnings("serial")
public class EditTicket extends EditForm<Ticket>{

	private EndlessComboBox<Client> clientSelect;
	private ComboBox propertyCB;
	private TextArea descriptionArea;
	private PopupDateField dateField;
	private CheckBox completedField;
	
	private Ticket item;
	
	public EditTicket(SessionManager manager, Ticket item, boolean isEditable) {
		super(manager, item, "Ticket", isEditable);
		this.item = item;
	}

	@Override
	protected void saveBtnAction() {
		try{
			if(item == null){
				item = new Ticket();
			}
			item.setDate("date", dateField.getValue());
			item.setEncrypted("description", descriptionArea.getValue());
			item.setEncrypted("status", completedField.getValue() ? Status.COMPLETED.toString() : Status.PENDING.toString());
			item.save();
			
			clientSelect.setManyToMany(item, Tables.CLIENT);
			
			if(propertyCB.getValue() != null){
				Property p = (Property) propertyCB.getValue();
				p.add(item);
			}
			item.save();
			
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
		descriptionArea.clear();
		dateField.setValue(new Date());
		completedField.clear();
	}

	@Override
	protected void reloadData() {
		propertyCB.removeAllItems();
		propertyCB.addItems(Tables.PROPERTY.findAll());
	}

	@Override
	protected void setViewMode(Ticket t) {
		if(t != null){
			viewMode = Mode.VIEW;
		}else{
			viewMode = Mode.ADD;
		}
	}

	@Override
	protected void fillFields(Ticket t) {}

	@Override
	protected void populateLeftCol(Layout leftCol, Ticket t) {
		dateField = new PopupDateField();
		dateField.setCaption("Date");
		dateField.setValue(new Date());
		leftCol.addComponent(dateField);
		addCustomComponent(dateField);
		
		propertyCB = addOneToManySelector(Property.class, Tables.PROPERTY, "Property", leftCol);
		propertyCB.addValueChangeListener(new ValueChangeListener(){

			@Override
			public void valueChange(ValueChangeEvent event) {
				SessionManager.ensureBase();
				clientSelect.clear();
				if(propertyCB.getValue() == null){
					clientSelect.setOptions(new ArrayList<Client>());
				}else{
					clientSelect.setOptions(Tables.CLIENT.where("property_id = (?)", ((EncryptedModel)propertyCB.getValue()).getId()));
				}
			}
			
		});
		
		completedField = new CheckBox("Repair Completed");
		leftCol.addComponent(completedField);
		addCustomComponent(completedField);
	}

	@Override
	protected void populateRightCol(Layout rightCol, Ticket t) {
		clientSelect = new EndlessComboBox<Client>(new ArrayList<Client>(), (t == null) ? null : t.getAll(Client.class));
		clientSelect.setCaption("Clients Involved");
		rightCol.addComponent(clientSelect);
		addCustomComponent(clientSelect);
	}
	
	@Override
	protected void populateMiddleRow(Layout middleRow){
		descriptionArea = new TextArea("Description");
		descriptionArea.setWidth("100%");
		middleRow.addComponent(descriptionArea);
		addCustomComponent(descriptionArea);
		//TODO photo upload
	}
}
