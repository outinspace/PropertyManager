package com.wilsongateway.Forms;

import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.Model;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
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
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;

@SuppressWarnings("serial")
public abstract class EditForm<T extends EncryptedModel> extends Tab{

	private HorizontalLayout split;
	private FormLayout leftCol;
	private VerticalLayout rightCol;
	
	private Label heading;
	private Map<String, TextField> columnToTF;
	
	public enum Mode {ADD, EDIT, PERSONAL};
	protected Mode viewMode = Mode.EDIT;
	
	private T item;
	private String itemName;
	
	public EditForm(SessionManager manager, T item, String itemName) {
		super(item == null ? "Add " + itemName : "Account Settings", manager);
		this.item = item;
		this.itemName = itemName;

		setViewMode(item);
		
		this.setSizeFull();
		this.setHeightUndefined();
		
		heading = new Label(viewMode == Mode.ADD ? "Create A New " + itemName : 
			viewMode == Mode.EDIT ? "Edit " + itemName : "Account Settings");
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		split = new HorizontalLayout();
		split.setSpacing(true);
		split.setSizeFull();
		split.setHeightUndefined();
		addComponent(split);
		
		leftCol = new FormLayout();
		split.addComponent(leftCol);
		
		columnToTF = new HashMap<String, TextField>();
		
		createLeftCol(leftCol, item);
		if(viewMode != Mode.PERSONAL){
			rightCol = new VerticalLayout();
			split.addComponent(rightCol);
			
			createRightCol(rightCol, item);
		}
		
		if(item != null){
			fillFields(item);
		}
		
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		createLowerLayout();
	}
	
	protected void addAndFillTF(String columnName, String captionName, Resource icon){
		TextField temp = new TextField(captionName);
		if(icon != null){
			temp.setIcon(icon);
		}else{
			temp.setIcon(FontAwesome.QUESTION);
		}
		
		if(viewMode != Mode.ADD){
			temp.setValue(item.getAsString(columnName));
		}
		
		leftCol.addComponent(temp);
		columnToTF.put(columnName, temp);
	}
	
	protected String getTFValue(String column){
		return columnToTF.get(column).getValue();
	}
	
	protected void clearAllTF(){
		for(TextField tf : columnToTF.values()){
			tf.clear();
		}
	}

	private void createLowerLayout() {
		HorizontalLayout lowerBtns = new HorizontalLayout();
		lowerBtns.setSizeFull();
		lowerBtns.setHeightUndefined();
		addComponent(lowerBtns);
		
		if(viewMode == Mode.EDIT){
			Button deleteBtn = new Button("Delete " + itemName);
			deleteBtn.setStyleName("danger");
			lowerBtns.addComponent(deleteBtn);
			lowerBtns.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
			
			deleteBtn.addClickListener(new ClickListener(){

				@Override
				public void buttonClick(ClickEvent event) {
					manager.ensureBase();
					deleteBtnAction();	
					manager.closeBase();
				}
				
			});
		}
		
		
		Button saveBtn = new Button(viewMode == Mode.ADD ? "Create " + itemName : "Save Changes");
		saveBtn.setIcon(FontAwesome.SAVE);
		lowerBtns.addComponent(saveBtn);
		lowerBtns.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		
		saveBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				manager.ensureBase();
				saveBtnAction();
				manager.closeBase();
			}
		});
	}
	
	protected void deleteBtnAction(){
		//TODO are you sure?
		if(getItem().delete()){
			Notification.show(itemName + " Deleted", Notification.Type.HUMANIZED_MESSAGE);
			clearFields();
		}else{
			Notification.show(itemName + " Could Not Be Deleted", Notification.Type.ERROR_MESSAGE);
		}
	}
	
	protected T getItem(){
		return item;
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh combos options
		if(viewMode == Mode.ADD){
			manager.ensureBase();
			reloadData();
			manager.closeBase();
		}
	}
	
	protected abstract void saveBtnAction();
	protected abstract void clearFields();
	protected abstract void reloadData();
	protected abstract void setViewMode(T item);
	protected abstract void fillFields(T item);
	protected abstract void createLeftCol(FormLayout leftCol, T item);
	protected abstract void createRightCol(VerticalLayout rightCol, T item);
}
