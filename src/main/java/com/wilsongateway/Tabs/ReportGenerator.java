package com.wilsongateway.Tabs;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

@SuppressWarnings("serial")
public class ReportGenerator extends Tab{

	public ReportGenerator(SessionManager manager) {
		super("Report Generator", manager);
		
		initGui();
	}

	private void initGui() {
		Label heading = new Label("Report Generator");
		addComponent(heading);
		addLineBreak();
		
		
	}

	@Override
	public void enter(ViewChangeEvent event) {}
}
