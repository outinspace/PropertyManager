package com.wilsongateway.Tabs;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.CustomComponents.EndlessComboBox;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Exceptions.NoTabsSelectedException;
import com.wilsongateway.Forms.EditForm;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Group;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class EditGroup extends EditForm<Group>{

	private static final long serialVersionUID = 7400513252328247255L;
	
	private EndlessComboBox<TabType> comboSelect;
	
	public EditGroup(SessionManager manager, Group item, boolean isEditable) {
		super(manager, item, "Group", isEditable);
	}

	@Override
	protected void saveModel(Group grp) {
		try{
			//Checks tabs
			checkValidTabs();
			
			if(viewMode == Mode.ADD){
				//Checks name is unique and saves
				grp = createAndSetGroup();
			}else if(!getTFValue("name").equals(grp.getAsString("name"))){
				checkAndSetName();
			}
			
			grp.set("tabs", comboSelect.getValues().toString());
			grp.save();
			
			Notification.show("Group Saved", Notification.Type.HUMANIZED_MESSAGE);
		}catch(NameUnavailableException e){
			Notification.show("That Group Name Is Unavailable", Notification.Type.ERROR_MESSAGE);
		}catch(NoTabsSelectedException e){
			Notification.show("No Tabs Selected", Notification.Type.ERROR_MESSAGE);
		}catch(Exception e){
			Notification.show("Unable To Save Group", Notification.Type.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private Group createAndSetGroup() throws NameUnavailableException{
		String name = getTFValue("name").replace(",", "");
		Group g = Tables.GROUP.findFirst("name = (?)", name);
		if(g == null && name.length() > 0){
			g = Tables.GROUP.create("name", name);
			return g;
		}else{
			throw new NameUnavailableException();
		}
	}
	
	private void checkAndSetName() throws NameUnavailableException{
		String name = getTFValue("name").replace(",", "");
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

	@Override
	protected void clearFields() {
		clearAllTF();
		comboSelect.clear();
	}

	@Override
	protected void reloadData() {}

	@Override
	protected void setViewMode(Group g) {
		if(g == null){
			viewMode = Mode.ADD;
		}else{
			viewMode = Mode.VIEW;
		}
	}

	@Override
	protected void populateLeftCol(Layout leftCol, Group g) {
		addAndFillTF("name", "Group Name", FontAwesome.GROUP).setRequired(true);
		
		comboSelect = new EndlessComboBox<TabType>(new ArrayList<TabType>(Arrays.asList(TabType.values())), g == null ? null : g.getTabs());
		comboSelect.setCaption("Select Tabs");
		leftCol.addComponent(comboSelect);
		addCustomComponent(comboSelect);
	}

	@Override
	protected void populateRightCol(Layout rightCol, Group item) {}

}
