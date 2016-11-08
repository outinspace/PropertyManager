package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

@SuppressWarnings("serial")
public class ErrorTab extends Tab{

	public ErrorTab(SessionManager manager, String errorMsg) {
		super("ERROR", manager);
		
		addComponent(new Label("There was a problem loading this tab: " + errorMsg));//TODO big logo
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

}
