package com.wilsongateway.Forms;

import java.util.ArrayList;
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
import com.vaadin.ui.Component;
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
	HorizontalLayout lowerBtns;
	Button editBtn;
	
	private Label heading;
	private Map<String, TextField> columnToTF = new HashMap<String, TextField>();
	private ArrayList<Component> customComponents = new ArrayList<Component>();
	
	public enum Mode {ADD, VIEW, EDIT};
	protected Mode viewMode = Mode.VIEW;
	
	private T item;
	private String itemName;
	
	public EditForm(SessionManager manager, T item, String itemName, boolean isEditable) {
		super("Add " + itemName, manager);
		this.item = item;
		this.itemName = itemName;
		
		setSizeFull();
		setHeightUndefined();
		setViewMode(item);
		
		heading = new Label();
		setHeading();
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		split = new HorizontalLayout();
		split.setSpacing(true);
		split.setSizeFull();
		split.setHeightUndefined();
		addComponent(split);
		
		leftCol = new FormLayout();
		split.addComponent(leftCol);
		populateLeftCol(leftCol, item);
		
		rightCol = new VerticalLayout();
		split.addComponent(rightCol);
		populateRightCol(rightCol, item);
		
		if(item != null){
			fillFields(item);
		}
		
		addComponent(new Label("<hr />", ContentMode.HTML));
		
		if(isEditable){
			if(viewMode == Mode.ADD){
				createLowerLayout();
				transitionView(Mode.ADD);
			}else{
				createEditBtn();
				createLowerLayout();
				transitionView(viewMode);
			}
		}else{
			for(Component c : customComponents){
				c.setEnabled(false);
			}
		}
	}
	
	private void createEditBtn() {
		editBtn = new Button("Edit");
		editBtn.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				transitionView(Mode.EDIT);
			}
			
		});
		addComponent(editBtn);
	}
	
	private void transitionView(Mode m){//TODO Normalize
		viewMode = m;
		switch (m){
			case VIEW:
				editBtn.setVisible(true);
				lowerBtns.setVisible(false);
				for(TextField tf : columnToTF.values()){
					tf.setEnabled(false);
				}
				for(Component c : customComponents){
					c.setEnabled(false);
				}
				break;
			case EDIT:
				editBtn.setVisible(false);
				lowerBtns.setVisible(true);
				for(TextField tf : columnToTF.values()){
					tf.setEnabled(true);
				}
				for(Component c : customComponents){
					c.setEnabled(true);
				}
				break;
			default:
				lowerBtns.setVisible(true);
				for(TextField tf : columnToTF.values()){
					tf.setEnabled(true);
				}
				for(Component c : customComponents){
					c.setEnabled(true);
				}
				clearFields();
				item = null;
		}
		setHeading();
	}
	
	private void setHeading(){//TODO change to 
		switch(viewMode){
		case ADD:
			heading.setValue("Create A New " + itemName);
			break;
		case EDIT:
			heading.setValue("Edit " + itemName);
			break;
		default:
			heading.setValue("View " + itemName);
		}
	}

	protected void addAndFillTF(String columnName, String captionName, Resource icon){
		TextField temp = new TextField(captionName);
		temp.setEnabled(false);
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
		lowerBtns = new HorizontalLayout();
		lowerBtns.setVisible(false);
		lowerBtns.setSizeFull();
		lowerBtns.setHeightUndefined();
		addComponent(lowerBtns);
		
		if(viewMode != Mode.ADD){
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
				if(viewMode == Mode.EDIT){
					transitionView(Mode.VIEW);
				}else if(viewMode == Mode.ADD){
					transitionView(Mode.ADD);
				}
			}
		});
	}
	
	protected void deleteBtnAction(){
		//TODO are you sure?
		if(getItem().delete()){
			Notification.show(itemName + " Deleted", Notification.Type.HUMANIZED_MESSAGE);
			clearFields();
			transitionView(Mode.ADD);
		}else{
			Notification.show(itemName + " Could Not Be Deleted", Notification.Type.ERROR_MESSAGE);
		}
	}
	
	protected T getItem(){
		return item;
	}
	
	protected void addCustomComponent(Component c){
		customComponents.add(c);
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
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
	protected abstract void populateLeftCol(FormLayout leftCol, T item);
	protected abstract void populateRightCol(VerticalLayout rightCol, T item);
}
