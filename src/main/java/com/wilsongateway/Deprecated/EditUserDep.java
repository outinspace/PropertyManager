package com.wilsongateway.Deprecated;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Exceptions.InvalidPasswordException;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
@Deprecated
public class EditUserDep extends Tab{

	private HorizontalLayout split;
	private FormLayout leftCol;
	private VerticalLayout rightCol;
	
	private Label heading;
	private TextField usernameTF;
	private TextField passwordTF;
	private TextField firstNameTF;
	private TextField lastNameTF;
	private TextField positionTF;
	private TextField aptPhoneTF;
	private TextField cellPhoneTF;
	private EndlessComboBox<Group> combos;
	
	public enum Mode {ADD, EDIT, PERSONAL};
	private Mode viewMode = Mode.EDIT;
	
	
	public EditUserDep(SessionManager manager, User u) {
		super(u == null ? "Add User" : "Account Settings", manager);

		if(u == null){
			viewMode = Mode.ADD;
		}else if(u.equals(manager.getCurrentUser())){
			viewMode = Mode.PERSONAL;
		}
		
		this.setSizeFull();
		this.setHeightUndefined();
		
		heading = new Label(viewMode == Mode.ADD ? "Create A New User" : 
			viewMode == Mode.EDIT ? "Edit User " + u.getUsername() : "Account Settings");
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		split = new HorizontalLayout();
		split.setSpacing(true);
		split.setSizeFull();
		split.setHeightUndefined();
		
		addComponent(split);
		createLeftCol();
		if(viewMode != Mode.PERSONAL){
			createRightCol(u);
		}
		
		fillFields(u);
		
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		HorizontalLayout lowerBtns = new HorizontalLayout();
		lowerBtns.setSizeFull();
		lowerBtns.setHeightUndefined();
		addComponent(lowerBtns);
		
		if(viewMode == Mode.EDIT){
			Button deleteBtn = new Button("Delete User");
			deleteBtn.setStyleName("danger");
			lowerBtns.addComponent(deleteBtn);
			lowerBtns.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
			
			deleteBtn.addClickListener(new ClickListener(){

				@Override
				public void buttonClick(ClickEvent event) {
					//TODO are you sure?
					if(u.delete()){
						Notification.show("User Deleted", Notification.Type.HUMANIZED_MESSAGE);
						clearFields();
					}else{
						Notification.show("User Could Not Be Deleted", Notification.Type.ERROR_MESSAGE);
					}	
				}
				
			});
		}
		
		
		Button saveBtn = new Button(viewMode == Mode.ADD ? "Create User" : "Save Changes");
		saveBtn.setIcon(FontAwesome.SAVE);
		lowerBtns.addComponent(saveBtn);
		lowerBtns.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		
		saveBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				manager.ensureBase();
				
				try{
					User newU;
					if(viewMode == Mode.ADD){
						newU = new User();
						newU.checkAndSetUsername(usernameTF.getValue());
						newU.checkAndSetPassword(passwordTF.getValue());
					}else{
						newU = u;
						if(!usernameTF.getValue().equals(newU.getUsername())){
							newU.checkAndSetUsername(usernameTF.getValue());
						}
					}
					
					newU.checkAndSetPassword(passwordTF.getValue());
					newU.set("first_name", firstNameTF.getValue());
					newU.set("last_name", lastNameTF.getValue());
					newU.set("position", positionTF.getValue());
					newU.set("apt_phone", aptPhoneTF.getValue());
					newU.set("cell_phone", cellPhoneTF.getValue());
					newU.save();
					
					if(viewMode != Mode.PERSONAL){
						interpretAndSetGroups(newU);
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
				manager.closeBase();
			}
		});
	}

	private void fillFields(User u) {
		if(u == null){
			return;
		}
		usernameTF.setValue(u.getUsername());
		passwordTF.setValue(u.getPassword());
		firstNameTF.setValue(u.getString("first_name"));
		lastNameTF.setValue(u.getString("last_name"));
		positionTF.setValue(u.getString("position"));
		aptPhoneTF.setValue(u.getString("apt_phone"));
		cellPhoneTF.setValue(u.getString("cell_phone"));
		//TODO switch to multiple combo boxes!
	}

	protected void interpretAndSetGroups(User u) {
		//Remove Groups
		for(Group g : u.getAll(Group.class)){
			if(!combos.getValues().contains(g)){
				u.remove(g);
			}
		}
		
		//Add new Groups
		for(Group g : combos.getValues()){
			if(g != null){
				u.addGroup(g);
			}
		}
	}

	private void createLeftCol() {
		leftCol = new FormLayout();
		split.addComponent(leftCol);
		
		usernameTF = new TextField("Username");
		usernameTF.setIcon(FontAwesome.USER);
		usernameTF.setRequired(true);
		leftCol.addComponent(usernameTF);
		
		passwordTF = new TextField("Password");
		passwordTF.setIcon(FontAwesome.LOCK);
		leftCol.addComponent(passwordTF);
		
		firstNameTF = new TextField("First Name");
		firstNameTF.setIcon(FontAwesome.QUESTION);
		leftCol.addComponent(firstNameTF);
		
		lastNameTF = new TextField("Last Name");
		lastNameTF.setIcon(FontAwesome.QUESTION);
		leftCol.addComponent(lastNameTF);
		
		positionTF = new TextField("Job Position");
		positionTF.setIcon(FontAwesome.QUESTION);
		leftCol.addComponent(positionTF);
		
		aptPhoneTF = new TextField("Apartment Phone");
		aptPhoneTF.setIcon(FontAwesome.HOME);
		leftCol.addComponent(aptPhoneTF);
		
		cellPhoneTF = new TextField("Cell Phone");
		cellPhoneTF.setIcon(FontAwesome.MOBILE_PHONE);
		leftCol.addComponent(cellPhoneTF);
	}
	
	private void createRightCol(User u){
		rightCol = new VerticalLayout();
		split.addComponent(rightCol);
		
		combos = new EndlessComboBox<Group>("Groups", Tables.GROUP.findAll(), (u == null) ? null : u.getAll(Group.class));
		rightCol.addComponent(combos);
		
		rightCol.addComponent(new Label("<p>Select groups from the left column and click >> to add this user"
				+ " to that group. Select groups from the right column and click << to remove this user from "
				+ "those groups.</p>",ContentMode.HTML));
	}

	protected void clearFields() {
		usernameTF.clear();
		passwordTF.clear();
		firstNameTF.clear();
		lastNameTF.clear();
		aptPhoneTF.clear();
		cellPhoneTF.clear();
		positionTF.clear();
		combos.clear();
	}

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh combos options
		if(viewMode == Mode.ADD){
			manager.ensureBase();
			combos.setOptions(Tables.GROUP.findAll());
			manager.closeBase();
		}
	}
}
