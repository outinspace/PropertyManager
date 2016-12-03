package com.wilsongateway.Forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Model;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
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
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 * @param <T> Type of EncryptedModel that an object of this class will be editing.
 */
public abstract class EditForm<T extends EncryptedModel> extends Tab{

	private static final long serialVersionUID = -4723363020095122718L;
	
	private HorizontalLayout split;
	private FormLayout leftCol;
	private FormLayout rightCol;
	private HorizontalLayout lowerBtns;
	private Button editBtn;
	
	private Label heading;
	private Map<String, Field> columnToTF = new HashMap<String, Field>();
	private List<Component> customComponents = new ArrayList<Component>();
	
	public enum Mode {ADD, VIEW, EDIT};
	protected Mode viewMode = Mode.VIEW;
	
	private T item;
	private String itemName;
	
	public EditForm(SessionManager manager, T item, String itemName, boolean isEditable) {
		super("Add " + itemName, manager);
		this.item = item;
		this.itemName = itemName;
		SessionManager.openBase();
		
		setSizeFull();
		setHeightUndefined();
		setViewMode(item);
		
		heading = new Label();
		setHeading();
		
		addComponent(heading);
		addLineBreak();
		
		split = new HorizontalLayout();
		split.setSpacing(true);
		split.setSizeFull();
		split.setHeightUndefined();
		addComponent(split);
		
		leftCol = new FormLayout();
		split.addComponent(leftCol);
		populateLeftCol(leftCol, item);
		
		rightCol = new FormLayout();
		split.addComponent(rightCol);
		populateRightCol(rightCol, item);
		
		populateMiddleRow(this);
		
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
		SessionManager.closeBase();
	}

	private void createEditBtn() {
		editBtn = new Button("Edit", e -> transitionView(Mode.EDIT));
		addComponent(editBtn);
		setComponentAlignment(editBtn, Alignment.MIDDLE_RIGHT);
	}
	
	private void transitionView(Mode m){
		viewMode = m;
		switch (m){
			case VIEW:
				editBtn.setVisible(true);
				lowerBtns.setVisible(false);
				setAllComponentsEnabled(false);
				break;
			case EDIT:
				editBtn.setVisible(false);
				lowerBtns.setVisible(true);
				setAllComponentsEnabled(true);
				break;
			default:
				lowerBtns.setVisible(true);
				setAllComponentsEnabled(true);
				clearFields();
				item = null;
		}
		setHeading();
	}
	
	private void setHeading(){
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

	protected TextField addAndFillTF(String columnName, String captionName, Resource icon, Layout layout){
		TextField temp = new TextField(captionName);
		temp.setEnabled(false);
		temp.setIcon(icon);
		
		if(viewMode != Mode.ADD){
			temp.setValue(item.getAsString(columnName));
		}
		
		layout.addComponent(temp);
		columnToTF.put(columnName, temp);
		return temp;
	}
	
	protected TextField addAndFillTF(String columnName, String captionName){
		return addAndFillTF(columnName, captionName, FontAwesome.QUESTION, leftCol);
	}
	
	protected TextField addAndFillTF(String columnName, String captionName, Resource icon){
		return addAndFillTF(columnName, captionName, icon, leftCol);
	}
	
	//TODO make custom component
	protected <J extends EncryptedModel> ComboBox addOneToManySelector(Class<J> parentClass, J staticModel, String caption, Layout layout){
		List<J> options  = staticModel.findAll();
		
		ComboBox temp = new ComboBox(caption);
		temp.addItems(options);
		
		try{
			J parent = item.parent(parentClass);
			
			for(J option : options){
				if(parent != null && parent.equals(option)){
					temp.setValue(option);
					break;
				}
			}
		}catch(NullPointerException e){
			temp.setValue(null);
		}
		
		layout.addComponent(temp);
		addCustomComponent(temp);
		return temp;
	}
	
	protected String getTFValue(String column){
		return columnToTF.get(column).getValue().toString();
	}
	
	protected void clearAllTF(){
		for(Field f : columnToTF.values()){
			f.clear();
		}
	}
	
	private void setAllComponentsEnabled(boolean value){
		Iterator<Component> leftIter = leftCol.iterator();
		while(leftIter.hasNext()){
			leftIter.next().setEnabled(value);
		}
		
		Iterator<Component> rightIter = rightCol.iterator();
		while(rightIter.hasNext()){
			rightIter.next().setEnabled(value);
		}
//		for(TextField tf : columnToTF.values()){
//			tf.setEnabled(value);
//		}
//		for(Component c : customComponents){
//			c.setEnabled(value);
//		}
	}

	private void createLowerLayout() {
		lowerBtns = new HorizontalLayout();
		lowerBtns.setVisible(false);
		lowerBtns.setSizeFull();
		lowerBtns.setHeightUndefined();
		addComponent(lowerBtns);
		
		if(viewMode != Mode.ADD){
			Button deleteBtn = new Button("Delete " + itemName, e -> deleteBtnAction());
			deleteBtn.setStyleName("danger");
			lowerBtns.addComponent(deleteBtn);
			lowerBtns.setComponentAlignment(deleteBtn, Alignment.MIDDLE_LEFT);
		}
		
		
		Button saveBtn = new Button(viewMode == Mode.ADD ? "Create " + itemName : "Save Changes");
		saveBtn.setIcon(FontAwesome.SAVE);
		lowerBtns.addComponent(saveBtn);
		lowerBtns.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		
		saveBtn.addClickListener(e -> {
			if(validateFields()){
				SessionManager.openBase();
				saveModel(item);
				SessionManager.closeBase();
				if(viewMode == Mode.EDIT){
					transitionView(Mode.VIEW);
				}else if(viewMode == Mode.ADD){
					transitionView(Mode.ADD);
				}
			}
		});
	}
	
	private boolean validateFields(){//TODO Validate collection<Field>
		try{
			validateLayout(leftCol);
			validateLayout(rightCol);
			validateLayout(this);
			return true;
		}catch(InvalidValueException e){
			return false;
		}
	}
	
	private void validateLayout(Layout layout) throws InvalidValueException{
		Iterator<Component> iterator = layout.iterator();
		while(iterator.hasNext()){
			Component c = iterator.next();
			if(c instanceof Field){
				Field<?> f = (Field<?>)c;
				f.validate();
			}
		}
	}

	private void deleteBtnAction(){
		ConfirmDialog.show(manager, "Please Confirm:", "Are you sure?", 
				"Yes", "No", (dialog) -> {
					SessionManager.openBase();
	                if (dialog.isConfirmed()) {
	                	if(item.delete()){
	                		Notification.show(itemName + " Deleted", Notification.Type.HUMANIZED_MESSAGE);
		        			clearFields();
		        			manager.getDash().navigateBack();
	                	}else{
	                		Notification.show(itemName + " Could Not Be Deleted", Notification.Type.ERROR_MESSAGE);
	                	}
	                }
	                SessionManager.closeBase();
				});
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		if(viewMode == Mode.ADD){
			SessionManager.openBase();
			reloadData();
			SessionManager.closeBase();
		}
	}
	
	protected void addCustomComponent(Component c){
		customComponents.add(c);
	}
	
	//For purpose of overriding as necessary
	protected void populateMiddleRow(Layout middleRow) {}
	protected void populateLeftCol(Layout leftCol, T item){}
	protected void populateRightCol(Layout rightCol, T item){}
	
	protected abstract void saveModel(T item);
	protected abstract void clearFields();
	protected abstract void reloadData();
	protected abstract void setViewMode(T item);
}
