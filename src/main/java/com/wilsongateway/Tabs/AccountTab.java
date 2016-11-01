package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

@SuppressWarnings("serial")
public class AccountTab extends Tab{

	public AccountTab(SessionManager manager) {
		super("Account Settings", manager);
		
		initUI();
	}

	private void initUI() {
		addComponent(new Label("Account Settings for " + manager.getCurrentUser().getString("first_name")));
		addComponent(new Button("Change Password"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
