package com.wilsongateway.Deprecated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Deprecated.EditUserDep.Mode;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Exceptions.NoTabsSelectedException;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;

@SuppressWarnings("serial")
@Deprecated
public class EditGroupDep extends Tab{

	private HorizontalLayout split;
	private FormLayout leftCol;
	private VerticalLayout rightCol;
	
	private TextField nameTF;
	private EndlessComboBox<TabType> comboSelect;
	
	public enum Mode {ADD, EDIT};
	private Mode currentMode;
	private Group g;
	
	
	public EditGroupDep(SessionManager manager, Group g) {
		super("Add Group", manager);
		if(g == null){
			currentMode = Mode.ADD;
		}else{
			currentMode = Mode.EDIT;
		}
		this.g = g;
		
		this.setSizeFull();
		this.setHeightUndefined();
		
		addComponent(new Label(currentMode == Mode.ADD ? "Create A Group" : "Edit Group: " + g.getString("name")));
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		split = new HorizontalLayout();
		split.setSpacing(true);
		split.setSizeFull();
		split.setHeightUndefined();
		
		addComponent(split);
		createLeftCol();
		//createRightCol();
		
		fillFields(g);
		
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		HorizontalLayout lowerBtns = new HorizontalLayout();
		lowerBtns.setSizeFull();
		lowerBtns.setHeightUndefined();
		addComponent(lowerBtns);
		
		if(currentMode == Mode.EDIT){
			Button deleteBtn = new Button("Group User");
			deleteBtn.setStyleName("danger");
			lowerBtns.addComponent(deleteBtn);
			lowerBtns.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
			
			deleteBtn.addClickListener(new ClickListener(){

				@Override
				public void buttonClick(ClickEvent event) {
					//TODO are you sure?
					if(g.delete()){
						Notification.show("Group Deleted", Notification.Type.HUMANIZED_MESSAGE);
						clearFields();
					}else{
						Notification.show("Group Could Not Be Deleted", Notification.Type.ERROR_MESSAGE);
					}	
				}
				
			});
		}
		
		Button saveBtn = new Button(currentMode == Mode.ADD ? "Create Group" : "Save Group");
		saveBtn.setIcon(FontAwesome.SAVE);
		lowerBtns.addComponent(saveBtn);
		lowerBtns.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		
		saveBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				manager.ensureBase();
				
				try{
					//Checks tabs
					checkValidTabs();
					Group grp;
					if(currentMode == Mode.ADD){
						//Checks name is unique and saves
						grp = createAndSetGroup();
					}else{
						grp = g;
						if(!nameTF.getValue().equals(grp.getString("name"))){
							checkAndSetName();
						}
					}
					grp.set("tabs", comboSelect.getValues().toString());
					grp.save();
					
					Notification.show("Group Saved", Notification.Type.HUMANIZED_MESSAGE);
					if(currentMode == Mode.ADD){
						clearFields();
					}
				}catch(NameUnavailableException e){
					Notification.show("That Group Name Is Unavailable", Notification.Type.ERROR_MESSAGE);
				}catch(NoTabsSelectedException e){
					Notification.show("No Tabs Selected", Notification.Type.ERROR_MESSAGE);
				}catch(Exception e){
					Notification.show("Unable To Save Group", Notification.Type.ERROR_MESSAGE);
					e.printStackTrace();
				}
				manager.closeBase();
			}
			
		});
	}

	private void fillFields(Group g) {
		if(g == null){
			return;
		}
		
		nameTF.setValue(g.getString("name"));
		
	}

	private Group createAndSetGroup() throws NameUnavailableException{
		String name = nameTF.getValue().replace(",", "");
		Group g = Tables.GROUP.findFirst("name = (?)", name);
		if(g == null && name.length() > 0){
			g = Tables.GROUP.create("name", name);
			return g;
		}else{
			throw new NameUnavailableException();
		}
	}
	
	private void checkAndSetName() throws NameUnavailableException{
		String name = nameTF.getValue().replace(",", "");
		Group g = Tables.GROUP.findFirst("name = (?)", name);
		if(g != null && name.length() > 0){
			g.set("name", name);
		}else{
			throw new NameUnavailableException();
		}
	}
	
	private void checkValidTabs() throws NoTabsSelectedException{
		if(comboSelect.getValues() == null || comboSelect.getValues().size() == 0){
			throw new NoTabsSelectedException();
		}
	}

	private void createLeftCol() {
		leftCol = new FormLayout();
		split.addComponent(leftCol);
		
		nameTF = new TextField("Group Name");
		nameTF.setRequired(true);
		leftCol.addComponent(nameTF);
		
		comboSelect = new EndlessComboBox<TabType>("Select Tabs", new ArrayList<TabType>(Arrays.asList(TabType.values())), g == null ? null : g.getTabs());
		leftCol.addComponent(comboSelect);
		
		leftCol.addComponent(comboSelect);
	}
	
	private void createRightCol(){//TODO add desc
		rightCol = new VerticalLayout();
		split.addComponent(rightCol);
		
		rightCol.addComponent(new Label("<p>This is where tab documentation will eventually go...</p>",ContentMode.HTML));
	}
	
	private void clearFields() {
		nameTF.clear();
		comboSelect.clear();
	}

	@Override
	public void enter(ViewChangeEvent event) {}
}
