package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.SubPanels.ChangePassword;
import com.wilsongateway.SubPanels.ChangeTheme;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class AccountTab extends Tab{//TODO

	private static final long serialVersionUID = -632039196777446599L;

	public AccountTab(SessionManager manager) {
		super("Account Settings", manager);
		
		addComponent(new Label("Account Settings for " + manager.getCurrentUser().getAsString("first_name")));
		addLineBreak();
		
		Panel info = new Panel();
		addComponent(info);
		
		VerticalLayout content = new VerticalLayout();
		content.setSpacing(true);
		content.setMargin(true);
		info.setContent(content);
		
		content.addComponent(new Label("ID: " + manager.getCurrentUser().getAsString("id")));
		content.addComponent(new Label("Name: " + manager.getCurrentUser().getAsString("first_name") 
				+ " " + manager.getCurrentUser().getAsString("last_name")));
		content.addComponent(new Label("Postition: " + manager.getCurrentUser().getAsString("position")));
		
		Button changePassBtn = new Button("Change Password", 
				e -> manager.addWindow(new ChangePassword(manager.getCurrentUser())));
		addComponent(changePassBtn);
		
		Button changeThemeBtn = new Button("Change Theme",
				e -> manager.addWindow(new ChangeTheme(manager)));
		addComponent(changeThemeBtn);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
