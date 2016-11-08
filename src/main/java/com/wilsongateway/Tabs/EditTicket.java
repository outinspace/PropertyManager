package com.wilsongateway.Tabs;

import java.util.Date;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;

@SuppressWarnings("serial")
public class EditTicket extends EditForm<Ticket>{

	public EditTicket(SessionManager manager, Ticket item, boolean isEditable) {
		super(manager, item, "Ticket", isEditable);
	}

	@Override
	protected void saveBtnAction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void clearFields() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void reloadData() {
		// TODO Auto-generated method stub
		
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
	protected void fillFields(Ticket t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void populateLeftCol(FormLayout leftCol, Ticket t) {
		PopupDateField dateField = new PopupDateField();
		dateField.setCaption("Date");
		dateField.setValue(new Date());
		leftCol.addComponent(dateField);
		
		addAndFillTF("description", "Description");
		
		ComboBox statusField = new ComboBox();
		statusField.setCaption("Status");
		leftCol.addComponent(statusField);
		
		
	}

	@Override
	protected void populateRightCol(VerticalLayout rightCol, Ticket t) {
		addOneToManySelector(Property.class, Tables.PROPERTY, "Property", rightCol);
	}
}
