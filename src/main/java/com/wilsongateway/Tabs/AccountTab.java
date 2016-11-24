package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;
import com.wilsongateway.SubPanels.ChangePassword;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class AccountTab extends Tab{//TODO

	public AccountTab(SessionManager manager) {
		super("Account Settings", manager);
		initUI();
		
	}

	private void initUI() {
		addComponent(new Label("Account Settings for " + manager.getCurrentUser().getDecrypted("first_name")));
		addLineBreak();
		
		ChangePassword passWindow = new ChangePassword(manager.getCurrentUser());
		
		Button changePassBtn = new Button("Change Password", e -> manager.addWindow(passWindow));
		addComponent(changePassBtn);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
