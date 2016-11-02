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
	
	public EditUser(SessionManager manager, User item) {
		super(manager, item, "User");
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
			newU.set("first_name", getTFValue("first_name"));
			newU.set("last_name", getTFValue("last_name"));
			newU.set("position", getTFValue("position"));
			newU.set("apt_phone", getTFValue("apt_phone"));
			newU.set("cell_phone", getTFValue("cell_phone"));
			newU.save();
			
			if(viewMode != Mode.PERSONAL){
				//interpretAndSetGroups(newU);
				groupSelect.setManyToMany(newU, Tables.GROUP);
				propertySelect.setManyToMany(newU, Tables.PROPERTY);
				newU.save();
			}
			
			Notification.show("User Saved", Notification.Type.HUMANIZED_MESSAGE);
			if(viewMode == Mode.ADD){
				clearFields();
			}
		}catch(NameUnavailableException e){
			Notification.show("Username Is Taken", Notification.Type.WARNING_MESSAGE);
		}catch(InvalidPasswordException e){
			Notification.show("Password Is Not Acceptable", Notification.Type.WARNING_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Save User", Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
//	private void interpretAndSetGroups(User u) {
//		//Remove Groups
//		for(Group g : u.getAll(Group.class)){
//			if(!groupSelect.getValues().contains(g)){
//				u.remove(g);
//			}
//		}
//		
//		//Add new Groups
//		for(Group g : groupSelect.getValues()){
//			if(g != null){
//				u.addGroup(g);
//			}
//		}
//	}
//	
//	private void interpretAndSetProperties(User u) {
//		//Remove Props
//		for(Property p : u.getAll(Property.class)){
//			if(!propertySelect.getValues().contains(p)){
//				u.remove(p);
//			}
//		}
//		
//		//Add new Props
//		for(Property p : propertySelect.getValues()){
//			if(p != null){
//				if(!u.getAll(Property.class).contains(p)){
//					u.add(p);
//				}
//			}
//		}
//	}

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
		if(u == null){
			viewMode = Mode.ADD;
		}else if(u.equals(manager.getCurrentUser())){
			viewMode = Mode.PERSONAL;
		}
	}

	@Override
	protected void fillFields(User u) {}

	@Override
	protected void createLeftCol(FormLayout leftCol, User u) {
		addAndFillTF("username", "Username", FontAwesome.USER);
		addAndFillTF("password", "Password", FontAwesome.LOCK);
		addAndFillTF("first_name", "First Name", null);
		addAndFillTF("last_name", "Last Name", null);
		addAndFillTF("position", "Job Position", null);
		addAndFillTF("apt_phone", "Apartment Phone", FontAwesome.HOME);
		addAndFillTF("cell_phone", "Cell Phone", FontAwesome.MOBILE_PHONE);
	}

	@Override
	protected void createRightCol(VerticalLayout rightCol, User u) {
		groupSelect = new EndlessComboBox<Group>("Groups", Tables.GROUP.findAll(), (u == null) ? null : u.getAll(Group.class));
		rightCol.addComponent(groupSelect);
		
		propertySelect = new EndlessComboBox<Property>("Properties", Tables.PROPERTY.findAll(), (u == null) ? null : u.getAll(Property.class));
		rightCol.addComponent(propertySelect);
	}

}
