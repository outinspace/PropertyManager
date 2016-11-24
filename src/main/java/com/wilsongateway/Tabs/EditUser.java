package com.wilsongateway.Tabs;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Exceptions.InvalidPasswordException;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Validators.PasswordValidator;
import com.wilsongateway.Validators.UniqueUsernameValidator;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
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
			}else{
				newU = getItem();
			}
			newU.setEncrypted("username", getTFValue("username"));
			
			if(!getTFValue("password").equals(EncryptedModel.PASSWORDFILLER)){
				newU.setEncrypted("password", getTFValue("password"));
			}
			
			newU.setEncrypted("first_name", getTFValue("first_name"));
			newU.setEncrypted("last_name", getTFValue("last_name"));
			newU.setEncrypted("position", getTFValue("position"));
			newU.setEncrypted("work_phone", getTFValue("work_phone"));
			newU.setEncrypted("cell_phone", getTFValue("cell_phone"));
			newU.save();
			
			groupSelect.setManyToMany(newU, Tables.GROUP);
			propertySelect.setManyToMany(newU, Tables.PROPERTY);
			newU.save();
			
			Notification.show("User Saved", Notification.Type.HUMANIZED_MESSAGE);
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
	protected void populateLeftCol(Layout leftCol, User u) {
		TextField userTF = addAndFillTF("username", "Username", FontAwesome.USER);
		userTF.setRequired(true);
		userTF.addValidator(new UniqueUsernameValidator(userTF.getValue()));
		
		TextField passTF = addAndFillTF("password", "Password", FontAwesome.LOCK);
		passTF.setRequired(true);
		passTF.addValidator(new PasswordValidator());
		
		addAndFillTF("first_name", "First Name");
		addAndFillTF("last_name", "Last Name");
		addAndFillTF("position", "Job Position");
		addAndFillTF("work_phone", "Work Phone", FontAwesome.HOME);
		addAndFillTF("cell_phone", "Cell Phone", FontAwesome.MOBILE_PHONE);
	}

	@Override
	protected void populateRightCol(Layout rightCol, User u) {
		groupSelect = new EndlessComboBox<Group>(Tables.GROUP.findAll(), (u == null) ? null : u.getAll(Group.class));
		groupSelect.setCaption("Groups");
		rightCol.addComponent(groupSelect);
		addCustomComponent(groupSelect);
		
		propertySelect = new EndlessComboBox<Property>(Tables.PROPERTY.findAll(), (u == null) ? null : u.getAll(Property.class));
		propertySelect.setCaption("Properties");
		rightCol.addComponent(propertySelect);
		addCustomComponent(propertySelect);
	}
}
