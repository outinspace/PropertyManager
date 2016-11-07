package com.wilsongateway.Tabs;

import org.vaadin7.console.Console;
import org.vaadin7.console.Console.Command;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

@SuppressWarnings("serial")
public class AdminConsole extends Tab{

	public AdminConsole(SessionManager manager) {
		super("Admin Console", manager);
		
		Console c = new Console();
		c.addCommand("help", new Command(){

			@Override
			public Object execute(Console console, String[] argv) throws Exception {
				console.println("works");
				return null;
			}

			@Override
			public String getUsage(Console console, String[] argv) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		addComponent(c);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
