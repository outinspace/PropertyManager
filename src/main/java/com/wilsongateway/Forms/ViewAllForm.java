package com.wilsongateway.Forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.associations.NotAssociatedException;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.CSVController;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public abstract class ViewAllForm extends Tab{

	private Table t;
	private List<Model> models;
	private Map<String, Class<? extends EncryptedModel>> relationshipColumns = new HashMap<String, Class<? extends EncryptedModel>>();
	
	public static final String ACTIONCOMPONENTS = "actionComponents";
	public static final String RELATIONSHIP = "relationship";
	
	public ViewAllForm(SessionManager manager, List<Model> models, String pluralItemName, boolean isEditable) {
		super((isEditable ? "Edit" : "View") + " All " + pluralItemName, manager);
		this.models = models;
		SessionManager.openBase();
		
		Label heading = new Label((isEditable ? "Edit" : "View") +  " All " + pluralItemName);
		
		addComponent(heading);
		addLineBreak();
		
		t = new Table(null);
		t.setWidth("100%");
		//TODO Search Box
		t.addContainerProperty(ACTIONCOMPONENTS, CssLayout.class, "", "", null, null);
		t.setColumnExpandRatio(ACTIONCOMPONENTS, 0);
		setContainerProperties(t);
		
		populateTable();
		addComponent(t);
		
		SessionManager.closeBase();
	}

	private void populateTable() {
		t.removeAllItems();
		
		//Iterate through all models
		for(Model m : models){
			EncryptedModel em = (EncryptedModel)m;
			int length = t.getContainerPropertyIds().size();
			Object[] cells = new Object[length];
			
			//Get column data from user using propertyIds
			for(int i = 0; i < length; i++){
				String attribute = (String)t.getContainerPropertyIds().toArray()[i];
					
				//Create CSSLayout and fill with action components
				if(attribute.equals(ACTIONCOMPONENTS)){
					CssLayout actionLayout = new CssLayout();
					cells[i] = actionLayout;
					
					loadActionComponents(actionLayout, em);
					
					//If the attribute is a many to many/one to many relationship,
					//set the cell to a String output of all relationships.
				}else if(relationshipColumns.containsKey(attribute)){
					try{
						cells[i] = m.getAll(relationshipColumns.get(attribute)).toString().replace("[", "").replace("]", "");
					}catch(NotAssociatedException e){
						EncryptedModel parent =  m.parent(relationshipColumns.get(attribute));
						cells[i] = parent == null ? "" : parent.toString();
					}
					
					//Set cell to the model's attribute value
				}else{
					cells[i] = em.getAsString(attribute) == null ? "" : em.getDecrypted(attribute).toString();
				}
				
				//Truncate long values
				if(cells[i] instanceof String && cells[i].toString().length() > 50){
					cells[i] = cells[i].toString().substring(0, 50) + "...";
				}
			}
			
			t.addItem(cells, em.getId());
		}
	}
	
	protected void loadActionComponents(CssLayout layout, EncryptedModel model) {
		Button btn = new Button("Details", event -> navToEdit(model));
		btn.setStyleName("quiet tiny");
		layout.addComponent(btn);
	}

	protected <T> void addTableColumn(String attribute, Class<T> type, String caption){
		t.addContainerProperty(attribute, type, "", caption, null, null);
		t.setColumnExpandRatio(attribute, 1);
	}
	
	protected void addRelationshipColumn(String identifier, Class<? extends EncryptedModel> type, String caption){
		//Finds all many to many or one to many relationships of model and type.class
		relationshipColumns.put(identifier, type);
		t.addContainerProperty(identifier, String.class, "", caption, null, null);
		t.setColumnExpandRatio(identifier, 1);
	}
	
	protected void addReportBtn(EncryptedModel model, String caption){
		Button downloadBtn = new Button("Click To Download");
		downloadBtn.setVisible(false);
		addComponent(downloadBtn);
		
		Button genBtn = new Button(caption);
		genBtn.addClickListener(e -> {
			downloadReport(model, downloadBtn);
			downloadBtn.setVisible(true);
			genBtn.setVisible(false);
		});
		addComponent(genBtn);
	}

	private void downloadReport(EncryptedModel model, Button downBtn) {
		SessionManager.openBase();
		
		CSVController reportGen = new CSVController(model);
		if(reportGen.createCSVFile()){
			Resource res = new FileResource(reportGen.getFile());
			FileDownloader downloader = new FileDownloader(res);
			downloader.extend(downBtn);
		}else{
			Notification.show("Could Not Generate Report", Notification.Type.WARNING_MESSAGE);
			downBtn.setCaption("Error");
		}
		SessionManager.closeBase();
	}

	protected abstract void navToEdit(EncryptedModel usr);
	protected abstract void setContainerProperties(Table t);

	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		SessionManager.openBase();
		populateTable();
		SessionManager.closeBase();
	}

}
