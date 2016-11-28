package com.wilsongateway.Forms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.associations.NotAssociatedException;

import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
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
public abstract class ViewAllForm extends Tab{

	private static final long serialVersionUID = -4327048693575273337L;
	
	private Table t;
	protected LazyList<? extends Model> models;
	private Map<String, Class<? extends EncryptedModel>> relationshipColumns = new HashMap<String, Class<? extends EncryptedModel>>();
	
	public static final String ACTIONCOMPONENTS = "actionComponents";
	public static final String RELATIONSHIP = "relationship";
	
	private HorizontalLayout topBar;
	
	public ViewAllForm(SessionManager manager, String pluralItemName, boolean isEditable) {
		super((isEditable ? "Edit" : "View") + " All " + pluralItemName, manager);
		SessionManager.openBase();
		
		Label heading = new Label((isEditable ? "Edit" : "View") +  " All " + pluralItemName);
		
		addComponent(heading);
		addLineBreak();
		
		topBar = new HorizontalLayout();
		topBar.setSpacing(true);
		addComponent(topBar);
		
		createTable();
		models = getModels();
		populateTable();
		
		SessionManager.closeBase();
	}
	
	private void createTable(){
//		if(t != null){
//			removeComponent(t);
//		}
		
		t = new Table(null);
		t.setWidth("100%");
		//TODO Search Box
		t.addContainerProperty(ACTIONCOMPONENTS, CssLayout.class, "", "", null, null);
		t.setColumnExpandRatio(ACTIONCOMPONENTS, 0);
		setContainerProperties(t);
		addComponent(t);
	}

	protected void populateTable() {
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
					
					fillActionLayout(actionLayout, em);
					
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
					cells[i] = em.getAsString(attribute);
				}
				
				//Truncate long values
//				if(cells[i] instanceof String && cells[i].toString().length() > 50){
//					cells[i] = cells[i].toString().substring(0, 50) + "...";
//				}
			}
			
			t.addItem(cells, em.getId());
		}
	}
	
	protected void fillActionLayout(Layout actionLayout, EncryptedModel model) {
		Button btn = new Button("", event -> navToEdit(model));
		btn.setIcon(FontAwesome.EDIT);
		btn.setStyleName("quiet tiny");
		actionLayout.addComponent(btn);
	}

	protected <T> void addTableColumn(String attribute, Class<T> type, String caption, float expandRatio){
		t.addContainerProperty(attribute, type, "", caption, null, null);
		t.setColumnExpandRatio(attribute, expandRatio);
	}
	
	protected void addRelationshipColumn(String identifier, Class<? extends EncryptedModel> type, String caption, float expandRatio){
		//Finds all many to many or one to many relationships of model and type.class
		relationshipColumns.put(identifier, type);
		t.addContainerProperty(identifier, String.class, "", caption, null, null);
		t.setColumnExpandRatio(identifier, expandRatio);
	}
	
	protected void addSearchBox(){//TODO
		topBar.addComponent(new Label("Search Where"));
		
		ComboBox fieldToSearch = new ComboBox();
		fieldToSearch.addItems(t.getContainerPropertyIds());
		fieldToSearch.setStyleName("tiny");
		topBar.addComponent(fieldToSearch);
		
		topBar.addComponent(new Label("Equals"));
		
		TextField searchField = new TextField();
		searchField.setStyleName("tiny");
		topBar.addComponent(searchField);
		
		searchField.addTextChangeListener(new TextChangeListener(){

			private static final long serialVersionUID = 715380955879471784L;

			@Override
			public void textChange(TextChangeEvent event) {
				String query = searchField.getValue();
				String attribute = (String) fieldToSearch.getValue();
				
				UI.getCurrent().access(new Runnable(){

					@Override
					public void run() {
						for(Model m : models){
							EncryptedModel em = (EncryptedModel) m;
							if(!em.getDecrypted(attribute).toString().startsWith(query)){
								models.remove(m);
							}
						}
					}
					
				});
				
				refresh(false);
			}
			
		});
	}
	
	protected void addRefreshButton(){
		Button refresh = new Button("",e -> {
			refresh();
			UI.getCurrent().access(() -> Notification.show("Refreshed", Notification.Type.TRAY_NOTIFICATION));
		});
		
		refresh.setStyleName("tiny");
		refresh.setIcon(FontAwesome.REFRESH);
		topBar.addComponent(refresh);
	}
	
	protected void addReportBtn(EncryptedModel model){
		Button downloadBtn = new Button("Click To Download");
		downloadBtn.setVisible(false);
		downloadBtn.setStyleName("tiny");
		topBar.addComponent(downloadBtn);
		
		Button genBtn = new Button("Report");
		genBtn.setIcon(FontAwesome.DOWNLOAD);
		genBtn.setStyleName("tiny");
		genBtn.addClickListener(e -> {
			downloadReport(model, downloadBtn);
			downloadBtn.setVisible(true);
			genBtn.setVisible(false);
		});
		topBar.addComponent(genBtn);
	}

	private void downloadReport(EncryptedModel model, Button downBtn) {
		SessionManager.openBase();
		
		CSVController reportGen = new CSVController(model, models);
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
	
	protected void refresh(){
		refresh(true);
	}
	
	protected void refresh(boolean reloadModels){
		UI.getCurrent().access(() -> {
			SessionManager.openBase();
				if(reloadModels){
					models = getModels();
				}
				populateTable();
			SessionManager.closeBase();
		});
	}

	protected HorizontalLayout getTopBar(){
		return topBar;
	}
	
	protected void reloadData(){}

	protected abstract void navToEdit(EncryptedModel usr);
	protected abstract void setContainerProperties(Table t);
	protected abstract LazyList<? extends Model> getModels();
	
	@Override
	public void enter(ViewChangeEvent event) {
		//Refresh
		refresh();
		reloadData();
	}

}
