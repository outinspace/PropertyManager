package com.wilsongateway.Forms;

import java.util.HashMap;
import java.util.Map;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.associations.NotAssociatedException;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

@SuppressWarnings("serial")
public abstract class ViewAllForm extends Tab{

	private Table t;
	private EncryptedModel model;
	private Map<String, Class<? extends EncryptedModel>> relationshipColumns = new HashMap<String, Class<? extends EncryptedModel>>();
	
	public static final String VIEW = "view";
	public static final String PASSWORD = "password";
	public static final String RELATIONSHIP = "relationship";
	
	public ViewAllForm(SessionManager manager, EncryptedModel model, String pluralItemName, boolean isEditable) {
		super((isEditable ? "Edit" : "View") + " All " + pluralItemName, manager);
		this.model = model;
		
		Label heading = new Label((isEditable ? "Edit" : "View") +  " All " + pluralItemName);
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		t = new Table(null);
		t.setWidth("100%");
		
		setContainerProperties(t);
		t.addContainerProperty(VIEW, CssLayout.class, "");
		
		populateTable();
		addComponent(t);
	}

	private void populateTable() {
		t.removeAllItems();
		//Iterate through all users
		for(Model m : model.findAll()){
			EncryptedModel em = (EncryptedModel)m;
			int length = t.getContainerPropertyIds().size();
			Object[] cells = new Object[length];
			
			//get column data from user using propertyIds
			for(int i = 0; i < length; i++){
				String attribute = (String)t.getContainerPropertyIds().toArray()[i];
				//Hide password
				if(attribute.equals(PASSWORD)){
					cells[i] = "********";
				}else if(attribute.equals(VIEW)){
					//Add edit button if isEditable
					CssLayout btnLayout = new CssLayout();
					cells[i] = btnLayout;
					
					Button btn = new Button("view", event -> navToEdit(em));
					btn.setStyleName("quiet");
					btnLayout.addComponent(btn);
				}else if(relationshipColumns.containsKey(attribute)){
					try{
						cells[i] = m.getAll(relationshipColumns.get(attribute)).toString().replace("[", "").replace("]", "");
					}catch(NotAssociatedException e){
						EncryptedModel parent =  m.parent(relationshipColumns.get(attribute));
						cells[i] = parent == null ? "" : parent.toString();
					}
				}else{
					cells[i] = em.getAsString(attribute) == null ? "" : em.getDecrypted(attribute).toString();
				}
				
				//Truncate
				if(cells[i] instanceof String && cells[i].toString().length() > 50){
					cells[i] = cells[i].toString().substring(0, 50) + "...";
				}
			}
			
			t.addItem(cells, em.getId());
		}
	}
	
	protected <T> void addTableColumn(String attribute, Class<T> type, String caption){
		t.addContainerProperty(attribute, type, "", caption, null, null);
	}
	
	protected void addRelationshipColumn(String identifier, Class<? extends EncryptedModel> type, String caption){
		//Finds all many to many or one to many relationships of model and type.class
		relationshipColumns.put(identifier, type);
		t.addContainerProperty(identifier, String.class, "", caption, null, null);
	}

	protected abstract void navToEdit(EncryptedModel usr);
	protected abstract void setContainerProperties(Table t);

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		manager.ensureBase();
		populateTable();
		manager.closeBase();
	}

}
