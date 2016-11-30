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

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class EditTicket extends EditForm<Ticket>{//TODO add URL property redirecting

	private static final long serialVersionUID = -3137430757686065445L;
	
	private EndlessComboBox<Client> clientSelect;
	private ComboBox propertyCB;
	private TextArea descriptionArea;
	private PopupDateField dateField;
	private CheckBox completedField;
	
	public EditTicket(SessionManager manager, Ticket t, boolean isEditable) {
		super(manager, t, "Ticket", isEditable);
		
		if(viewMode == Mode.ADD){
			completedField.setValue(Ticket.Status.valueOf(t.getAsString("status")) == Status.COMPLETED);
			descriptionArea.setValue(t.getAsString("description"));
		}
	}

	@Override
	protected void saveBtnAction(Ticket t) {
		try{
			if(t == null){
				t = new Ticket();
			}
			t.setDate("date", dateField.getValue());
			t.setEncrypted("description", descriptionArea.getValue());
			t.setEncrypted("status", completedField.getValue() ? Status.COMPLETED.toString() : Status.PENDING.toString());
			t.save();
			
			clientSelect.setManyToMany(t, Tables.CLIENT);
			
			if(propertyCB.getValue() != null){
				Property p = (Property) propertyCB.getValue();
				p.add(t);
			}
			t.save();
			
			Notification.show("Ticket Submitted", Notification.Type.HUMANIZED_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Submit Ticket", Notification.Type.ERROR_MESSAGE);
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
	protected void populateLeftCol(Layout leftCol, Ticket t) {
		dateField = new PopupDateField();
		dateField.setCaption("Date");
		dateField.setValue(new Date());
		dateField.setRequired(true);
		leftCol.addComponent(dateField);
		addCustomComponent(dateField);
		
		propertyCB = addOneToManySelector(Property.class, Tables.PROPERTY, "Property", leftCol);
		propertyCB.setRequired(true);
		propertyCB.addValueChangeListener(new ValueChangeListener(){

			private static final long serialVersionUID = -7529871905075864829L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				SessionManager.openBase();
				clientSelect.clear();
				if(propertyCB.getValue() == null){
					clientSelect.setOptions(new ArrayList<Client>());
				}else{
					clientSelect.setOptions(Tables.CLIENT.where("property_id = (?)", ((EncryptedModel)propertyCB.getValue()).getId()));
				}
				SessionManager.closeBase();
			}
			
		});
		
		completedField = new CheckBox("Repair Completed");
		leftCol.addComponent(completedField);
		addCustomComponent(completedField);
	}

	@Override
	protected void populateRightCol(Layout rightCol, Ticket t) {
		
		clientSelect = new EndlessComboBox<Client>(Tables.CLIENT.findAll(), (t == null) ? null : t.getAll(Client.class));
		clientSelect.setCaption("Clients Involved");
		rightCol.addComponent(clientSelect);
		addCustomComponent(clientSelect);
	}
	
	@Override
	protected void populateMiddleRow(Layout middleRow){
		descriptionArea = new TextArea("Description");
		descriptionArea.setWidth("100%");
		descriptionArea.setRequired(true);
		middleRow.addComponent(descriptionArea);
		addCustomComponent(descriptionArea);
	}
	
	@Override
	protected boolean checkRequiredFields(){
		if(propertyCB.getValue() == null){
			return false;
		}else if(descriptionArea.getValue().trim().equals("")){
			return false;
		}
		return true;
	}
}
