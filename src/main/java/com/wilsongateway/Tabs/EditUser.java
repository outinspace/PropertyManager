package com.wilsongateway.Tabs;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Exceptions.InvalidPasswordException;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public class EditUser extends EditForm<User>{

	private EndlessComboBox<Group> groupSelect;
	private EndlessComboBox<Property> propertySelect;
	
	public EditUser(SessionManager manager, User item, boolean isEditable) {
		super(manager, item, "User", isEditable);
	}

	@Override
	protected void saveBtnAction() {
		try{
			User newU;
			if(viewMode == Mode.ADD){
				newU = new User();
				newU.checkAndSetUsername(getTFValue("username"));
			}else{
				newU = getItem();
				if(!getTFValue("username").equals(newU.getUsername())){
					newU.checkAndSetUsername(getTFValue("username"));
				}
			}
			
			newU.checkAndSetPassword(getTFValue("password"));
			newU.setEncrypted("first_name", getTFValue("first_name"));
			newU.setEncrypted("last_name", getTFValue("last_name"));
			newU.setEncrypted("position", getTFValue("position"));
			newU.setEncrypted("apt_phone", getTFValue("apt_phone"));
			newU.setEncrypted("cell_phone", getTFValue("cell_phone"));
			newU.save();
			
			groupSelect.setManyToMany(newU, Tables.GROUP);
			propertySelect.setManyToMany(newU, Tables.PROPERTY);
			newU.save();
			
			Notification.show("User Saved", Notification.Type.HUMANIZED_MESSAGE);
		}catch(NameUnavailableException e){
			Notification.show("Username Is Taken", Notification.Type.WARNING_MESSAGE);
		}catch(InvalidPasswordException e){
			Notification.show("Password Is Not Acceptable", Notification.Type.WARNING_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Save User", Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	protected void clearFields() {
		clearAllTF();
		groupSelect.clear();
		propertySelect.clear();
	}

	@Override
	protected void reloadData() {
		groupSelect.setOptions(Tables.GROUP.findAll());
		propertySelect.setOptions(Tables.PROPERTY.findAll());
	}

	@Override
	protected void setViewMode(User u) {
		if(getItem() == null){
			viewMode = Mode.ADD;
		}else{
			viewMode = Mode.VIEW;
		}
	}

	@Override
	protected void fillFields(User u) {}

	@Override
	protected void populateLeftCol(FormLayout leftCol, User u) {
		addAndFillTF("username", "Username", FontAwesome.USER);
		addAndFillTF("password", "Password", FontAwesome.LOCK);
		addAndFillTF("first_name", "First Name", null);
		addAndFillTF("last_name", "Last Name", null);
		addAndFillTF("position", "Job Position", null);
		addAndFillTF("apt_phone", "Apartment Phone", FontAwesome.HOME);
		addAndFillTF("cell_phone", "Cell Phone", FontAwesome.MOBILE_PHONE);
	}

	@Override
	protected void populateRightCol(VerticalLayout rightCol, User u) {
		groupSelect = new EndlessComboBox<Group>("Groups", Tables.GROUP.findAll(), (u == null) ? null : u.getAll(Group.class));
		rightCol.addComponent(groupSelect);
		addCustomComponent(groupSelect);
		
		propertySelect = new EndlessComboBox<Property>("Properties", Tables.PROPERTY.findAll(), (u == null) ? null : u.getAll(Property.class));
		rightCol.addComponent(propertySelect);
		addCustomComponent(propertySelect);
	}

}
