package com.wilsongateway.Tabs;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;

@SuppressWarnings("serial")
public class EditProperty extends EditForm<Property>{
	
	public EditProperty(SessionManager manager, Property prop, boolean isEditable) {
		super(manager, prop, "Property", isEditable);
	}

	@Override
	protected void saveBtnAction() {
		try{
			Property prop;
			if(viewMode == Mode.ADD){
				prop = new Property();
				Tables.isUniqueName("name", prop, getTFValue("name"));
				prop.set("name", getTFValue("name"));
			}else{
				prop = getItem();
				if(!getTFValue("name").equals(prop.getString("name"))){
					Tables.isUniqueName("name", prop, getTFValue("name"));
					prop.set("name", getTFValue("name"));
				}
			}
			
			prop.set("street_address", getTFValue("street_address"));
			prop.set("city", getTFValue("city"));
			prop.set("state", getTFValue("state"));
			prop.set("zip", getTFValue("zip"));
			prop.save();
			
			Notification.show("Property Saved", Notification.Type.HUMANIZED_MESSAGE);
		}catch(NameUnavailableException e){
			Notification.show("Name Is Already Being Used", Notification.Type.WARNING_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Save Property", Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	protected void clearFields() {
		this.clearAllTF();
	}

	@Override
	protected void reloadData() {}

	@Override
	protected void setViewMode(Property item) {
		if(item == null){
			viewMode = Mode.ADD;
		}else{
			viewMode = Mode.VIEW;
		}
	}

	@Override
	protected void fillFields(Property item) {}

	@Override
	protected void populateLeftCol(Layout leftCol, Property item) {
		addAndFillTF("name", "Name", FontAwesome.BUILDING);//TODO add icons
		addAndFillTF("street_address", "Street Address", FontAwesome.MAP_PIN);
		addAndFillTF("city", "City");
		addAndFillTF("state", "State");
		addAndFillTF("zip", "Zip");
	}

	@Override
	protected void populateRightCol(Layout rightCol, Property item) {
		// TODO image upload
	}

}
