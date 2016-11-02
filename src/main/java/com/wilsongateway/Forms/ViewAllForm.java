package com.wilsongateway.Forms;

import org.javalite.activejdbc.Model;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.wilsongateway.Deprecated.EditUserDep.Mode;
import com.wilsongateway.Framework.DashboardView;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Tabs.EditUser;

@SuppressWarnings("serial")
public abstract class ViewAllForm extends Tab{

	private Table t;
	private EncryptedModel model;
	
	public ViewAllForm(SessionManager manager, EncryptedModel model, String pluralItemName, boolean isEditable) {
		super((isEditable ? "Edit" : "View") + " All " + pluralItemName, manager);
		this.model = model;
		
		Label heading = new Label((isEditable ? "Edit" : "View") +  " All " + pluralItemName);
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		t = new Table(null);
		t.setWidth("100%");
		
		setContainerProperties(t);
		
		if(isEditable){
			t.addContainerProperty("", CssLayout.class, "");
		}
		
		populateTable();
		addComponent(t);
	}

	protected abstract void setContainerProperties(Table t);

	private void populateTable() {//TODO add column captions
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
				if(attribute.equals("password")){
					cells[i] = "********";
					//Add edit button if isEditable
				}else if(attribute.equals("")){
					CssLayout btnLayout = new CssLayout();
					cells[i] = btnLayout;
					
					Button btn = new Button("edit", event -> navToEdit(em));
					btn.setStyleName("quiet");
					btnLayout.addComponent(btn);
					//Set cell to get(cellName)
				}else{
					if(em.getAsString(attribute) == null){
						cells[i] = "";
					}else if(em.getDecrypted(attribute).toString().length() < 50){
						cells[i] = em.getDecrypted(attribute).toString();
					}else{
						cells[i] = em.getDecrypted(attribute).toString().substring(0, 50) + "...";
					}
				}
			}
			
			t.addItem(cells, em.getId());
		}
		
	}

	protected abstract void navToEdit(EncryptedModel usr);

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		manager.ensureBase();
		populateTable();
		manager.closeBase();
	}

}
