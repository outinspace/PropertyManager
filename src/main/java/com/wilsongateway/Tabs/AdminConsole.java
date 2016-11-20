package com.wilsongateway.Tabs;

import org.vaadin7.console.Console;
import org.vaadin7.console.Console.Command;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tab;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@SuppressWarnings("serial")
public class AdminConsole extends Tab {//TODO

	private Console c;
	
	public AdminConsole(SessionManager manager) {
		super("Admin Console", manager);
		
		Label heading = new Label("Admin Console");
		
		addComponent(heading);
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		initConsole();
	}

	private void initConsole() {
		if(c != null){
			this.removeComponent(c);
		}
		c = new Console();
		c.setCols(100);
		c.setRows(20);
		c.setGreeting("Welcome back " + manager.getCurrentUser().getAsString("username"));
		addCommands();
		this.addComponent(c);
	}

	private void addCommands() {
		c.addCommand("help", new Command(){

			@Override
			public Object execute(Console console, String[] argv) throws Exception {
				for(String com : console.getCommands()){
					console.println(com + " : " + console.getCommand(com).getUsage(console, null));
				}
				return null;
			}

			@Override
			public String getUsage(Console console, String[] argv) {
				return "Creates a list of all usable commands with descriptions.";
			}
			
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		initConsole();
	}
}
