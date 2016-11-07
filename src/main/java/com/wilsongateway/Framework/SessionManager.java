package com.wilsongateway.Framework;

import java.sql.Connection;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;

import org.javalite.activejdbc.Base;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.wilsongateway.Framework.Tab.TabType;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@SuppressWarnings("serial")
@Theme("pmTheme")
public class SessionManager extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SessionManager.class)
	public static class Servlet extends VaadinServlet {
		
		private static final String HOSTNAME = System.getProperty("RDS_HOSTNAME") == null ? "localhost" : System.getProperty("RDS_HOSTNAME");
		private static final String PORT = System.getProperty("RDS_PORT") == null ? "3306" : System.getProperty("RDS_PORT");
		private static final String DBNAME = System.getProperty("RDS_DBNAME") == null ? "pm_database" : System.getProperty("RDS_DBNAME");
		private static final String USERNAME = System.getProperty("RDS_USERNAME") == null ? "root" : System.getProperty("RDS_USERNAME");
		private static final String PASSWORD = System.getProperty("RDS_PASSWORD") == null ? "databaseserver" : System.getProperty("RDS_PASSWORD");
		
		private static HikariDataSource datasource;
		
		public Servlet(){
			//Create connection pool
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DBNAME);
			config.setUsername(USERNAME);
			config.setPassword(PASSWORD);
			config.setDriverClassName("com.mysql.jdbc.Driver");
			config.addDataSourceProperty("cachePrepStmts", "true");
			config.addDataSourceProperty("prepStmtCacheSize", "250");
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
			config.setConnectionTestQuery("/* ping */");

			datasource = new HikariDataSource(config);
		}
		
		public static HikariDataSource getDatasource(){
			return datasource;
		}
	}

	//Dev Database login
	static final String SQLuser = "wilsongatewaydb";
	static final String SQLpassword = "databaseserver";
	
	//View ENUMS
	public static final String LOGINVIEW = "";
	public static final String DASHBOARDVIEW = "dashboardview";
	
	//Navigation
	private Navigator nav;
	
	//Current
	private DashboardView currentDash;
	
	@Override
	protected void init(VaadinRequest request) {
		//Init database connection
		ensureBase();
		
		nav = new Navigator(this, this);
		LoginView login = new LoginView(this);
		nav.addView(LOGINVIEW, login);
		nav.setErrorView(login);
		
		//Navigate to LoginView
		nav.navigateTo(LOGINVIEW);
		
		//Create database entries for testing purposes
		createTestEntries();
		
		//TODO re-enable, removed for debugging
		//addDetachListener(new SessionCleanup(connections));
	}
	
	private void createTestEntries() {
		System.out.println("Creating Test Entries");
		User u = Tables.USER.findFirst("username = 'admin'");
		if(u == null){
			u = new User();
		}
		u.setEncrypted("username", "admin");
		u.setEncrypted("password", "Password1");
		System.out.println("Saving admin");
		u.save();
		
		//Create group with one tab
		Group g1 = Tables.GROUP.findFirst("name = 'Admin'");
		if(g1 == null){
			Tables.GROUP.createIt("name", "Admin");
			g1 = Tables.GROUP.findFirst("name = 'Admin'");
		}
		ArrayList<TabType> tabs = new ArrayList<TabType>();
		for(TabType t : TabType.values()){
			tabs.add(t);
		}
		g1.setTabs(tabs);
		g1.save();
		
		//add group to user
		u.addGroup(g1);
		u.save();
	}
	
	public void ensureBase(){
		if(!Base.hasConnection()){
			Base.open(Servlet.getDatasource());
		}
	}
	
	public void closeBase(){
		//connections.remove(Base.connection());
		Base.close(true);
	}
	
	public void setCurrentUser(User temp) {
		VaadinSession.getCurrent().setAttribute(User.class, temp);
	}
	
	public void setCurrentDash(DashboardView dash){
		this.currentDash = dash;
	}
	
	public DashboardView getDash(){
		return currentDash;
	}
	
	public User getCurrentUser(){
		return VaadinSession.getCurrent().getAttribute(User.class);
	}
	
	public Navigator getNav(){
		return nav;
	}
}