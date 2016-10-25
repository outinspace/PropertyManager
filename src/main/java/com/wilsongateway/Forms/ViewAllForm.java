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
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Tabs.EditUser;

@SuppressWarnings("serial")
public abstract class ViewAllForm extends Tab{

	private Table t;
	private Model model;
	
	public ViewAllForm(SessionManager manager, Model model, String pluralItemName, boolean isEditable) {
		super((isEditable ? "Edit" : "View") + " All " + pluralItemName, manager);
		this.model = model;
		
		Label heading = new Label((isEditable ? "Edit" : "View") +  " All " + model.getClass().getSimpleName() + "s");
		
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

	private void populateTable() {
		t.removeAllItems();
		//Iterate through all users
		for(Model m : model.findAll()){
			
			int length = t.getContainerPropertyIds().size();
			Object[] cells = new Object[length];
			
			//get column data from user using propertyIds
			for(int i = 0; i < length; i++){
				String id = (String)t.getContainerPropertyIds().toArray()[i];
				//Hide password
				if(id.equals("password")){
					cells[i] = "********";
					//Add edit button if isEditable
				}else if(id.equals("")){
					CssLayout btnLayout = new CssLayout();
					cells[i] = btnLayout;
					
					Button btn = new Button("edit", event -> navToEdit(m));
					btn.setStyleName("quiet");
					btnLayout.addComponent(btn);
					//Set cell to get(cellName)
				}else{
					if(m.get(id) == null){
						cells[i] = "";
					}else if(m.get(id).toString().length() < 50){
						cells[i] = m.get(id).toString();
					}else{
						cells[i] = m.get(id).toString().substring(0, 50) + "...";
					}
				}
			}
			
			t.addItem(cells, m.getId());
		}
		
	}

	protected abstract void navToEdit(Model usr);

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		manager.ensureBase();
		populateTable();
		manager.closeBase();
	}

}
