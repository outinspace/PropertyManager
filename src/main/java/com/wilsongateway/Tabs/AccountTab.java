package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
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
		
		addComponent(new Label(manager.getCurrentUser().getAsString("id")));
		addComponent(new Label(manager.getCurrentUser().getAsString("first_name")));
		addComponent(new Label(manager.getCurrentUser().getAsString("last_name")));
		addComponent(new Label(manager.getCurrentUser().getAsString("position")));
		
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
