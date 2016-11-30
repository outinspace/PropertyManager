package com.wilsongateway.Tabs;

import org.javalite.activejdbc.Base;
import org.vaadin7.console.Console;
import org.vaadin7.console.Console.Command;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
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
public class AdminConsole extends Tab {//TODO

	private static final long serialVersionUID = 3750064176744522864L;
	
	private Console c;
	private Label sessionsLabel;
	
	public AdminConsole(SessionManager manager) {
		super("Admin Console", manager);
		
		addComponent(new Label("Admin Console"));
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		initConsole();
		//initStatsSection();
	}

	private void initStatsSection() {
		addComponent(new Label("Server Status"));
		addComponent(new Label("<hr />",ContentMode.HTML));
		
		sessionsLabel = new Label();
	}
	
	private void refreshStatsLabels(){
//		sessionsLabel.setValue(VaadinSession.getAllSessions(null));
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

			/**
			 * 
			 */
			private static final long serialVersionUID = -3889022088061334208L;

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
		
		c.addCommand("close-db-connection", new Command(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -5829924709697163435L;

			@Override
			public Object execute(Console console, String[] argv) throws Exception {
				console.println("Closing database connection for current session.");
				SessionManager.openBase();
				Base.connection().close();
				return null;
			}

			@Override
			public String getUsage(Console console, String[] argv) {
				return "Closes database connection in current session for testing purposes.";
			}
			
		});
		
		c.addCommand("close-db-connection", new Command(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 8835682017841009441L;

			@Override
			public Object execute(Console console, String[] argv) throws Exception {
				console.println("Closing database connection for current session.");
				Base.connection().close();
				return null;
			}

			@Override
			public String getUsage(Console console, String[] argv) {
				return "Closes database connection in current session for testing purposes.";
			}
			
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		initConsole();
	}
}
